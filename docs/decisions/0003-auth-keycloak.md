# ADR-0003 – Keycloak as Identity Provider

## Status

Accepted (updated)

## Context

The platform requires:

- Email/password authentication
- Role-based access control
- JWT tokens
- Admin-only VAT verification
- Multi-role users
- Three frontend applications (customer app, seller portal, admin panel)

## Decision

Use Keycloak 26 as IAM with a single realm (`bibs`).

### Realm Roles

- `ADMIN` — back-office operations (VAT verification, user management)
- `USER` — all regular users (customers and sellers alike)

No client-level roles. All business capabilities are derived from database profiles.

### Keycloak Clients

| Client          | Purpose                  | Redirect URIs    |
|-----------------|--------------------------|------------------|
| `bibs-customer` | Customer-facing web app  | `localhost:3000` |
| `bibs-seller`   | Seller management portal | `localhost:3001` |
| `bibs-admin`    | Admin panel (ADMIN role) | `localhost:3002` |
| `bibs-swagger`  | Swagger UI (dev only)    | `localhost:8080` |

All clients are **public** (PKCE), standard flow, with the `realm roles` protocol mapper.

### Capability-Based Authorization

Business permissions are stored in the database, not in Keycloak:

| Capability | Database entity                         | Meaning                                         |
|------------|-----------------------------------------|-------------------------------------------------|
| Seller     | `SellerProfile`                         | User can manage stores (after VAT verification) |
| Customer   | `CustomerProfile`                       | User can search and purchase                    |
| Store role | `StoreMember` (OWNER / MANAGER / CLERK) | User has a role within a specific store         |

A user can have both `SellerProfile` and `CustomerProfile` (cross-onboarding).

### Automatic Profile Creation

`UserSynchronizationService` reads the `azp` (authorized party) JWT claim on first login:

- `bibs-customer` → creates `User` + `CustomerProfile`
- `bibs-seller` → creates `User` only (seller onboarding requires VAT submission)
- `bibs-admin` → creates `User` only (admin panel, ADMIN role required)
- `bibs-swagger` → creates `User` only

## Rationale

- Mature OIDC implementation with PKCE support
- Realm roles for coarse-grained access (admin vs user)
- Database-driven capabilities for fine-grained business logic — no sync issues with Keycloak
- `azp` claim enables automatic profile creation without client-level roles

## Consequences

- Keycloak realm configuration is source of truth for **authentication**
- Authorization is split:
  - Realm-level: `ADMIN` role (JWT)
  - Domain-level: profiles and store membership (database)
- No client roles to maintain or synchronize
- Adding new capabilities = new database entity, not Keycloak configuration
