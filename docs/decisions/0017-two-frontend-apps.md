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

## Decision

**Option 2: Two frontend applications, backed by the Spring Boot monolith.**

### System Architecture

```
                    ┌─────────────────┐
                    │   Keycloak 26   │
                    └────────┬────────┘
                             │ JWT
           ┌─────────────────┼─────────────────┐
           │                 │                  │
    ┌──────┴──────┐   ┌─────┴──────┐   ┌──────┴──────┐
    │ Customer App│   │Seller Portal│   │ Swagger UI  │
    │  (Next.js)  │   │(Vite + TSR)│   │ (dev only)  │
    │  port 3000  │   │  port 3001 │   │  port 8080  │
    └──────┬──────┘   └─────┬──────┘   └──────┬──────┘
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

| App               | Framework | Routing         | SSR      | Why                                           |
|-------------------|-----------|-----------------|----------|-----------------------------------------------|
| **Customer App**  | Next.js   | App Router      | Yes      | SEO required for product/store pages          |
| **Seller Portal** | Vite      | TanStack Router | No (CSR) | Behind login, no SEO; best DX and type-safety |

The admin panel is a protected section (`/admin`) within the customer app, accessible only to users with the `ADMIN`
realm role.

### Monorepo Structure

```
bibs-frontend/
  apps/
    customer/          ← Next.js (bibs.it)
    seller/            ← Vite + TanStack Router (seller.bibs.it)
  packages/
    ui/                ← Shared React components (shadcn/ui + Tailwind)
    api-client/        ← TypeScript types auto-generated from OpenAPI
    auth/              ← Keycloak wrapper (next-auth for customer, react-oidc for seller)
    shared/            ← Utils, constants, Zod validation schemas
```

### Shared Libraries

| Package      | Content                                                                      | Framework-agnostic |
|--------------|------------------------------------------------------------------------------|--------------------|
| `ui`         | Design system (shadcn/ui + Tailwind CSS v4)                                  | Yes                |
| `api-client` | Types generated from `http://localhost:8080/api-docs` via openapi-typescript | Yes                |
| `auth`       | Keycloak OIDC integration                                                    | Adapter per app    |
| `shared`     | Zod schemas, constants, formatters                                           | Yes                |

### Key Technology Choices

- **Tailwind CSS v4** — utility-first styling, shared between both apps
- **shadcn/ui** — copy-paste components (not a dependency), highly customizable
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
- **Right tool for each app**: Next.js provides SSR/SSG for SEO-critical customer pages; TanStack Router provides
  superior type-safety and DX for the seller portal where SEO is irrelevant.
- **Spring Boot as domain server**: the backend handles complex domain logic (transactions, events, schedulers, PostGIS)
  that JS/TS backend frameworks cannot match in maturity. Frontends are thin clients consuming a REST API.
- **Bundle size**: each app only ships code for its audience. Customer app stays lightweight.
- **Admin is lightweight**: the admin currently only manages VAT verification and seller profiles — not enough to
  justify a dedicated third app. If admin grows complex, it can be extracted later.
- **Keycloak alignment**: two clients (`bibs-customer`, `bibs-seller`) already map 1:1 to the two apps. The `azp` claim
  drives automatic profile creation.
- **Independent deployability**: each app can be deployed, versioned, and scaled independently.
- **No vendor lock-in**: the seller portal (Vite) deploys anywhere; the customer app (Next.js) can be deployed on
  Vercel, Netlify, or self-hosted.

## Consequences

- Two separate frontend codebases sharing code via Turborepo monorepo.
- The Spring Boot backend API serves both apps — no API duplication.
- API contract alignment is enforced via auto-generated TypeScript types from the OpenAPI spec.
- The `bibs-swagger` Keycloak client remains for development/testing via Swagger UI.
- Admin routes in the customer app are guarded by role checks on the `ADMIN` realm role from the JWT.
- Adding a mobile app in the future requires no backend changes — it consumes the same REST API.
