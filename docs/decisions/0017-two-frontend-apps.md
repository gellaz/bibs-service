# ADR-0017 – Two Separate Frontend Applications

## Status

Accepted

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

A mixed frontend stack (Next.js for customer + Vite/TanStack Router for seller) was also considered
but dismissed in favor of a unified Next.js stack: same routing paradigm, same auth library,
same deploy pipeline, lower cognitive overhead. The seller portal does not use SSR (pages are
client components) so the overhead is negligible.

## Decision

**Option 2: Two Next.js applications, backed by the Spring Boot monolith.**

### System Architecture

```
                    ┌─────────────────┐
                    │   Keycloak 26   │
                    └────────┬────────┘
                             │ JWT
           ┌─────────────────┼─────────────────┐
           │                 │                 │
    ┌──────┴──────┐   ┌──────┴──────┐   ┌──────┴──────┐
    │ Customer App│   │Seller Portal│   │ Swagger UI  │
    │  (Next.js)  │   │  (Next.js)  │   │ (dev only)  │
    │  port 3000  │   │  port 3001  │   │  port 8080  │
    └──────┬──────┘   └─────┬───────┘   └──────┬──────┘
           │                │                  │
           └────────────────┼──────────────────┘
                            │ REST + Bearer JWT
                   ┌────────┴────────┐
                   │  Spring Boot 4  │
                   │  (bibs-service) │
                   └────────┬────────┘
                            │
              ┌─────────────┼─────────────┐
              │             │             │
         PostgreSQL      MinIO       Keycloak
         + PostGIS                   (admin API)
```

### Frontend Stack

| App               | Framework            | SSR                    | Why                                                   |
|-------------------|----------------------|------------------------|-------------------------------------------------------|
| **Customer App**  | Next.js (App Router) | Yes                    | SEO required for product/store/search pages           |
| **Seller Portal** | Next.js (App Router) | No (client components) | Behind login, no SEO; unified stack with customer app |

The admin panel is a protected section (`/admin`) within the customer app, accessible only to users with the `ADMIN`
realm role.

### Monorepo Structure

```
bibs-frontend/
  apps/
    customer/          ← Next.js (bibs.it)
    seller/            ← Next.js (seller.bibs.it)
  packages/
    ui/                ← Shared React components (shadcn/ui + Tailwind)
    api-client/        ← TypeScript types auto-generated from OpenAPI
    auth/              ← next-auth v5 with Keycloak provider
    shared/            ← Utils, constants, Zod validation schemas
```

### Shared Libraries

| Package      | Content                                                                      | Shared                 |
|--------------|------------------------------------------------------------------------------|------------------------|
| `ui`         | Design system (shadcn/ui + Tailwind CSS v4)                                  | Yes                    |
| `api-client` | Types generated from `http://localhost:8080/api-docs` via openapi-typescript | Yes                    |
| `auth`       | next-auth v5 with Keycloak OIDC provider                                     | Yes (identical config) |
| `shared`     | Zod schemas, constants, formatters                                           | Yes                    |

### Key Technology Choices

- **Next.js (App Router)** — framework for both apps; SSR where needed (customer), CSR elsewhere (seller)
- **next-auth v5** — Keycloak OIDC integration, shared between both apps
- **Tailwind CSS v4** — utility-first styling, shared design tokens
- **shadcn/ui** — copy-paste components (not a dependency), highly customizable
- **nuqs** — type-safe URL search params for Next.js (similar DX to TanStack Router search params)
- **TanStack Query** — data fetching and cache in both apps
- **openapi-typescript + openapi-fetch** — type-safe API client generated from backend OpenAPI spec
- **Turborepo** — monorepo build orchestration with caching

### Keycloak Clients

| Client          | App           | Dev Port |
|-----------------|---------------|----------|
| `bibs-customer` | Customer App  | 3000     |
| `bibs-seller`   | Seller Portal | 3001     |
| `bibs-swagger`  | Swagger UI    | 8080     |

## Rationale

- **UX separation**: customers and sellers have fundamentally different needs. A single app would lead to confusing
  navigation and bloated bundles.
- **Unified stack**: both apps use Next.js — same routing paradigm, same auth library (next-auth), same middleware
  patterns, same deploy pipeline. Developers only need to learn one framework.
- **SSR only where needed**: the customer app uses SSR/SSG for SEO-critical pages (products, stores, search).
  The seller portal pages are client components (`"use client"`) — no SSR overhead.
- **Spring Boot as domain server**: the backend handles complex domain logic (transactions, events, schedulers, PostGIS)
  that JS/TS backend frameworks cannot match in maturity. Frontends are thin clients consuming a REST API.
- **Bundle size**: each app only ships code for its audience. Customer app stays lightweight.
- **Admin is lightweight**: the admin currently only manages VAT verification and seller profiles — not enough to
  justify a dedicated third app. If admin grows complex, it can be extracted later.
- **Keycloak alignment**: two clients (`bibs-customer`, `bibs-seller`) map 1:1 to the two apps. The `azp` claim
  drives automatic profile creation.
- **Independent deployability**: each app can be deployed, versioned, and scaled independently.

## Consequences

- Two separate Next.js apps sharing code via Turborepo monorepo.
- The Spring Boot backend API serves both apps — no API duplication.
- API contract alignment is enforced via auto-generated TypeScript types from the OpenAPI spec.
- The `bibs-swagger` Keycloak client remains for development/testing via Swagger UI.
- Admin routes in the customer app are guarded by role checks on the `ADMIN` realm role from the JWT.
- Adding a mobile app in the future requires no backend changes — it consumes the same REST API.
