# VetTrack: System Design & Architecture Document

## 1. System Overview
VetTrack is an enterprise-grade veterinary health records, vaccination reminder, and stock management platform designed to automate clinical operations for small animal clinics and commercial livestock herds.

## 2. High-Level Architecture

```mermaid
graph TD
    Client[React 18 + Vite SPA] -->|REST / JSON over HTTPS| API[Spring Boot 3 REST API]
    API -->|JPA / Hibernate| PG[(PostgreSQL 16 Database)]
    API -->|MDC Logging| Log[SLF4J Structured Logs]
    API -->|Health / Metrics| Actuator[Spring Boot Actuator]
    
    subgraph Data Engineering Pipeline
        Messy[Legacy CSV Files] --> Cleaner[Python pandas Cleaner]
        Cleaner --> Cleaned[Cleaned Datasets]
        Cleaned --> Loader[Python Database Loader]
        Loader --> PG
        Cleaner --> Errors[(import_errors Table)]
        PG --> Reporter[Python Matplotlib Reporting]
        Reporter --> Reports[PNG Executive Visualizations]
    end
```

## 3. Key Design Decisions

### Separation of Backend and Data Service
- **Java (Spring Boot 3)** handles the high-concurrency transactional REST API, enforces business domain constraints, manages JWT authentication and RBAC, and controls stock decrement within database transactions.
- **Python (pandas & Matplotlib)** handles analytical batch data pipelines, fuzzy cleaning of legacy client records, multi-format date normalization, and report generation.

### Transactional Vaccination & Stock Integrity
When a vaccination is recorded:
1. Animal existence and batch integrity are verified.
2. The batch's expiry date is compared with the administration date (`given_on`). If expired, a `BatchExpiredException` is thrown.
3. The remaining inventory is evaluated. If zero, a `BatchOutOfStockException` is thrown.
4. Stock is decremented within the same atomic `@Transactional` boundary.
5. The `next_due_on` booster date is automatically computed based on the vaccine's interval days.

### Security Architecture
- Stateless JWT authentication using HMAC-SHA-256 (`io.jsonwebtoken:jjwt`).
- Passwords hashed using standard `BCryptPasswordEncoder` (12 salt rounds).
- RBAC with two roles: `ADMIN` (can delete records and view Actuator metrics) and `STAFF` (can record clinic operations, clients, and vaccinations).

## 4. Entity-Relationship Diagram (ERD)

```mermaid
erDiagram
    USERS {
        bigserial id PK
        varchar username UK
        varchar password_hash
        varchar role
        timestamptz created_at
        timestamptz updated_at
    }

    OWNERS {
        bigserial id PK
        varchar name
        varchar phone
        varchar email
        varchar city
        timestamptz created_at
        timestamptz updated_at
    }

    ANIMALS {
        bigserial id PK
        bigint owner_id FK
        varchar name
        varchar species
        varchar breed
        date birth_date
        varchar sex
        timestamptz created_at
        timestamptz updated_at
    }

    VETS {
        bigserial id PK
        varchar name
        varchar license_no UK
        timestamptz created_at
        timestamptz updated_at
    }

    VISITS {
        bigserial id PK
        bigint animal_id FK
        bigint vet_id FK
        date visit_date
        varchar reason
        text notes
        timestamptz created_at
        timestamptz updated_at
    }

    VACCINES {
        bigserial id PK
        varchar name UK
        varchar species_target
        int interval_days
        timestamptz created_at
        timestamptz updated_at
    }

    VACCINE_BATCHES {
        bigserial id PK
        bigint vaccine_id FK
        varchar batch_no
        date expiry_date
        int quantity_remaining
        timestamptz created_at
        timestamptz updated_at
    }

    VACCINATIONS {
        bigserial id PK
        bigint animal_id FK
        bigint vaccine_id FK
        bigint batch_id FK
        bigint visit_id FK
        date given_on
        date next_due_on
        timestamptz created_at
        timestamptz updated_at
    }

    IMPORT_ERRORS {
        bigserial id PK
        varchar source_file
        int row_number
        varchar reason
        text raw_row
        timestamptz created_at
        timestamptz updated_at
    }

    OWNERS ||--o{ ANIMALS : "owns"
    ANIMALS ||--o{ VISITS : "attends"
    VETS ||--o{ VISITS : "conducts"
    VACCINES ||--o{ VACCINE_BATCHES : "contains"
    ANIMALS ||--o{ VACCINATIONS : "receives"
    VACCINES ||--o{ VACCINATIONS : "type_of"
    VACCINE_BATCHES ||--o{ VACCINATIONS : "drawn_from"
    VISITS ||--o{ VACCINATIONS : "administered_during"
```
