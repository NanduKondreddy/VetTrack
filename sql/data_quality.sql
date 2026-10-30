-- ============================================================================
-- VetTrack: Data Quality & Integrity Queries (data_quality.sql)
-- Validating completeness, uniqueness, referential integrity, and business rules.
-- ============================================================================

-- Query 9: Count of null or blank required fields across all core tables
-- Audits table cleanliness.
SELECT 
    'owners' AS table_name,
    COUNT(*) FILTER (WHERE name IS NULL OR TRIM(name) = '') AS null_name_count,
    COUNT(*) FILTER (WHERE phone IS NULL OR TRIM(phone) = '') AS null_phone_count,
    COUNT(*) FILTER (WHERE city IS NULL OR TRIM(city) = '') AS null_city_count,
    0 AS null_birthdate_count
FROM owners

UNION ALL

SELECT 
    'animals' AS table_name,
    COUNT(*) FILTER (WHERE name IS NULL OR TRIM(name) = '') AS null_name_count,
    COUNT(*) FILTER (WHERE species IS NULL OR TRIM(species) = '') AS null_phone_count,
    0 AS null_city_count,
    COUNT(*) FILTER (WHERE birth_date IS NULL) AS null_birthdate_count
FROM animals;


-- Query 10: Identify duplicate owners based on phone or lowercase email
-- Finds potential duplicate client profiles needing deduplication.
SELECT 
    phone,
    LOWER(email) AS normalized_email,
    COUNT(*) AS duplicate_count,
    ARRAY_AGG(id) AS owner_ids,
    ARRAY_AGG(name) AS owner_names
FROM owners
GROUP BY phone, LOWER(email)
HAVING COUNT(*) > 1;


-- Query 11: Identify orphan vaccinations (pointing to non-existent batches or animals)
-- Detects referential integrity lapses or dangling keys.
SELECT 
    v.id AS vaccination_id,
    v.animal_id,
    v.batch_id,
    v.given_on,
    CASE 
        WHEN a.id IS NULL AND b.id IS NULL THEN 'Orphan: Missing Animal & Batch'
        WHEN a.id IS NULL THEN 'Orphan: Missing Animal'
        WHEN b.id IS NULL THEN 'Orphan: Missing Batch'
        ELSE 'Valid'
    END AS integrity_status
FROM vaccinations v
LEFT JOIN animals a ON v.animal_id = a.id
LEFT JOIN vaccine_batches b ON v.batch_id = b.id
WHERE a.id IS NULL OR b.id IS NULL;


-- Query 12: Detect impossible domain values violating business rules
-- Flags future animal birth dates, negative stock, and next due date earlier than given date.
SELECT 
    'animals' AS entity_type,
    id AS record_id,
    'Future birth date: ' || birth_date::TEXT AS violation_reason
FROM animals
WHERE birth_date > CURRENT_DATE

UNION ALL

SELECT 
    'vaccine_batches' AS entity_type,
    id AS record_id,
    'Negative inventory stock: ' || quantity_remaining::TEXT AS violation_reason
FROM vaccine_batches
WHERE quantity_remaining < 0

UNION ALL

SELECT 
    'vaccinations' AS entity_type,
    id AS record_id,
    'Next due date (' || next_due_on::TEXT || ') earlier than given date (' || given_on::TEXT || ')' AS violation_reason
FROM vaccinations
WHERE next_due_on < given_on;


-- Query 13: Row counts and audit summary for data imports
-- Provides pre/post migration metrics and error rate tracking.
SELECT 
    'Production Records' AS metric_group,
    (SELECT COUNT(*) FROM owners) AS total_owners,
    (SELECT COUNT(*) FROM animals) AS total_animals,
    (SELECT COUNT(*) FROM vaccinations) AS total_vaccinations,
    (SELECT COUNT(*) FROM visits) AS total_visits,
    (SELECT COUNT(*) FROM import_errors) AS total_rejected_rows;
