"""
VetTrack Data Cleaning & Transformation Pipeline.
Handles text normalization, flexible multi-format date parsing, phone standardisation,
species synonym mapping, and comprehensive rejection auditing.
"""

import argparse
import re
from datetime import date, datetime
from typing import Dict, List, Optional, Tuple
import pandas as pd

# Supported date format patterns
DATE_FORMATS = [
    "%Y-%m-%d",
    "%d/%m/%Y",
    "%m/%d/%Y",
    "%d %b %Y",
    "%d %B %Y",
    "%d-%m-%Y",
    "%Y/%m/%d",
    "%m-%d-%Y",
]

# Species taxonomy mapping
SPECIES_SYNONYMS: Dict[str, str] = {
    # Canine
    "dog": "Dog",
    "dogs": "Dog",
    "canine": "Dog",
    "canines": "Dog",
    "pup": "Dog",
    "puppy": "Dog",
    "hound": "Dog",
    # Feline
    "cat": "Cat",
    "cats": "Cat",
    "feline": "Cat",
    "felines": "Cat",
    "kitten": "Cat",
    # Bovine / Cattle
    "cow": "Cattle",
    "cows": "Cattle",
    "cattle": "Cattle",
    "bovine": "Cattle",
    "calf": "Cattle",
    "bull": "Cattle",
    "steer": "Cattle",
    # Avian / Poultry
    "poultry": "Poultry",
    "chicken": "Poultry",
    "chickens": "Poultry",
    "hen": "Poultry",
    "hens": "Poultry",
    "rooster": "Poultry",
    "avian": "Poultry",
    "fowl": "Poultry",
}


def normalize_text(val: Optional[str]) -> str:
    """Strip whitespace and normalize spaces."""
    if val is None or pd.isna(val):
        return ""
    text = str(val).strip()
    return re.sub(r"\s+", " ", text)


def normalize_email(val: Optional[str]) -> Optional[str]:
    """Lowercase and validate email address."""
    if val is None or pd.isna(val):
        return None
    email = str(val).strip().lower()
    if not email:
        return None
    # Basic RFC compliant email regex
    if not re.match(r"^[^@\s]+@[^@\s]+\.[^@\s]+$", email):
        raise ValueError(f"Invalid email format: '{val}'")
    return email


def standardize_phone(val: Optional[str]) -> str:
    """Standardize phone numbers to E.164-style '+1-XXX-XXX-XXXX' format."""
    if val is None or pd.isna(val):
        raise ValueError("Missing phone number")

    raw = str(val).strip()
    # Check for invalid letters in phone
    if re.search(r"[a-zA-Z]", raw):
        raise ValueError(f"Phone number contains alphabetical characters: '{val}'")

    # Extract all digits
    digits = re.sub(r"\D", "", raw)

    # Remove leading 1 if 11 digits
    if len(digits) == 11 and digits.startswith("1"):
        digits = digits[1:]

    if len(digits) != 10:
        raise ValueError(f"Phone number must contain exactly 10 digits, found {len(digits)}: '{val}'")

    return f"+1-{digits[:3]}-{digits[3:6]}-{digits[6:]}"


def parse_date(val: Optional[str], disallow_future: bool = True) -> date:
    """Parse dates across multiple common formats and check bounds."""
    if val is None or pd.isna(val):
        raise ValueError("Missing date value")

    raw = str(val).strip()
    parsed_date = None

    for fmt in DATE_FORMATS:
        try:
            parsed_date = datetime.strptime(raw, fmt).date()
            break
        except ValueError:
            continue

    if parsed_date is None:
        raise ValueError(f"Unable to parse date string with known formats: '{val}'")

    if disallow_future and parsed_date > date.today():
        raise ValueError(f"Date cannot be in the future: {parsed_date} (today is {date.today()})")

    return parsed_date


def map_species(val: Optional[str]) -> str:
    """Map informal species names and synonyms to standard categories."""
    if val is None or pd.isna(val):
        raise ValueError("Missing species value")

    cleaned = str(val).strip().lower()
    if cleaned in SPECIES_SYNONYMS:
        return SPECIES_SYNONYMS[cleaned]

    raise ValueError(f"Unknown or unsupported species: '{val}'")


