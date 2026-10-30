"""
Pytest unit tests for VetTrack Data Cleaning Pipeline.
Covers text normalization, date parsing, phone standardisation,
species synonym taxonomy, and rejection auditing.
"""

import pytest
from datetime import date, timedelta
import pandas as pd
from src.clean import (
    normalize_text,
    normalize_email,
    standardize_phone,
    parse_date,
    map_species,
    clean_owners,
    clean_animals
)


class TestTextNormalization:

    def test_strip_and_collapse_whitespace(self):
        assert normalize_text("   John    Doe  ") == "John Doe"
        assert normalize_text("Dr.  Sarah   Jenkins ") == "Dr. Sarah Jenkins"

    def test_handles_none_and_empty(self):
        assert normalize_text(None) == ""
        assert normalize_text("") == ""


class TestEmailNormalization:

    def test_valid_emails_lowercased(self):
        assert normalize_email("USER@EXAMPLE.COM") == "user@example.com"
        assert normalize_email("  Doctor.House@Hospital.Org  ") == "doctor.house@hospital.org"

    def test_empty_email_allowed(self):
        assert normalize_email(None) is None
        assert normalize_email("") is None

    def test_invalid_email_raises_error(self):
        with pytest.raises(ValueError, match="Invalid email format"):
            normalize_email("not-an-email")
        with pytest.raises(ValueError, match="Invalid email format"):
            normalize_email("user@domain")


class TestPhoneStandardization:

    def test_valid_formats_to_e164_style(self):
        assert standardize_phone("(555) 234-5678") == "+1-555-234-5678"
        assert standardize_phone("555.234.5678") == "+1-555-234-5678"
        assert standardize_phone("+1 555-234-5678") == "+1-555-234-5678"
        assert standardize_phone("5552345678") == "+1-555-234-5678"

    def test_phone_with_letters_rejected(self):
        with pytest.raises(ValueError, match="contains alphabetical characters"):
            standardize_phone("555-CALL-VET")

    def test_wrong_length_rejected(self):
        with pytest.raises(ValueError, match="must contain exactly 10 digits"):
            standardize_phone("12345")


class TestDateParsing:

    def test_multiple_date_formats(self):
        expected = date(2023, 4, 15)
        assert parse_date("2023-04-15") == expected
        assert parse_date("15/04/2023") == expected
        assert parse_date("15 Apr 2023") == expected
        assert parse_date("2023/04/15") == expected

    def test_future_dates_rejected_when_flagged(self):
        future_date = (date.today() + timedelta(days=30)).strftime("%Y-%m-%d")
        with pytest.raises(ValueError, match="Date cannot be in the future"):
            parse_date(future_date, disallow_future=True)

    def test_invalid_unparseable_date_raises_error(self):
        with pytest.raises(ValueError, match="Unable to parse date string"):
            parse_date("not-a-valid-date")


class TestSpeciesMapping:

    def test_synonym_mappings(self):
        assert map_species("canine") == "Dog"
        assert map_species("DOG") == "Dog"
        assert map_species("puppy") == "Dog"
        assert map_species("feline") == "Cat"
        assert map_species("kitten") == "Cat"
        assert map_species("bovine") == "Cattle"
        assert map_species("cow") == "Cattle"
        assert map_species("hen") == "Poultry"
        assert map_species("chicken") == "Poultry"

    def test_unknown_species_rejected(self):
        with pytest.raises(ValueError, match="Unknown or unsupported species"):
            map_species("Dinosaur")


class TestPipelineCleaning:

    def test_clean_owners_dataframe(self):
        raw_data = pd.DataFrame([
            {"raw_name": "  Alice Smith ", "raw_phone": "555-123-4567", "raw_email": "ALICE@VET.ORG", "raw_city": "Austin"},
            {"raw_name": "alice smith", "raw_phone": "555.123.4567", "raw_email": "alice@vet.org", "raw_city": "austin"}, # Duplicate phone
            {"raw_name": "", "raw_phone": "555-999-8888", "raw_email": "no@name.org", "raw_city": "Dallas"}, # Missing name
        ])

        cleaned, rejected = clean_owners(raw_data)
        assert len(cleaned) == 1
        assert len(rejected) == 2
        assert cleaned.iloc[0]["name"] == "Alice Smith"
        assert cleaned.iloc[0]["phone"] == "+1-555-123-4567"
        assert cleaned.iloc[0]["email"] == "alice@vet.org"

    def test_clean_animals_dataframe(self):
        raw_data = pd.DataFrame([
            {"owner_phone": "555-111-2222", "animal_name": "Rex", "raw_species": "canine", "breed": "Husky", "raw_birth_date": "2022-01-15", "sex": "Male"},
            {"owner_phone": "555-111-2222", "animal_name": "Chirpy", "raw_species": "hen", "breed": "Leghorn", "raw_birth_date": "10/05/2023", "sex": "Female"},
            {"owner_phone": "555-111-2222", "animal_name": "FutureDog", "raw_species": "dog", "breed": "Mixed", "raw_birth_date": "2030-01-01", "sex": "Male"}, # Future
            {"owner_phone": "555-111-2222", "animal_name": "Monster", "raw_species": "Dragon", "breed": "Fire", "raw_birth_date": "2021-01-01", "sex": "Male"}, # Bad species
        ])

        cleaned, rejected = clean_animals(raw_data)
        assert len(cleaned) == 2
        assert len(rejected) == 2
        assert list(cleaned["species"]) == ["Dog", "Poultry"]
