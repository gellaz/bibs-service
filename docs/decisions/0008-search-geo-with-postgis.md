# ADR-0008 – Geo Search via PostGIS

## Status

Accepted

## Context

Products must be searchable by:

- Full-text
- Category
- Distance from user

## Decision

Use PostgreSQL with PostGIS extension.

Store entity stores:

- Latitude
- Longitude

Distance calculated at query time.

Sorting order:

1. Distance
2. Text relevance

## Rationale

- Avoid external search engines
- Keep architecture simple
- Strong geo query support

## Consequences

- PostGIS required in DB
- Search queries must be optimized
- Proper indexing required
