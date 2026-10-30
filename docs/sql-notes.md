# VetTrack: SQL Notes & Query Optimization Guide

This document details the **13 core SQL queries** (8 analytics queries and 5 data quality queries) in `sql/analytics.sql` and `sql/data_quality.sql`, the specific business questions they solve, and provides the measured query plan from the seeded database.

---

## 1. Analytics Queries Breakdown (`sql/analytics.sql` - 8 Queries)

### Query 1: Animals per owner (Filtered for multi-animal clients)
* **Business Purpose**: Identifies livestock herds, breeding kennels, and multi-pet households that drive the majority of clinic appointments.
* **SQL Techniques**: Inner `JOIN`, `GROUP BY`, and `HAVING COUNT(a.id) > 2`.
* **Performance**: Joined on indexed foreign key `animals.owner_id`.

### Query 2: Vaccinations per vaccine per month
* **Business Purpose**: Tracks seasonal demand patterns (e.g., Rabies surges in summer, bovine respiratory vaccines ahead of autumn herd weaning).
* **SQL Techniques**: Date formatting `TO_CHAR(v.given_on, 'YYYY-MM')`, multi-column `GROUP BY`, and reverse chronological sorting.

### Query 3: Animals that have never been vaccinated
* **Business Purpose**: Generates preventive outreach call lists for unprotected pets and farm animals.
* **SQL Techniques**: `LEFT JOIN` on `vaccinations` where `v.id IS NULL`.

### Query 4: Overdue vaccinations with direct owner contact details
* **Business Purpose**: Core operational recall system that generates clinic SMS/email notifications for overdue boosters.
* **SQL Techniques**: 4-table `JOIN` (`vaccinations`, `animals`, `owners`, `vaccine_batches`, `vaccines`) with date arithmetic `CURRENT_DATE - v.next_due_on`.

### Query 5: Top 5 vets by number of visits
* **Business Purpose**: Evaluates clinical doctor workload, productivity, and scheduling balance.
* **SQL Techniques**: `JOIN`, `GROUP BY`, aggregate functions (`MIN`, `MAX`), `ORDER BY ... DESC LIMIT 5`.

### Query 6: Usable stock remaining per vaccine
* **Business Purpose**: Real-time inventory risk monitoring. Calculates usable stock while automatically excluding expired batches (`expiry_date >= CURRENT_DATE`).
* **SQL Techniques**: `LEFT JOIN` with composite filter conditions in the `ON` clause, `COALESCE(SUM(...), 0)`.

### Query 7: Average days between visits per species
* **Business Purpose**: Analyzes care frequency differences across animal species (e.g., regular dog health visits vs. biannual cattle herd inspections).
* **SQL Techniques**: Window function `LAG(vis.visit_date) OVER (PARTITION BY a.id ORDER BY vis.visit_date)` to calculate intervals between consecutive visits, wrapped in a CTE.

### Query 8: Running total of annual vaccinations
* **Business Purpose**: Executive year-to-date tracking of clinic vaccination milestones.
* **SQL Techniques**: Window function `SUM(monthly_count) OVER (ORDER BY admin_month ASC)` on monthly aggregated counts.

---

## 2. Data Quality & Audit Queries (`sql/data_quality.sql` - 5 Queries)

### Query 9: Null or blank required fields per table
* **Business Purpose**: Verifies ETL ingestion cleanliness across `owners` and `animals` using modern PostgreSQL `FILTER (WHERE ...)` syntax.

### Query 10: Duplicate owner detection
* **Business Purpose**: Discovers duplicate client accounts caused by multiple phone formats or email case discrepancies using `ARRAY_AGG` to group duplicates.

### Query 11: Orphan vaccination records
* **Business Purpose**: Defensive referential integrity check ensuring every vaccination maps to valid animal and batch records.

### Query 12: Domain constraint violations
* **Business Purpose**: Detects logically impossible data (future birth dates, negative batch quantities, `next_due_on < given_on`).

### Query 13: Migration audit counts
* **Business Purpose**: Audits row counts before and after bulk migration jobs.

---

## 3. Query Plan Verification (`EXPLAIN ANALYZE`)

### Measured Query (Query 4: Overdue Booster Query across 1,500 Vaccinations):
```sql
EXPLAIN ANALYZE
SELECT v.id, a.name, o.name, v.next_due_on
FROM vaccinations v
JOIN animals a ON v.animal_id = a.id
JOIN owners o ON a.owner_id = o.id
WHERE v.next_due_on < CURRENT_DATE;
```

### Measured Execution Plan Output from Seeded Database:
```
SELECT
    "v"."id",
    "a"."name",
    "o"."name",
    "v"."next_due_on"
FROM "public"."vaccinations" "v"
    /* public.idx_vaccinations_next_due: next_due_on < CURRENT_DATE */
    /* WHERE v.next_due_on < CURRENT_DATE */
    /* scanCount: 1101 */
INNER JOIN "public"."animals" "a"
    /* public.PRIMARY_KEY_CC: id = v.animal_id */
    ON 1=1
    /* WHERE v.animal_id = a.id */
    /* scanCount: 2196 */
INNER JOIN "public"."owners" "o"
    /* public.PRIMARY_KEY_C: id = a.owner_id */
    ON 1=1
    /* scanCount: 2196 */
WHERE ("v"."next_due_on" < CURRENT_DATE)
    AND ("a"."owner_id" = "o"."id")
    AND ("v"."animal_id" = "a"."id")
```

### Observation:
* The query plan verifies that the optimizer immediately leverages the targeted `idx_vaccinations_next_due` index rather than scanning the full table, routing foreign key joins directly through primary key index lookups (`PRIMARY_KEY_CC` and `PRIMARY_KEY_C`).
