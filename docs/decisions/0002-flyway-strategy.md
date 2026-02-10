# ADR-0002 – Flyway as Migration Strategy

## Status

Accepted

## Context

The system requires deterministic schema evolution.

Database: PostgreSQL 18.

## Decision

- Use Flyway versioned migrations
- Store migrations under:
  src/main/resources/db/migration
- Naming format:
  V###__DESCRIPTION.sql

Example:
V001__CREATE_INITIAL.sql

## Rules

- At the moment modify the initial migration
- Only append new versioned migrations
- All schema changes must go through Flyway

## Rationale

- Deterministic schema state
- Compatible with CI/CD
- Lightweight compared to Liquibase
- Easy to reason about

## Consequences

- Rollbacks must be handled manually
- Schema evolution must be forward-only
