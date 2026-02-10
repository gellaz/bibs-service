# ADR-0012 – OpenAPI as First-Class Contract

## Status

Accepted

## Context

The backend exposes REST APIs consumed by:

- Web application
- Mobile application
- Possibly future third-party integrations

The system is developed with AI-assisted tools (Cursor, Claude Code).

Clear API contracts are critical for:

- Avoiding ambiguity
- Preventing breaking changes
- Enabling safe refactoring
- Improving AI-generated code accuracy

## Decision

OpenAPI documentation is mandatory and treated as a first-class contract.

### Requirements

- Every REST endpoint must be documented.
- All request and response schemas must be explicitly defined.
- No undocumented endpoints allowed.
- No generic "Object" responses.
- All enums must be documented with allowed values.
- All error responses must be documented.

### Error responses

- The API uses `error-handling-spring-boot-starter` for the standard error payload.
- OpenAPI must document the standard error response schema and reference it across endpoints.

### Swagger UI

- Swagger UI must always be enabled in non-production environments.
- OAuth2 integration with Keycloak must be configured.

### Documentation Level

Each endpoint must clearly specify:

- Purpose
- Required roles
- Input DTO schema
- Response DTO schema
- Possible HTTP status codes
- Error response structure

### Versioning

- Breaking API changes require version bump.
- Minor additive changes do not require version bump.
- Deprecated endpoints must be annotated and documented.

## Rationale

- API becomes explicit contract
- Reduces AI hallucinations
- Improves frontend-backend collaboration
- Enables automated testing from OpenAPI spec

## Consequences

- Additional development overhead
- DTO discipline required
- Clear separation between Entity and API schema
