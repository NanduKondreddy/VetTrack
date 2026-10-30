# VetTrack: Build Plan

Animal health records, vaccination reminders and reporting platform.
Built to cover the Zoetis Tech & Digital internship (JR00021924) point by point, using Java and Spring Boot from your Tech Mahindra work, plus React, PostgreSQL, Python automation and Matplotlib.

Use synthetic data only. Never use real client or animal data.

## 1. Why this project

| JD requirement | Where VetTrack shows it |
|---|---|
| Full stack web app, React frontend | React (Vite) web app |
| Backend in Java or Python or Node | Spring Boot REST API (Java) |
| REST APIs | Documented endpoints with OpenAPI |
| Performance, security, reliability, observability | JWT auth, input validation, logging, health and metrics endpoints |
| Clean code, Git, code reviews | Feature branches and pull requests, reviewed by you before merge |
| Unit and integration tests | JUnit, Mockito, integration tests, frontend tests |
| Python scripts for cleaning and automation | Python service that loads messy CSV files, cleans, validates and loads them |
| SQL joins, aggregations, data quality checks | A `sql/` folder with real queries |
| Reports, lightweight dashboards, Matplotlib | Python report job that writes Matplotlib charts, plus a dashboard page |
| Basic CI | GitHub Actions running all tests on every push |
| Document code, queries, processes | README, API docs, SQL notes, short design doc |
| Agile and SDLC | GitHub Issues board with sprints, and a short retro note |

Skip GraphQL and Vue. The JD says "REST and/or GraphQL" and "React or Vue", so you do not need them.

## 2. What the app does

A small clinic or farm manager can:

1. Register owners and animals (dogs, cats, cattle, poultry).
2. Record visits, treatments and vaccinations.
3. See which vaccinations are due or overdue.
4. Track vaccine stock and batch expiry.
5. View a dashboard (vaccinations per month, overdue count, stock risk).
6. Import legacy data from CSV files through the Python cleaning service.

Roles: `ADMIN` and `STAFF`. Keep it to two.

## 3. Architecture

```
React (Vite)  --->  Spring Boot REST API  --->  PostgreSQL
                          ^                          ^
                          |                          |
                    Python data service (cleaning, validation, reports)
```

- Frontend: React 18, Vite, React Router, Axios. Charts: Recharts for the live page.
- Backend: Java 17, Spring Boot 3, Spring Web, Spring Data JPA (Hibernate), Spring Security with JWT, Bean Validation, Actuator, springdoc OpenAPI.
- Database: PostgreSQL 16. Migrations with Flyway.
- Python: Python 3.11, pandas, psycopg (or SQLAlchemy), matplotlib, pytest.
- Tooling: Docker Compose, GitHub Actions, Maven.

Keep the Python service separate. It talks to PostgreSQL directly for bulk loading and reports. That shows two languages doing the jobs they are best at.

## 4. Repository layout

```
vettrack/
  README.md
  docker-compose.yml
  .github/workflows/ci.yml
  docs/
    design.md            (architecture, decisions, ER diagram)
    api.md               (or use the generated OpenAPI page)
    sql-notes.md         (what each query answers)
    retro.md             (what went well, what to improve)
  backend/               (Spring Boot)
  frontend/              (React)
  data-service/          (Python)
    raw/                 (messy sample CSV files)
    src/
    tests/
    reports/             (generated PNG charts)
  sql/
    schema_checks.sql
    analytics.sql
    data_quality.sql
```

## 5. Database design

Tables (all with `id`, `created_at`, `updated_at`):

| Table | Main columns |
|---|---|
| `owners` | name, phone, email, city |
| `animals` | owner_id (FK), name, species, breed, birth_date, sex |
| `vets` | name, license_no |
| `visits` | animal_id (FK), vet_id (FK), visit_date, reason, notes |
| `vaccines` | name, species_target, interval_days |
| `vaccine_batches` | vaccine_id (FK), batch_no, expiry_date, quantity_remaining |
| `vaccinations` | animal_id (FK), vaccine_id (FK), batch_id (FK), given_on, next_due_on, visit_id (FK) |
| `users` | username, password_hash, role |
| `import_errors` | source_file, row_number, reason, raw_row (written by the Python service) |

Rules to enforce in both the database and the code: foreign keys, `NOT NULL` where needed, unique `(vaccine_id, batch_no)`, check that `next_due_on >= given_on`, check that quantity is not negative.

Seed about 200 owners, 400 animals and 1,500 vaccinations with a script (Faker). Keep the seed script in the repo.

## 6. Backend API

Base path `/api/v1`.