def clean_owners(df: pd.DataFrame) -> Tuple[pd.DataFrame, pd.DataFrame]:
    """Clean and validate legacy owners dataset."""
    cleaned_rows = []
    rejected_rows = []
    seen_phones = set()

    for idx, row in df.iterrows():
        raw_row_str = row.to_json()
        row_num = idx + 2  # 1-indexed including header

        try:
            name = normalize_text(row.get("raw_name", row.get("name")))
            if not name:
                raise ValueError("Missing required owner name")

            phone = standardize_phone(row.get("raw_phone", row.get("phone")))

            email = normalize_email(row.get("raw_email", row.get("email")))

            city = normalize_text(row.get("raw_city", row.get("city")))
            if not city:
                raise ValueError("Missing required owner city")

            # Duplicate check
            if phone in seen_phones:
                raise ValueError(f"Duplicate owner detected with phone: {phone}")

            seen_phones.add(phone)
            cleaned_rows.append({
                "name": name,
                "phone": phone,
                "email": email or "",
                "city": city.title(),
            })

        except Exception as e:
            rejected_rows.append({
                "row_number": row_num,
                "reason": str(e),
                "raw_row": raw_row_str
            })

    return pd.DataFrame(cleaned_rows), pd.DataFrame(rejected_rows)


def clean_animals(df: pd.DataFrame) -> Tuple[pd.DataFrame, pd.DataFrame]:
    """Clean and validate legacy animals dataset."""
    cleaned_rows = []
    rejected_rows = []

    for idx, row in df.iterrows():
        raw_row_str = row.to_json()
        row_num = idx + 2

        try:
            owner_phone = standardize_phone(row.get("owner_phone"))

            name = normalize_text(row.get("animal_name", row.get("name")))
            if not name:
                raise ValueError("Missing required animal name")

            species = map_species(row.get("raw_species", row.get("species")))

            breed = normalize_text(row.get("breed")) or "Unknown"

            birth_date = parse_date(row.get("raw_birth_date", row.get("birth_date")), disallow_future=True)

            sex_raw = normalize_text(row.get("sex")).capitalize()
            if sex_raw not in ["Male", "Female", "Mixed", "Unknown"]:
                sex_raw = "Unknown"

            cleaned_rows.append({
                "owner_phone": owner_phone,
                "name": name,
                "species": species,
                "breed": breed,
                "birth_date": birth_date.isoformat(),
                "sex": sex_raw
            })

        except Exception as e:
            rejected_rows.append({
                "row_number": row_num,
                "reason": str(e),
                "raw_row": raw_row_str
            })

    return pd.DataFrame(cleaned_rows), pd.DataFrame(rejected_rows)


def run_clean_pipeline(input_path: str, output_path: str, data_type: str = "owners"):
    print(f"==================================================")
    print(f"VetTrack Data Cleaning Pipeline: {data_type.upper()}")
    print(f"Input : {input_path}")
    print(f"Output: {output_path}")
    print(f"==================================================")

    df_raw = pd.read_csv(input_path)
    total_in = len(df_raw)

    if data_type.lower() == "owners":
        df_clean, df_rejected = clean_owners(df_raw)
    elif data_type.lower() == "animals":
        df_clean, df_rejected = clean_animals(df_raw)
    else:
        raise ValueError(f"Unsupported data type: {data_type}")

    import os
    if os.path.dirname(output_path):
        os.makedirs(os.path.dirname(output_path), exist_ok=True)

    # Save cleaned
    df_clean.to_csv(output_path, index=False)

    print(f"Summary:")
    print(f" - Rows Read     : {total_in}")
    print(f" - Rows Cleaned  : {len(df_clean)}")
    print(f" - Rows Rejected : {len(df_rejected)}")

    if not df_rejected.empty:
        print("\nRejection Breakdown by Reason:")
        reason_counts = df_rejected["reason"].value_counts()
        for reason, count in reason_counts.items():
            print(f"   [{count}x] {reason}")

    return df_clean, df_rejected


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="VetTrack Data Cleaner")
    parser.add_argument("--in", dest="input_file", required=True, help="Input raw CSV path")
    parser.add_argument("--out", dest="output_file", required=True, help="Output cleaned CSV path")
    parser.add_argument("--type", dest="data_type", default="owners", choices=["owners", "animals"], help="Dataset schema type")

    args = parser.parse_args()
    run_clean_pipeline(args.input_file, args.output_file, args.data_type)
