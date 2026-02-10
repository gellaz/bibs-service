# ADR-0015 – DTO Mapping via MapStruct

## Status

Accepted

## Context

The project uses DTOs for API layer and entities for persistence.

Mapping must be:

- explicit
- testable
- maintainable
- safe from accidental exposure of JPA entities in APIs

The project uses MapStruct with Lombok binding.

## Decision

Use MapStruct as the standard mapping mechanism between:

- Entities ↔ DTOs
- Domain objects ↔ API models (if introduced later)

### Rules

- API layer must expose DTOs only (never JPA entities).
- Conversions between Entity and DTO must be implemented via MapStruct mappers.
- Hand-written mapping is allowed only for complex cases and must be justified in code comments.
- Mappers should be placed near the feature package (e.g. `product/ProductMapper`).
- Avoid putting business logic in mappers.

## Rationale

- Compile-time safety
- Clear mapping contracts
- Consistent patterns for AI assistants

## Consequences

- DTO design becomes explicit and structured
- Changes to DTO fields must update mappers (compile-time feedback)
