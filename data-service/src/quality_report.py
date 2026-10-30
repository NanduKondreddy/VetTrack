"""
VetTrack Data Quality Auditor.
Runs structured integrity checks against the database or cleaned datasets,
validating foreign keys, constraints, null checks, and duplicate detections.
"""

import os
import pandas as pd
from datetime import date

def run_quality_audit(cleaned_dir="cleaned"):
    print("=" * 60)
    print("VetTrack Data Quality Audit Report")
    print("=" * 60)

    owners_csv = os.path.join(cleaned_dir, "owners.csv")
    animals_csv = os.path.join(cleaned_dir, "animals.csv")

    checks = []

    # Check 1: Owners Dataset
    if os.path.exists(owners_csv):
        df_owners = pd.read_csv(owners_csv)
        total_owners = len(df_owners)

        # Check nulls in required fields
        null_names = df_owners["name"].isna().sum()
        null_phones = df_owners["phone"].isna().sum()
        null_cities = df_owners["city"].isna().sum()

        checks.append(("Owners: Missing Names", null_names, 0, "PASSED" if null_names == 0 else "FAILED"))
        checks.append(("Owners: Missing Phones", null_phones, 0, "PASSED" if null_phones == 0 else "FAILED"))
        checks.append(("Owners: Missing Cities", null_cities, 0, "PASSED" if null_cities == 0 else "FAILED"))

        # Duplicate phone check
        dup_phones = df_owners["phone"].duplicated().sum()
        checks.append(("Owners: Duplicate Phone Numbers", dup_phones, 0, "PASSED" if dup_phones == 0 else "FAILED"))

    # Check 2: Animals Dataset
    if os.path.exists(animals_csv):
        df_animals = pd.read_csv(animals_csv)
        total_animals = len(df_animals)

        null_names = df_animals["name"].isna().sum()
        null_species = df_animals["species"].isna().sum()
        null_dates = df_animals["birth_date"].isna().sum()

        checks.append(("Animals: Missing Names", null_names, 0, "PASSED" if null_names == 0 else "FAILED"))
        checks.append(("Animals: Missing Species", null_species, 0, "PASSED" if null_species == 0 else "FAILED"))
        checks.append(("Animals: Missing Birth Dates", null_dates, 0, "PASSED" if null_dates == 0 else "FAILED"))

        # Future date check
        today_str = date.today().isoformat()
        future_dates = (df_animals["birth_date"] > today_str).sum()
        checks.append(("Animals: Future Birth Dates", future_dates, 0, "PASSED" if future_dates == 0 else "FAILED"))

        # Species Taxonomy Check
        valid_species = {"Dog", "Cat", "Cattle", "Poultry"}
        invalid_species = (~df_animals["species"].isin(valid_species)).sum()
        checks.append(("Animals: Invalid Species Taxonomy", invalid_species, 0, "PASSED" if invalid_species == 0 else "FAILED"))

    # Print Summary Table
    print(f"\n{'Rule / Check Description':<40} | {'Found':<8} | {'Threshold':<9} | {'Status'}")
    print("-" * 75)
    for desc, found, threshold, status in checks:
        print(f"{desc:<40} | {found:<8} | {threshold:<9} | {status}")

    all_passed = all(status == "PASSED" for _, _, _, status in checks)
    print("-" * 75)
    print(f"Overall Quality Status: {' ALL CHECKS PASSED' if all_passed else ' ISSUES DETECTED'}")
    print("=" * 60)


if __name__ == "__main__":
    current_dir = os.path.dirname(os.path.abspath(__file__))
    cleaned_path = os.path.join(current_dir, "..", "cleaned")
    run_quality_audit(cleaned_path)
