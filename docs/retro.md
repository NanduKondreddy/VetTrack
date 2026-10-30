# VetTrack: Sprint Retrospectives

## Sprint 1: Foundation (Database, Architecture, Security)
* **What worked**: 
  Flyway database migrations and unified schema definitions provided an immediate, reproducible database setup. Spring Security with stateless JWT and the MDC Request ID filter gave us strong security and request tracing from day one.
* **What did not**: 
  Flyway 10 introduced modular database dependencies (`flyway-database-postgresql`), which initially conflicted with Spring Boot's managed parent BOM.
* **What changes**: 
  Pinned Flyway to the managed version in `pom.xml`, relying on `flyway-core` for seamless PostgreSQL and H2 compatibility.

---

## Sprint 2: Core Domain Logic, Data Service, & SQL
* **What worked**: 
  Enforcing batch validation and stock decrement inside a single `@Transactional` method eliminated race conditions. The Python cleaner pipeline cleanly normalized multi-format dates, standardized phone numbers, and handled synonym taxonomies (`canine` -> `Dog`).
* **What did not**: 
  Messy legacy phone numbers contained alphabet characters and missing digits that broke standard regex parsers.
* **What changes**: 
  Implemented strict pre-validation digit extraction in `standardize_phone`, routing all unfixable rows to the `import_errors` table for auditing.

---

## Sprint 3: Frontend UI, Analytics Visualizations, & CI Automation
* **What worked**: 
  React 18 with Vite provided fast hot reloading and instant test execution via Vitest. The 4 Matplotlib analytical charts and Recharts interactive widgets delivered high-impact clinical visualizations.
* **What did not**: 
  Multiple elements containing the text "Overdue" in the due list table required exact matcher scoping in the test assertions.
* **What changes**: 
  Switched to scoped test selectors in `DueList.test.jsx` and added automated CI coverage across all 3 parallel jobs in GitHub Actions.
