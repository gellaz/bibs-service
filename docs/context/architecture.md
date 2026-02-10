# Architecture

## Layering Rules

Controllers (*Resource classes*)

- Validate input
- Delegate to Service
- No persistence logic

Services

- Business logic
- Transactional boundaries
- Orchestration

Repositories

- Only data access

Entities

- JPA entities
- No Spring dependencies

## Constraints

- No direct access to repositories from controllers.
- All DB schema changes must go through Flyway.
- No business logic inside DTO mappers.
- Security decisions handled in service layer or ACL service.

## Storage

MinIO is used only through dedicated service abstractions.
Controllers must not access storage directly.
