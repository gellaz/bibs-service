<h1 align="center">BIBS Service</h1>

<div align="center">

![Java Badge](https://img.shields.io/badge/Java-21-555?logo=openjdk&logoColor=white&labelColor=blue&style=plastic)
![Spring Boot Badge](https://img.shields.io/badge/Spring%20Boot-4-555?logo=springboot&logoColor=fff&labelColor=6DB33F&style=plastic)
![Apache Maven Badge](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=fff&style=plastic)
![Hibernate Badge](https://img.shields.io/badge/Hibernate-59666C?logo=hibernate&logoColor=fff&style=plastic)
![Flyway Badge](https://img.shields.io/badge/Flyway-CC0200?logo=flyway&logoColor=fff&style=plastic)
![JUnit5 Badge](https://img.shields.io/badge/JUnit5-25A162?logo=junit5&logoColor=fff&style=plastic)
![OpenAPI Initiative Badge](https://img.shields.io/badge/OpenAPI-3.1-555?logo=openapiinitiative&logoColor=fff&labelColor=6BA539&style=plastic)
![Swagger Badge](https://img.shields.io/badge/Swagger-85EA2D?logo=swagger&logoColor=000&style=plastic)
<br/>
![PostgreSQL Badge](https://img.shields.io/badge/PostgreSQL-18-555?logo=postgresql&logoColor=fff&labelColor=4169E1&style=plastic)
![MinIO Badge](https://img.shields.io/badge/MinIO-C72E49?logo=minio&logoColor=fff&style=plastic)
<br/>
![GitHub Actions Badge](https://img.shields.io/badge/GitHub%20Actions-000?logo=githubactions&logoColor=fff&style=plastic)
![Docker Badge](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=fff&style=plastic)

</div>

---

## Overview

Backend service for the BIBS local commerce e-commerce platform.
Monolithic Spring Boot 4 application with package-by-feature architecture,
PostgreSQL 18, Keycloak 26 (OIDC), MinIO (S3), and Flyway migrations.

---

## Quick Start

**Prerequisites:** Java 21 ([SDKMAN!](https://sdkman.io/)), [Docker](https://www.docker.com/get-started/).

```shell
git clone https://github.com/gellaz/bibs-service.git
cd bibs-service
mvnw spring-boot:run -Dspring.profiles.active=local
```

> In IntelliJ add `-Dspring.profiles.active=local` to VM options.
> Create `application-local.yml` to override settings for development.

---

## Local Dev

Infrastructure is managed via `compose.yml`:

```shell
docker compose up -d
```

| Service      | URL / Port                       | Credentials                         |
|--------------|----------------------------------|-------------------------------------|
| Application  | http://localhost:8080            | —                                   |
| Swagger UI   | http://localhost:8080/swagger-ui | OAuth2 via Keycloak                 |
| OpenAPI JSON | http://localhost:8080/api-docs   | —                                   |
| Keycloak     | http://localhost:8085            | `kcadmin` / `P4ssword!`             |
| MinIO        | http://localhost:9001            | `minioadmin` / `P4ssword!`          |
| PostgreSQL   | localhost:5432                   | `pgadmin` / `P4ssword!` / `bibs-db` |

### Pre-configured Users (Keycloak `bibs` realm)

| User          | Email                 | Password  | IAM Role | App Role      |
|---------------|-----------------------|-----------|----------|---------------|
| Admin         | admin@bibs.it         | P4ssword! | ADMIN    | ADMIN         |
| User          | user@bibs.it          | P4ssword! | USER     | CUSTOMER      |
| Store Owner   | store.owner@bibs.it   | P4ssword! | USER     | STORE_OWNER   |
| Store Manager | store.manager@bibs.it | P4ssword! | USER     | STORE_MANAGER |
| Store Clerk   | store.clerk@bibs.it   | P4ssword! | USER     | STORE_CLERK   |

---

## OpenAPI

OpenAPI documentation is **mandatory** for every endpoint (see [ADR-0012](docs/decisions/0012-openapi-as-contract.md)).

- Swagger UI: http://localhost:8080/swagger-ui (OAuth2 Keycloak integration built-in)
- OpenAPI spec: http://localhost:8080/api-docs

Every endpoint must have: summary, description, response codes, error responses, and security requirements.
Error responses follow the standard format from `error-handling-spring-boot-starter` (
see [ADR-0013](docs/decisions/0013-error-handling-standard.md)).

---

## Build

```shell
mvnw clean package
```

Run in production:

```shell
java -Dspring.profiles.active=production -jar ./target/bibs-service-0.0.1-SNAPSHOT.jar
```

Docker image:

```shell
mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=it.bibs/bibs-service
```

---

## Documentation Map

### Context (Project Knowledge)

| Document                                                | Description                                    |
|---------------------------------------------------------|------------------------------------------------|
| [project-overview.md](docs/context/project-overview.md) | Architecture, stack, design goals              |
| [requirements.md](docs/context/requirements.md)         | Functional requirements by actor               |
| [domain-map.md](docs/context/domain-map.md)             | Aggregates and domain model                    |
| [flows.md](docs/context/flows.md)                       | Business flows (registration, orders, loyalty) |
| [authz-model.md](docs/context/authz-model.md)           | Authorization roles and rules                  |
| [vat-verification.md](docs/context/vat-verification.md) | Async VAT verification flow                    |
| [search-and-geo.md](docs/context/search-and-geo.md)     | Full-text search and PostGIS distance          |
| [loyalty.md](docs/context/loyalty.md)                   | Ledger-based loyalty system                    |
| [coding-rules.md](docs/context/coding-rules.md)         | Mandatory coding standards                     |
| [architecture.md](docs/context/architecture.md)         | Layering rules and constraints                 |

### Architecture Decision Records (ADRs)

| ADR                                                             | Title                                         |
|-----------------------------------------------------------------|-----------------------------------------------|
| [0001](docs/decisions/0001-monolith-package-by-feature.md)      | Monolith with package-by-feature              |
| [0002](docs/decisions/0002-flyway-strategy.md)                  | Flyway as migration strategy                  |
| [0003](docs/decisions/0003-auth-keycloak.md)                    | Keycloak as identity provider                 |
| [0004](docs/decisions/0004-storage-minio.md)                    | MinIO for object storage                      |
| [0005](docs/decisions/0005-order-types-and-fulfillment.md)      | Explicit order types and fulfillment          |
| [0006](docs/decisions/0006-vat-async-admin-review.md)           | VAT verification as async admin review        |
| [0007](docs/decisions/0007-loyalty-ledger-model.md)             | Loyalty implemented as ledger model           |
| [0008](docs/decisions/0008-search-geo-with-postgis.md)          | Geo search via PostGIS                        |
| [0009](docs/decisions/0009-stock-consistency-rules.md)          | Stock consistency and transactional integrity |
| [0010](docs/decisions/0010-internal-domain-events.md)           | Internal domain events                        |
| [0011](docs/decisions/0011-reservation-expiration-scheduler.md) | Reservation expiration via scheduler + event  |
| [0012](docs/decisions/0012-openapi-as-contract.md)              | OpenAPI as first-class contract               |
| [0013](docs/decisions/0013-error-handling-standard.md)          | Standard error handling                       |
| [0014](docs/decisions/0014-code-formatting-spotless.md)         | Code formatting via Spotless                  |
| [0015](docs/decisions/0015-mapping-via-mapstruct.md)            | DTO mapping via MapStruct                     |

---

## Auth

The application is an OAuth2 Resource Server. The client authenticates directly with Keycloak,
and the backend validates the JWT token from the `Authorization: Bearer ...` header.

### Keycloak Realm Export

```shell
./scripts/export-keycloak-realm-from-docker.sh
```

---

## References

* [Building a Location-Based REST API with OpenStreetMap, PostGIS, and Spring Boot](https://www.antanaskovic.com/en/blog/building-location-based-rest-api-with-osm-postgis-and-spring-boot)
* [Maven docs](https://maven.apache.org/guides/index.html)
* [Spring Boot reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
* [Spring Data JPA reference](https://docs.spring.io/spring-data/jpa/reference/jpa.html)