| Method | Path | Purpose |
|---|---|---|
| POST | `/auth/login` | Returns JWT |
| GET, POST | `/owners` | List (paged, searchable) and create |
| GET, PUT, DELETE | `/owners/{id}` | Read, update, delete |
| GET, POST | `/animals` | List with filters (species, owner) and create |
| GET, PUT | `/animals/{id}` | Read and update |
| GET, POST | `/animals/{id}/visits` | Visit history |
| POST | `/animals/{id}/vaccinations` | Record a vaccination, reduces batch stock |
| GET | `/vaccinations/due?withinDays=14` | Due and overdue list |
| GET | `/batches/expiring?withinDays=30` | Stock risk |
| GET | `/reports/summary` | Numbers for the dashboard |
| GET | `/actuator/health`, `/actuator/metrics` | Observability |

Build these properly:

- DTOs separate from entities.
- Bean Validation on every request body.
- One global exception handler (`@RestControllerAdvice`) that returns a consistent error JSON.
- Pagination and sorting on list endpoints.
- Recording a vaccination must run in one transaction: insert the vaccination, reduce stock, reject if the batch is expired or empty.
- Logging with SLF4J: log each request id, each business rule rejection, and each import.
- Passwords hashed with BCrypt. JWT secret from an environment variable, never in the repo.

## 7. Python data service

Commands (use `argparse` or Typer):

```
python -m src.clean --in raw/owners_legacy.csv --out cleaned/owners.csv
python -m src.load --file cleaned/owners.csv --table owners
python -m src.quality_report
python -m src.make_charts
```

Make the raw files truly messy so the cleaning is real:

- Mixed date formats (`12/03/2024`, `2024-03-12`, `12 Mar 24`).
- Duplicate owners with different spelling or case.
- Phone numbers with spaces, dashes, country codes or letters.
- Missing species, impossible birth dates (in the future), negative quantities.
- Species written as `dog`, `Dog`, `DOG`, `canine`.

Cleaning steps to implement: trim and normalise text, parse dates with a fallback list, standardise phone numbers, map species synonyms, drop exact duplicates, flag near duplicates, and write every rejected row to `import_errors` with a reason. Print a short summary: rows in, rows cleaned, rows rejected, by reason.

Charts (Matplotlib, saved as PNG in `reports/`):

1. Vaccinations per month.
2. Overdue vaccinations by species.
3. Batches expiring in the next 30, 60, 90 days.
4. Data quality: rejected rows by reason.

Add `pytest` tests for every cleaning function (dates, phones, species mapping, duplicates).

## 8. SQL files

Write these yourself and explain each one in `docs/sql-notes.md`. At least 12 queries.

`analytics.sql` (joins and aggregations):

1. Animals per owner, only owners with more than 2 animals (`JOIN`, `GROUP BY`, `HAVING`).
2. Vaccinations per vaccine per month (`GROUP BY` on a date part).
3. Animals that have never been vaccinated (`LEFT JOIN ... WHERE ... IS NULL`).
4. Overdue vaccinations with owner contact (multi table `JOIN`).
5. Top 5 vets by number of visits (`ORDER BY`, `LIMIT`).
6. Stock remaining per vaccine across non expired batches.
7. Average days between visits per species (window function `LAG`).
8. Running total of vaccinations in the year (window function `SUM() OVER`).

`data_quality.sql`:

9. Count of null or empty required fields per table.
10. Duplicate owners (same phone or same lowercase email).
11. Orphan rows (vaccinations pointing to missing batches).
12. Impossible values (birth date in the future, `next_due_on` before `given_on`, negative stock).
13. Row counts before and after an import.

Add indexes for the slow ones and show the result of `EXPLAIN ANALYZE` before and after in the notes. One honest before and after is a strong interview story.

## 9. Frontend pages

1. Login.
2. Dashboard: totals, overdue count, expiring batches, one chart.
3. Owners list with search and pagination, create and edit form.
4. Animal detail: profile, visit history, vaccinations, "Record vaccination" form.
5. Due list with a filter (7, 14, 30 days) and a CSV export button.

Keep it simple and clean. Use form validation, loading and error states. Store the JWT in memory or an HTTP only cookie if you can. Do not spend days on styling.

## 10. Testing plan

| Layer | Tool | What to test (minimum) |
|---|---|---|
| Backend unit | JUnit 5, Mockito | Vaccination service rules: expired batch rejected, empty batch rejected, stock reduced, next due date calculated |
| Backend integration | Spring Boot Test with Testcontainers (PostgreSQL) | Controller to database for owners and vaccinations, auth required, validation errors return 400 |
| Python | pytest | All cleaning functions, loader rejects bad rows |
| Frontend | Vitest and React Testing Library | Login form, due list renders and filters |

Target: at least 25 meaningful tests in total. Do not chase a coverage number. Test the rules that matter.

## 11. CI with GitHub Actions

`.github/workflows/ci.yml` should run on every push and pull request with three jobs:

