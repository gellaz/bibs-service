# Project Overview – BIBS Backend

## Architecture

- Monolithic Spring Boot 4 application
- Package-by-feature structure
- PostgreSQL 18 database
- Flyway for schema migrations
- Keycloak 26 as IAM (OIDC)
- MinIO as S3-compatible object storage

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
