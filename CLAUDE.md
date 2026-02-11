# CLAUDE.md — Guardrails for Claude Code

This file defines strict rules for AI-assisted development in this repository.
All code generation and refactoring MUST comply with these constraints.

---

## Architecture

- This is a **Spring Boot 4 monolith** with **package-by-feature** structure.
- Layering: Controller → Service → Repository. No shortcuts.
- Do NOT introduce microservices, new frameworks, or replace existing libraries.

## Flyway

- **Never edit existing applied migrations.** Only append new versioned migrations.
- Naming format: `V###__DESCRIPTION.sql` under `src/main/resources/db/migration`.
- During early development, the initial migration `V001__CREATE_INITIAL.sql` may be modified.
  Once applied in a shared environment, it becomes immutable.

## API Layer

- **Never expose JPA entities in REST APIs.** Always use DTOs.
- Use **MapStruct** for Entity ↔ DTO mapping. No manual mapping unless justified.
- **No business logic in mappers.** Mappers are pure data transformers.

## OpenAPI (Mandatory)

- **Every endpoint must be documented** with OpenAPI annotations.
- Required per endpoint: `@Operation(summary, description)`, `@ApiResponse` for all status codes, `@SecurityRequirement`
  where auth is needed.
- Request and response schemas must be explicitly typed (no generic `Object`).
- All enums must be documented.
- Swagger UI must be enabled in non-production environments.

## Error Handling

- The project uses `error-handling-spring-boot-starter` (io.github.wimdeblauwe).
- **Do NOT create ad-hoc error response formats** or custom `@ControllerAdvice` that conflicts with the starter.
- Throw domain exceptions: `NotFoundException`, `UnauthorizedException`, `ReferencedException`.
- The starter produces the standardized error payload automatically.
- OpenAPI must reference the `ApiErrorResponse` schema for error responses.

## Security

- Authentication via **Keycloak 26** (OIDC / JWT).
- Realm roles: `ADMIN`, `USER`.
- Keycloak clients:
    - `bibs-customer` — customer-facing web app (port 3000).
    - `bibs-seller` — seller management portal (port 3001).
    - `bibs-swagger` — Swagger UI (development only).
- `UserSynchronizationService` syncs users on first login:
    - `bibs-customer` → creates `User` + `CustomerProfile` automatically.
    - `bibs-seller` / `bibs-swagger` → creates `User` only (seller onboarding requires VAT).
- User capabilities are derived from profiles (capability-based model):
    - `SellerProfile` → user is a seller (store owner)
    - `CustomerProfile` → user is a customer
    - A user can have both profiles.
- **VAT VERIFIED gate**: only sellers with `vatVerificationStatus == VERIFIED` can create stores or manage products.
- **Only ADMIN can verify or reject VAT.** This is enforced in the service layer.

## Store Membership

- `StoreMember` links a User to a Store with a role: `OWNER`, `MANAGER`, `CLERK`.
- The **OWNER** is auto-assigned when a verified seller creates a store.
- Only the **OWNER** can invite or remove members.
- Employees (MANAGER, CLERK) do NOT need a `SellerProfile` — they work for someone else's store.

## Domain Events

- Use **internal domain events** (Spring application events) for side effects.
- Events are emitted by the service layer after state changes.
- Handlers use `@TransactionalEventListener(phase = AFTER_COMMIT)`.
- Event handlers must be idempotent and must not break the caller's flow.

## Reservation Expiration

- Expired reservations (RESERVE_PICKUP) are handled by a **periodic scheduler**.
- The scheduler finds expired reservations, transitions them to EXPIRED, releases stock, and publishes a
  `ReservationExpired` domain event.
- Side effects (notifications, analytics) are handled by event listeners, not the scheduler itself.
- The job must be **idempotent** and use database-level locking for concurrency safety.

## Stock Safety

- Stock changes must be **transactional** and happen only inside `OrderService`.
- Stock decreases at order creation; expired/cancelled orders restore stock.
- Stock can never go below zero. Use database-level locking where necessary.

## Loyalty

- Loyalty is **ledger-based**: `LoyaltyPointTransaction` is the source of truth.
- `CustomerProfile.pointsBalance = SUM(transactions)`. Never mutate balance directly.
- Every balance change requires a `LoyaltyPointTransaction` record.

## Formatting

- **Spotless** runs in Maven `validate` phase (google-java-format).
- No wildcard imports. Import order enforced by plugin configuration.
- All code must pass `mvnw spotless:check` before commit.

## Storage

- **MinIO** is the S3-compatible object storage.
- Access pattern: Controller → Service → StorageService → MinIO. Never access MinIO directly from controllers.