1. `backend`: set up Java 17, run `mvn -B verify`.
2. `data-service`: set up Python, install requirements, run `pytest`.
3. `frontend`: set up Node, run `npm ci`, `npm test -- --run`, `npm run build`.

Add the status badge to the top of the README. Make sure the badge is green before you apply.

## 12. Observability and security checklist

- Actuator health and metrics enabled (restrict sensitive ones to `ADMIN`).
- Structured log lines with request id.
- All input validated, SQL only through JPA or parameterised queries.
- BCrypt, JWT expiry, role checks on every write endpoint.
- CORS limited to the frontend origin.
- Secrets in environment variables with a `.env.example` file.
- No real personal data anywhere.

## 13. Git and Agile habits (these are scored in interviews)

- Create the GitHub Issues board first. Write 20 to 30 small issues grouped into 3 sprints.
- One branch per issue, small commits with clear messages (`feat: record vaccination with stock check`).
- Open a pull request for each issue, even though you are the only developer. Write a short description. Review your own diff before merging.
- Tag a release `v1.0.0` when done.
- Write `docs/retro.md` after each sprint, three lines each: what worked, what did not, what changes.

## 14. Build order (about 3 weeks of focused evenings)

**Sprint 1: foundation (days 1 to 6)**

1. Repo, Docker Compose with PostgreSQL, Flyway schema, seed script.
2. Spring Boot project, entities, repositories, owners and animals endpoints.
3. Global error handling, validation, pagination.
4. JWT login and role checks.
5. CI with backend job only.

**Sprint 2: core logic and data (days 7 to 13)**

1. Visits, vaccines, batches, vaccination endpoint with transaction and rules.
2. Due and expiring endpoints.
3. Unit and integration tests for the rules.
4. Python cleaning, loading and `import_errors`, with pytest.
5. SQL files and `EXPLAIN ANALYZE` notes.

**Sprint 3: UI, reports, polish (days 14 to 21)**

1. React pages and API wiring.
2. Matplotlib charts and dashboard page.
3. Frontend tests, CI for all three jobs.
4. README, design doc, OpenAPI page, screenshots, retro.
5. Optional: deploy the frontend (Vercel or Netlify) and the backend (any free host). A live link is nice but not required.

If time is short, cut in this order: deployment, CSV export, charts on the web page. Never cut tests, CI or SQL.

## 15. README must contain

1. One paragraph on what it is and why.
2. A screenshot of the dashboard and one chart.
3. Architecture diagram (a simple image or Mermaid).
4. How to run everything with one command (`docker compose up`).
5. How to run tests for each part.
6. Example `curl` calls for login and recording a vaccination.
7. Link to the OpenAPI page, SQL notes and design doc.
8. A short "what I would improve next" list.

## 16. Definition of done

- [ ] `docker compose up` starts database, backend, frontend and data service.
- [ ] 25 or more tests pass locally and in CI, badge is green.
- [ ] 12 or more SQL queries in the repo with notes.
- [ ] Python service cleans the messy files and logs rejected rows.
- [ ] Four Matplotlib charts are generated and shown in the README.
- [ ] Repo has issues, pull requests, a v1.0.0 tag and a retro file.
- [ ] You can explain every file in an interview without notes.

## 17. Resume bullets (fill in only after it is built)

Use these as templates. Replace the numbers with your real ones. Delete any bullet you cannot back with the repo.

- Built VetTrack, a full stack animal health records platform with a Spring Boot REST API, React frontend and PostgreSQL, with JWT authentication and role based access.
- Implemented transactional vaccination logic with stock and expiry rules, covered by `<N>` JUnit and integration tests running in GitHub Actions CI.
- Wrote a Python data service (pandas) that cleans and validates messy CSV files, loads them to PostgreSQL, and logs `<N>` rejected rows by reason.
- Wrote `<N>` SQL queries (joins, aggregations, window functions, data quality checks) and improved a slow query using an index, confirmed with `EXPLAIN ANALYZE`.
- Generated Matplotlib reports and a dashboard for due vaccinations and expiring stock.

Skills you can add to the resume once this is done: PostgreSQL project evidence, JUnit, Mockito, pytest, GitHub Actions, Flyway, OpenAPI.

## 18. Interview questions this project prepares you for

- Explain the layers of your Spring Boot app and why you used DTOs.
- What does `@Transactional` do, and what happens if the stock update fails?
- How does JWT work, and where is it checked?
- Walk through your slowest SQL query and how you fixed it.
- Inner join versus left join, with an example from your data.
- How did you decide which cases to unit test and which to integration test?
- How do you handle bad data in an import?
- What is the difference between Agile and Waterfall, and which did you use here?

## 19. Rules for honesty

- Only claim what exists in the repo.
- Keep real commit history. Do not add the whole project in one commit.
- If you use an AI assistant for parts, make sure you understand and can rebuild any part you list on the resume.
