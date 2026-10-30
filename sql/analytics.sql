-- ============================================================================
-- VetTrack: Analytics Queries (analytics.sql)
-- Multi-table joins, aggregations, date parts, and advanced window functions.
-- ============================================================================

-- Query 1: Animals per owner, only owners with more than 2 animals
-- Uses JOIN, GROUP BY, and HAVING to isolate multi-animal farms and breeders.
SELECT 
    o.id AS owner_id,
    o.name AS owner_name,
    o.phone AS owner_phone,
    o.city,
    COUNT(a.id) AS total_animals
FROM owners o
JOIN animals a ON o.id = a.owner_id
GROUP BY o.id, o.name, o.phone, o.city
HAVING COUNT(a.id) > 2
ORDER BY total_animals DESC;


-- Query 2: Vaccinations administered per vaccine per month
-- Groups by truncated month and vaccine to report operational medication trends.
SELECT 
    TO_CHAR(v.given_on, 'YYYY-MM') AS admin_month,
    vac.id AS vaccine_id,
    vac.name AS vaccine_name,
    vac.species_target,
    COUNT(v.id) AS doses_given
FROM vaccinations v
JOIN vaccines vac ON v.vaccine_id = vac.id
GROUP BY TO_CHAR(v.given_on, 'YYYY-MM'), vac.id, vac.name, vac.species_target
ORDER BY admin_month DESC, doses_given DESC;


-- Query 3: Animals that have NEVER been vaccinated
-- Identifies vulnerable animals with zero clinical vaccination records using LEFT JOIN.
SELECT 
    a.id AS animal_id,
    a.name AS animal_name,
    a.species,
    a.breed,
    a.birth_date,
    o.id AS owner_id,
    o.name AS owner_name,
    o.phone AS owner_phone,
    o.email AS owner_email
FROM animals a
JOIN owners o ON a.owner_id = o.id
LEFT JOIN vaccinations v ON a.id = v.animal_id
WHERE v.id IS NULL
ORDER BY a.species, a.birth_date ASC;


-- Query 4: Overdue vaccinations with direct owner contact details
-- Multi-table join detecting booster lapses with animal, owner, and vaccine batch details.
SELECT 
    v.id AS vaccination_id,
    a.name AS animal_name,
    a.species,
    a.breed,
    o.name AS owner_name,
    o.phone AS owner_phone,
    o.email AS owner_email,
    vac.name AS vaccine_name,
    b.batch_no,
    v.given_on,
    v.next_due_on,
    CURRENT_DATE - v.next_due_on AS days_overdue
FROM vaccinations v
JOIN animals a ON v.animal_id = a.id
JOIN owners o ON a.owner_id = o.id
JOIN vaccines vac ON v.vaccine_id = vac.id
JOIN vaccine_batches b ON v.batch_id = b.id
WHERE v.next_due_on < CURRENT_DATE
ORDER BY days_overdue DESC;


-- Query 5: Top 5 vets by total clinic visits conducted
-- Evaluates veterinarian workload distribution.
SELECT 
    vet.id AS vet_id,
    vet.name AS vet_name,
    vet.license_no,
    COUNT(vis.id) AS total_visits,
    MIN(vis.visit_date) AS earliest_visit,
    MAX(vis.visit_date) AS latest_visit
FROM vets vet
JOIN visits vis ON vet.id = vis.vet_id
GROUP BY vet.id, vet.name, vet.license_no
ORDER BY total_visits DESC
LIMIT 5;


-- Query 6: Total available stock per vaccine across non-expired batches
-- Aggregates inventory stock safety levels, discarding expired batches.
SELECT 
    vac.id AS vaccine_id,
    vac.name AS vaccine_name,
    vac.species_target,
    COUNT(b.id) AS active_batches_count,
    COALESCE(SUM(b.quantity_remaining), 0) AS total_usable_doses
FROM vaccines vac
LEFT JOIN vaccine_batches b ON vac.id = b.vaccine_id 
    AND b.expiry_date >= CURRENT_DATE 
    AND b.quantity_remaining > 0
GROUP BY vac.id, vac.name, vac.species_target
ORDER BY total_usable_doses ASC;


-- Query 7: Average days between visits per species using window function LAG()
-- Calculates inter-visit frequency for preventive care by species.
WITH visit_intervals AS (
    SELECT 
        a.species,
        a.id AS animal_id,
        vis.visit_date,
        LAG(vis.visit_date) OVER (
            PARTITION BY a.id 
            ORDER BY vis.visit_date ASC
        ) AS previous_visit_date
    FROM visits vis
    JOIN animals a ON vis.animal_id = a.id
)
SELECT 
    species,
    COUNT(*) AS subsequent_visit_count,
    ROUND(AVG(visit_date - previous_visit_date), 1) AS avg_days_between_visits
FROM visit_intervals
WHERE previous_visit_date IS NOT NULL
GROUP BY species
ORDER BY avg_days_between_visits ASC;


-- Query 8: Running total of vaccinations within the year using SUM() OVER ()
-- Window function showing cumulative annual progress.
WITH monthly_vaccinations AS (
    SELECT 
        DATE_TRUNC('month', given_on)::DATE AS admin_month,
        COUNT(id) AS monthly_count
    FROM vaccinations
    WHERE EXTRACT(YEAR FROM given_on) = EXTRACT(YEAR FROM CURRENT_DATE)
    GROUP BY DATE_TRUNC('month', given_on)::DATE
)
SELECT 
    admin_month,
    monthly_count,
    SUM(monthly_count) OVER (ORDER BY admin_month ASC) AS cumulative_vaccinations_year_to_date
FROM monthly_vaccinations
ORDER BY admin_month ASC;
