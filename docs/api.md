# VetTrack: REST API Reference Documentation

Base URL: `http://localhost:8080/api/v1`  
OpenAPI 3 / Swagger UI: `http://localhost:8080/swagger-ui.html`  
Raw OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## Authentication & Headers

Protected endpoints require a standard Bearer token in the HTTP `Authorization` header:
```http
Authorization: Bearer <jwt_token>
```

All responses include an `X-Request-Id` header for end-to-end distributed tracing.

---

## 1. Authentication Endpoints

### `POST /api/v1/auth/login`
Authenticates user credentials and returns a signed JWT.

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "username": "admin",
  "role": "ADMIN",
  "expiresInMs": 86400000
}
```

---

## 2. Owners Management

### `GET /api/v1/owners`
Paged and searchable client list.
* **Query Parameters**:
  * `query`: Search string across name, phone, email, city
  * `page`: Page index (default: `0`)
  * `size`: Page size (default: `10`)
  * `sortBy`: Sort property (default: `name`)
  * `direction`: `asc` or `desc`

### `POST /api/v1/owners`
Registers a new animal owner.

**Request Body:**
```json
{
  "name": "Elena Rostova",
  "phone": "+1-555-019-2834",
  "email": "elena.rostova@farmfresh.org",
  "city": "Austin"
}
```

### `DELETE /api/v1/owners/{id}`
Deletes an owner and their associated records. **Requires `ADMIN` role.**

---

## 3. Animal Records & Clinical History

### `GET /api/v1/animals`
List animals with filters.
* **Query Parameters**: `species`, `ownerId`, `search`, `page`, `size`.

### `POST /api/v1/animals`
Registers a new animal under an owner.

**Request Body:**
```json
{
  "ownerId": 1,
  "name": "Barnaby",
  "species": "Dog",
  "breed": "Golden Retriever",
  "birthDate": "2022-03-15",
  "sex": "Male"
}
```

### `POST /api/v1/animals/{id}/vaccinations`
Records a vaccination, performs validation, and decrements batch stock.

**Request Body:**
```json
{
  "vaccineId": 1,
  "batchId": 1,
  "givenOn": "2026-10-30",
  "nextDueOn": null,
  "visitId": 1
}
```

**Response (201 Created):**
```json
{
  "id": 142,
  "animalId": 1,
  "animalName": "Barnaby",
  "vaccineId": 1,
  "vaccineName": "Rabies Core",
  "batchId": 1,
  "batchNo": "RAB-2026-A1",
  "givenOn": "2026-10-30",
  "nextDueOn": "2027-10-30",
  "visitId": 1,
  "overdue": false,
  "createdAt": "2026-10-30T14:35:10Z"
}
```

---

## 4. Reminders & Pharmacy Inventory

### `GET /api/v1/vaccinations/due?withinDays=14`
Returns animals with vaccinations due or overdue within the given horizon.

### `GET /api/v1/batches/expiring?withinDays=30`
Returns batches at risk of expiring within the specified window.

### `GET /api/v1/reports/summary`
Returns executive dashboard numbers (total clients, animal population, overdue counts, expiring batches).

---

## 5. Observability Endpoints

* `GET /actuator/health` — Application health check (Public status; details restricted to `ADMIN`)
* `GET /actuator/metrics` — Internal JVM and HTTP metrics (`ADMIN` only)
