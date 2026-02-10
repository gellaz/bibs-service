# ADR-0003 – Keycloak as Identity Provider

## Status

Accepted

## Context

The platform requires:

- Email/password authentication
- Role-based access control
- JWT tokens
- Admin-only VAT verification
- Multi-role users

## Decision

Use Keycloak 26 as IAM.

Realm roles:

- ADMIN
- USER

Business roles stored in database:

- CUSTOMER
- STORE_OWNER
- STORE_EMPLOYEE

Backend validates:

- JWT via OIDC
- Business permissions via database membership

## Rationale

- Mature OIDC implementation
- Role mapping support
- Secure token handling
- Easy admin management

## Authorization Strategy

- ADMIN → can verify VAT
- STORE_OWNER → manage stores
- STORE_EMPLOYEE → manage products of assigned stores
- CUSTOMER → search and purchase

## Consequences

- Keycloak realm configuration is source of truth for authentication
- Authorization is split:
  - Realm-level (ADMIN)
  - Domain-level (store membership)
