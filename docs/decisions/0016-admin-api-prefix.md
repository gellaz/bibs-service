# ADR-0016 – Separate Admin API Prefix (`/api/admin/`)

## Status

Proposed

## Context

The backend exposes REST endpoints with mixed authorization requirements:

- Public/user endpoints (e.g. `GET /api/users/me`, `GET /api/stores`)
- Admin-only endpoints (e.g. VAT verification, user lookup by ID)

Currently all endpoints share the `/api/` prefix. Admin protection relies solely on method-level
`@PreAuthorize("@acl.isAdmin()")` annotations. If an annotation is accidentally omitted on a new
admin endpoint, the endpoint is exposed to any authenticated user.

## Decision

Introduce a `/api/admin/` path prefix for all admin-only endpoints.

### URL structure

| Prefix          | Audience                | Example                                   |
|-----------------|-------------------------|-------------------------------------------|
| `/api/`         | Authenticated users     | `GET /api/users/me`, `GET /api/stores`    |
| `/api/admin/`   | ADMIN role only         | `PUT /api/admin/seller-profiles/{id}/verify` |

### Security strategy (defense in depth)

1. **HTTP filter** — `SecurityFilterChain` enforces `hasAuthority("ADMIN")` on `/api/admin/**`.
2. **Method annotation** — `@PreAuthorize("@acl.isAdmin()")` remains on each controller method.

Both layers must be present. The HTTP filter acts as a safety net.

### Migration plan

Controllers affected:

| Controller                 | Endpoints to move to `/api/admin/`                           |
|----------------------------|--------------------------------------------------------------|
| `SellerProfileResource`    | All endpoints (CRUD + verify/reject)                         |
| `UserResource`             | `GET /api/users/{userId}` (admin-only lookup)                |

Controllers NOT affected (no admin-only endpoints today):

- `StoreResource`
- `ProductResource`
- `ProductCategoryResource`

### Implementation steps

1. Create `BibsSecurityConfig` rule: `.requestMatchers("/api/admin/**").hasAuthority("ADMIN")`
2. Move `SellerProfileResource` mapping from `/api/seller-profiles` to `/api/admin/seller-profiles`
3. Extract `GET /api/users/{userId}` into a new `AdminUserResource` under `/api/admin/users`
4. Keep `@PreAuthorize` annotations as secondary guard
5. Update OpenAPI tags to reflect the admin grouping
6. Update CLAUDE.md and coding-rules.md with the new convention

## Rationale

- Defense in depth: two independent authorization layers
- Clearer intent in URLs for developers, reviewers, and AI assistants
- Better Swagger UI grouping (admin vs user-facing)
- Scalable pattern for future admin endpoints

## Consequences

- Slightly more controller classes (admin-specific controllers)
- Must keep `SecurityFilterChain` rule and method annotations in sync
- Frontend/clients must update paths for moved endpoints
