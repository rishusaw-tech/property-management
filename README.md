# PMFMS — Property Management & Facility Management System (Backend)

Spring Boot 3 backend implementing the BRD v1.0: property acquisition & onboarding, units, lease lifecycle, billing & collections, asset register, preventive/corrective work orders with SLA timers, vendor management and statutory compliance tracking — secured with JWT (access + rotating refresh tokens), role-based access control, CORS and a forgot/reset-password flow.

## Tech Stack
- Java 17, Spring Boot 3.3 (Web, Security, Data JPA, Validation)
- JWT (jjwt), BCrypt
- Flyway DB migrations, H2 (default) / MySQL-ready
- ModelMapper (single shared mapper for every entity↔DTO mapping)
- springdoc-openapi (Swagger UI)

## Architecture / Layers
```
com.pmfms
├── api/            # Interfaces with ALL springdoc/Swagger annotations
├── controller/     # Implementations of the api interfaces (+ @PreAuthorize RBAC)
├── dto/            # Request/response DTOs with jakarta validation
├── enums/          # All domain enums (statuses, types, roles...)
├── entity/         # JPA entities — proper relationships, ≥2NF (amenities normalized)
├── repository/     # Spring Data JPA repositories (paginated, filtered queries)
├── service/        # Service interfaces
├── service/impl/   # Service implementations (business rules & lifecycles)
├── mapper/         # EntityMapper — the ONE ModelMapper wrapper used everywhere
├── security/       # JwtService + JwtAuthFilter
├── config/         # Security (auth+CORS), OpenAPI, ModelMapper, exception handler
└── util/           # CodeGenerator (PROP-/UNIT-/LSE-/INV-/WO-/AST- codes)
```

## Run
```bash
mvn spring-boot:run
```
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:file:./data/pmfms`)

**Default admin (seeded by Flyway `V5`):** `admin@pmfms.com` / `Admin@123` — change in production.

Public signup (`POST /api/v1/auth/signup`) creates **TENANT** users; the admin creates managers/owners/vendors via `POST /api/v1/users`.

## Environment Variables
All configuration is env-driven with dev defaults (see `.env.example`):

| Variable | Default | Purpose |
|---|---|---|
| `SERVER_PORT` | 8080 | HTTP port |
| `DB_URL` / `DB_DRIVER` / `DB_USERNAME` / `DB_PASSWORD` | H2 file DB | Datasource |
| `JWT_SECRET` | dev value | **≥32 chars, override in prod** |
| `JWT_EXPIRATION_MS` | 900000 (15 min) | Access token TTL |
| `JWT_REFRESH_EXPIRATION_MS` | 604800000 (7 d) | Refresh token TTL |
| `RESET_TOKEN_EXPIRATION_MS` | 900000 | Password-reset token TTL |
| `FRONTEND_RESET_PASSWORD_URL` | localhost:3000/reset-password | Link in reset email |
| `CORS_ALLOWED_ORIGINS` | localhost:3000,5173 | Comma-separated origins |
| `SLA_CRITICAL_HOURS` … `SLA_LOW_HOURS` | 4 / 24 / 72 / 168 | Work-order SLA per priority |
| `H2_CONSOLE_ENABLED` | true | Disable in prod |

## Switching to MySQL
1. Uncomment `mysql-connector-j` and `flyway-mysql` in `pom.xml`.
2. Set `DB_URL=jdbc:mysql://host:3306/pmfms`, `DB_DRIVER=com.mysql.cj.jdbc.Driver`, credentials.
The Flyway scripts use portable SQL and run on MySQL as-is.

## API Map (v1, all list endpoints paginated)
| Area | Base path | Highlights |
|---|---|---|
| Auth | `/api/v1/auth` | signup, login, refresh (rotation), logout, logout-all, forgot-password, reset-password, DELETE `/me` |
| Users | `/api/v1/users` | admin-created users with roles, `/me` |
| Properties | `/api/v1/properties` | DRAFT→…→ACTIVE onboarding workflow, auto `PROP-` codes |
| Units | `/api/v1/units` | auto `UNIT-` codes, occupancy status, amenities |
| Leases | `/api/v1/leases` | submit / approve / renew (rent revision) / notice / terminate / close |
| Billing | `/api/v1/invoices` | RENT/CAM/UTILITY/LATE_FEE/ONE_TIME, **idempotent** `POST /{id}/payments` |
| Assets | `/api/v1/assets` | auto QR `AST-` tags, `/by-tag/{tag}` scan lookup |
| Vendors | `/api/v1/vendors` | rating scorecard, status control |
| Work orders | `/api/v1/work-orders` | auto SLA due by priority, assign, validated lifecycle transitions |
| Compliance | `/api/v1/compliance-records` | VALID / EXPIRING_SOON (≤30 d) / EXPIRED auto-status |

## Background Jobs
- Overdue invoice marker (daily) — BRD 14.3
- Lease renewal-due flagger, 90-day window (daily) — BRD 8.5
- Compliance status refresher (daily) — BRD 9.6/15.1

## Database
- Flyway migrations in `src/main/resources/db/migration` (V1–V5, includes admin seed)
- ER diagram: paste `docs/dbdiagram.dbml` into [dbdiagram.io](https://dbdiagram.io)
- Hibernate runs with `ddl-auto=validate` — the schema is owned by Flyway

## Notes
- Password reset "email" is logged to the console in dev (`ConsoleEmailService`); implement `EmailService` with SMTP/SendGrid/SES for production.
- Payments are idempotent by `reference` — resubmitting the same reference returns the original payment (BRD 11.3).
