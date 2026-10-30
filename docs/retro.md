# VetTrack: Technical Sprint Retrospective Notes

These notes capture real technical hurdles and solutions encountered across the three development milestones.

---

## Sprint 1: Architecture, Flyway Migrations, & Security Setup
* **What worked**:
  Flyway reproducible migrations (`V1`, `V2`, `V3`) cleanly set up all 9 tables, indexes, and synthetic seeds. Spring Security stateless JWT filter with MDC request ID tracing gave transparent request tracing in logs.
* **What did not**:
  Flyway 10 introduced split module packages (`flyway-database-postgresql`), which initially failed dependency resolution under Spring Boot 3.2's managed parent.
* **What changed**:
  Removed the extra dependency and relied on Spring Boot's managed `flyway-core`, ensuring consistent behavior across both PostgreSQL and H2 compatibility mode.

---

## Sprint 2: Domain Business Rules, Data Cleaning, & SQL Queries
* **What worked**:
  Atomic transaction wrapping in `VaccinationService` successfully caught zero-stock batches and expired batches before any state change could occur. The Python cleaner parsed multiple date formats and mapped species synonyms (`canine` -> `Dog`).
* **What did not**:
  Legacy phone data included unexpected alphabetical strings and malformed lengths that threw unexpected exceptions during naive parsing.
* **What changed**:
  Added strict pre-regex sanitation and directed all rejected records into the `import_errors` table with explicit failure reasons.

---

## Sprint 3: React Single-Page Application & Test Suites
* **What worked**:
  Vite and React 18 delivered fast builds (9.0s), and Recharts integrated cleanly into the executive dashboard with dynamic species distribution and monthly counts. All 41 automated tests across Java (21), Python (15), and React (5) passed cleanly.
* **What did not**:
  Due list table testing in React Testing Library initially raised ambiguity errors because the word "Overdue" appeared both in the status badge and in relative date calculations.
* **What changed**:
  Updated test assertions to use scoped element matchers (`getAllByText`) and verified CSV export downloads.

