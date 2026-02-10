# ADR-0001 – Monolith with Package-by-Feature

## Status

Accepted

## Context

The backend is a Spring Boot 4 application serving a local commerce e-commerce platform.

The system must support:

- Multi-store ownership
- Product management
- Order flows
- Loyalty
- VAT verification
- Geo search

The domain is cohesive and tightly coupled.

## Decision

The system will be implemented as:

- A single Spring Boot monolith
- Package-by-feature structure
- Layered architecture inside each feature

Example:

product/
  Product.java
  ProductRepository.java
  ProductService.java
  ProductResource.java

## Rationale

- Lower operational complexity
- No distributed transactions
- Easier debugging
- Simpler deployment
- Clear aggregate boundaries

## Consequences

- All modules share a single database
- Strong discipline required to maintain aggregate isolation
- Service layer enforces domain rules
