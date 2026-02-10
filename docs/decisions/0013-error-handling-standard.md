# ADR-0013 – Standard Error Handling via error-handling-spring-boot-starter

## Status

Accepted

## Context

The backend exposes REST APIs consumed by web and mobile clients.

Consistency of error responses is critical for:

- client integration stability
- predictable troubleshooting
- AI-assisted development (Cursor/Claude) without inventing new error formats

The project uses `io.github.wimdeblauwe:error-handling-spring-boot-starter`.

## Decision

Use `error-handling-spring-boot-starter` as the single mechanism for API error responses.

### Rules

- Controllers and services throw domain exceptions (e.g. NotFoundException, UnauthorizedException, ReferencedException).
- Do NOT implement ad-hoc `@ControllerAdvice` error payloads that conflict with the starter.
- All new error cases must map to the existing standardized format produced by the starter.
- Validation errors must also follow the same standardized output.
- API documentation (OpenAPI) must document the standard error response schema for all endpoints.

## Rationale

- Ensures a stable and consistent error contract
- Reduces duplicated boilerplate
- Improves frontend development experience
- Reduces AI hallucinations about error payload shapes

## Consequences

- Error payload shape is controlled by the starter library
- Changes to error contract must be reviewed carefully
- Customization must be done through the starter’s extension points, not by replacing it
