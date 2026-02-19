# ADR-0017 – Separate Frontend Applications (Customer, Seller, Admin)

## Status

Accepted (updated for three apps)

## Context

The platform serves two very different user groups:

- **Customers**: search products, browse stores, place orders, earn loyalty points
- **Sellers**: manage store, products, orders, employees, view analytics

Each group has distinct UX needs, navigation flows, and feature sets.
Additionally, an ADMIN role exists for back-office operations (VAT verification, user management).

Options considered:

1. **1 app** — single SPA for all roles (customer, seller, admin)
2. **2 apps** — customer app + seller portal; admin panel embedded in customer app
3. **3 apps** — customer app + seller portal + admin app

Full-stack JS alternatives (Next.js API routes, Hono, Elysia, TanStack Start server functions)
were evaluated as backend replacements but dismissed: the domain complexity (transactional stock,
loyalty ledger, domain events, scheduled jobs, PostGIS, pessimistic locking) requires the mature
transaction management and enterprise patterns provided by Spring Boot + JPA.

A mixed frontend stack (Next.js for customer + Vite/TanStack Router for seller) was also considered.
The chosen approach is a **unified TanStack Start stack** for both apps: same routing paradigm (TanStack Router),
same auth pattern (Arctic + Keycloak OIDC), same deploy pipeline, type-safe file-based routing,
and first-class SSR/streaming support via Nitro. The customer app uses SSR for SEO; the seller portal
primarily uses client-side rendering behind login.

## Decision

**Option 2 (extended): Three TanStack Start applications** — customer, seller, admin — backed by the Spring Boot
monolith.

> *Note: Originally documented as Next.js (ADR v1). Updated to reflect current implementation using TanStack Start.*

### System Architecture

```text
                       ┌─────────────────┐
                       │   Keycloak 26   │
                       └────────┬────────┘
                                │ JWT
            ┌─────────────┬─────────────┬─────────────┐
            │             │             │             │           
      ┌─────┴─────┐ ┌─────┴─────┐ ┌─────┴─────┐ ┌─────┴─────┐
      │ Customer  │ │  Seller   │ │   Admin   │ │  Swagger  │
      │   App     │ │  Portal   │ │   Panel   │ │    UI     │
      │ TanStack  │ │ TanStack  │ │ TanStack  │ │(dev only) │
      │  Start    │ │  Start    │ │  Start    │ │ port 8080 │
      │port 3000  │ │port 3001  │ │port 3002  │ │           │
      └─────┬─────┘ └─────┬─────┘ └─────┬─────┘ └─────┬─────┘
            │             │             │             │
            └─────────────┴──────┬──────┴─────────────┘
                                 │ REST + Bearer JWT
                         ┌───────┴───────┐
                         │ Spring Boot 4 │
                         │(bibs-service) │
                         └───────┬───────┘
                                 │
             ┌───────────────────┼───────────────────┐
             │                   │                   │
        PostgreSQL             MinIO              Keycloak
        + PostGIS                                (admin API)
```

### Frontend Stack

| App               | Framework      | SSR                   | Why                                                   |
|-------------------|----------------|-----------------------|-------------------------------------------------------|
| **Customer App**  | TanStack Start | Yes                   | SEO required for product/store/search pages           |
| **Seller Portal** | TanStack Start | Optional (mainly CSR) | Behind login, no SEO; unified stack with customer app |

The admin panel is a separate TanStack Start app (`web-admin`), accessible only to users with the `ADMIN` realm role.

### Monorepo Structure

```text
bibs-frontend/
  apps/
    web-customer/      ← TanStack Start (bibs.it)
    web-seller/        ← TanStack Start (seller.bibs.it)
    web-admin/         ← TanStack Start (admin.bibs.it)
  packages/
    ui/                ← Shared React components (shadcn/ui + Tailwind)
    api-client/        ← TypeScript types auto-generated from OpenAPI (Orval)
    auth/              ← Arctic + Keycloak OIDC (shared config)
    shared/            ← Utils, constants, Zod validation schemas
```

### Shared Libraries

| Package      | Content                                                         | Shared                 |
|--------------|-----------------------------------------------------------------|------------------------|
| `ui`         | Design system (shadcn/ui + Tailwind CSS v4)                     | Yes                    |
| `api-client` | Types generated from `http://localhost:8080/api-docs` via Orval | Yes                    |
| `auth`       | Arctic with Keycloak OIDC provider                              | Yes (identical config) |
| `shared`     | Zod schemas, constants, formatters                              | Yes                    |

### Key Technology Choices

- **TanStack Start** — full-stack React framework (Vite + Nitro) for both apps; SSR where needed (customer), CSR
  elsewhere (seller)
- **TanStack Router** — file-based routing, type-safe search params, server functions
- **Arctic** — Keycloak OIDC integration (PKCE, token refresh), shared between both apps
- **Tailwind CSS v4** — utility-first styling, shared design tokens
- **shadcn/ui** — copy-paste components (not a dependency), highly customizable
- **TanStack Query** — data fetching and cache in both apps
- **Orval** — type-safe API client generated from backend OpenAPI spec
- **Nitro** — server runtime for TanStack Start (SSR, API routes, server functions)

### Keycloak Clients

| Client          | App           | Dev Port |
|-----------------|---------------|----------|
| `bibs-customer` | Customer App  | 3000     |
| `bibs-seller`   | Seller Portal | 3001     |
| `bibs-admin`    | Admin Panel   | 3002     |
| `bibs-swagger`  | Swagger UI    | 8080     |

## Rationale

- **UX separation**: customers and sellers have fundamentally different needs. A single app would lead to confusing
  navigation and bloated bundles.
- **Unified stack**: both apps use TanStack Start — same routing paradigm (TanStack Router), same auth pattern (Arctic +
  Keycloak),
  same deploy pipeline. Developers only need to learn one framework.
- **SSR only where needed**: the customer app uses SSR for SEO-critical pages (products, stores, search).
  The seller portal is primarily client-side — no SSR overhead where not needed.
- **Spring Boot as domain server**: the backend handles complex domain logic (transactions, events, schedulers, PostGIS)
  that JS/TS backend frameworks cannot match in maturity. Frontends are thin clients consuming a REST API.
- **Bundle size**: each app only ships code for its audience. Customer app stays lightweight.
- **Admin as separate app**: the admin panel (VAT verification, user management) is a dedicated app for back-office,
  isolated from customer and seller flows.
- **Keycloak alignment**: separate clients (`bibs-customer`, `bibs-seller`, `bibs-admin`) map 1:1 to the three apps.
  The `azp` claim drives automatic profile creation (e.g. customer profile only for `bibs-customer`).
- **Independent deployability**: each app can be deployed, versioned, and scaled independently.
- **Type-safe DX**: TanStack Router provides file-based routing with type-safe search params; TanStack Start server
  functions
  enable colocated backend logic where needed (auth callbacks, session handling).

## Consequences

- Two separate TanStack Start apps sharing code via monorepo.
- The Spring Boot backend API serves both apps — no API duplication.
- API contract alignment is enforced via auto-generated TypeScript types from the OpenAPI spec.
- The `bibs-swagger` Keycloak client remains for development/testing via Swagger UI.
- Admin app is restricted to users with the `ADMIN` realm role; other apps have no access to admin-only features.
- Adding a mobile app in the future requires no backend changes — it consumes the same REST API.
