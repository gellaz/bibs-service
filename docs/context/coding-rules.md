# Coding Rules

## Layering

Controller → Service → Repository

No business logic in controllers.

---

## OpenAPI Rules (MANDATORY)

- Every endpoint must be documented with OpenAPI annotations.
- No undocumented endpoints allowed.
- Never expose JPA entities directly.
- Always use DTOs for API layer.
- Every endpoint must define:
  - Summary
  - Description
  - Response codes
  - Error responses
- All enums must be documented in schema.
- All security requirements must be documented.

---

## Flyway

- At the beginning modify the initial migration V001__CREATE_INITIAL.sql without adding new migrations.
- Once migrations are applied in production, never edit them — only append new versioned migrations.
- Naming format: `V###__DESCRIPTION.sql`

---

## Security

- Always validate store ownership
- Always validate VAT VERIFIED before store/product operations
- Document required roles in OpenAPI

---

## Stock Safety

- Stock changes must be transactional
- Never reduce stock outside order flow

---

## Loyalty Safety

- Never update `CustomerProfile.pointsBalance` without creating a `LoyaltyPointTransaction`

---

## Error Handling (MANDATORY)

- The project uses `error-handling-spring-boot-starter`.
- Do not create custom error response payload formats.
- Throw domain exceptions and rely on the standardized handler output.
- OpenAPI must document error responses consistently.

---

## Formatting (MANDATORY)

- Spotless runs in Maven `validate`.
- Code must comply with google-java-format, enforced import order, no wildcard imports.

---

## Mapping (MANDATORY)

- Never expose JPA entities in REST APIs.
- Use DTOs + MapStruct mappers for Entity ↔ DTO mapping.
- Avoid business logic inside mappers.
