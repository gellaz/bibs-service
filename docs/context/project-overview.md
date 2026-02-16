# Project Overview – BIBS Backend

## Architecture

- Monolithic Spring Boot 4 application
- Package-by-feature structure
- PostgreSQL 18 database
- Flyway for schema migrations
- Keycloak 26 as IAM (OIDC)
- MinIO as S3-compatible object storage

## System Architecture

```
                         ┌─────────────────┐
                         │   Keycloak 26   │
                         └────────┬────────┘
                                  │ JWT
            ┌─────────────────────┼─────────────────────┐
            │                     │                     │
     ┌──────┴───────┐      ┌──────┴───────┐      ┌──────┴──────┐
     │ Customer App │      │Seller Portal │      │ Swagger UI  │
     │TanStack Start│      │TanStack Start│      │ (dev only)  │
     │ port 3000    │      │ port 3001    │      │ port 8080   │
     └──────┬───────┘      └──────┬───────┘      └──────┬──────┘
            │                     │                     │
            └─────────────────────┼─────────────────────┘
                                  │ REST + Bearer JWT
                         ┌────────┴────────┐
                         │  Spring Boot 4  │
                         │  (bibs-service) │
                         └────────┬────────┘
                                  │
               ┌──────────────────┼──────────────────┐
               │                  │                  │
          PostgreSQL           MinIO            Keycloak
          + PostGIS                          (admin API)
```

## Frontend Architecture

Two separate TanStack Start applications (see [ADR-0017](../decisions/0017-two-frontend-apps.md)):

| App           | Framework      | Audience                | Domain           | Keycloak Client |
|---------------|----------------|-------------------------|------------------|-----------------|
| Customer App  | TanStack Start | Customers + Admin panel | `bibs.it`        | `bibs-customer` |
| Seller Portal | TanStack Start | Sellers                 | `seller.bibs.it` | `bibs-seller`   |

The admin panel is a protected section within the customer app (`/admin`), guarded by the `ADMIN` realm role.
The frontend code lives in a separate monorepo with shared packages (UI, API client, auth).

## Deployment Model

The backend is deployed together with:

- Postgres (docker)
- Keycloak (docker)
- MinIO (docker)

See `compose.yml`.

## Source of Truth

- Database schema → Flyway migrations (`/src/main/resources/db/migration`)
- Identity model → `keycloak-realm.json`
- Authorization rules → backend security layer
- Domain rules → Service layer

## Architectural Style

- Clean layered architecture
- Controller → Service → Repository
- Entities are persistence models only
- Business logic lives in Services

## Design Goals

- Deterministic domain behavior
- Clear aggregate boundaries
- Safe stock handling
- Strict VAT verification flow
- Explicit order types
- Loyalty as ledger (no mutable balance without transaction)

## API Documentation

The system uses OpenAPI 3 for API documentation.

Principles:

- API contract-first mindset
- DTO-based schema
- Explicit response models
- Strict separation from persistence entities

Swagger UI available in development environments.

## Tooling Standards

- Error responses: error-handling-spring-boot-starter
- API docs: springdoc-openapi (OpenAPI 3)
- Mapping: MapStruct (+ Lombok binding)
- Formatting: Spotless (google-java-format + import rules)
