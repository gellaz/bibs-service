# ADR-0009 – Stock Consistency and Transactional Integrity

## Status

Accepted

## Context

Incorrect stock handling can break the business.

System supports:

- Reservations
- Payments
- Deliveries

## Decision

Stock changes must:

- Be transactional
- Be performed only inside OrderService
- Never be modified directly by controllers

Rules:

- Stock decreases at order creation
- Expired reservations restore stock
- Cancellation restores stock
- Completed order does not modify stock further

## Invariants

- Stock cannot go below zero
- Concurrent order creation must be safe
- All stock changes logged

## Rationale

- Prevent overselling
- Maintain data integrity
- Ensure predictable behavior

## Consequences

- Use database-level locking where necessary
- Service layer responsible for enforcing invariants
