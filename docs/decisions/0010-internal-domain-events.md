# ADR-0010 – Internal Domain Events (Event-Driven Design in a Monolith)

## Status

Accepted

## Context

The system is a Spring Boot monolith with multiple business flows:

- Orders (DIRECT, RESERVE_PICKUP, PAY_PICKUP, PAY_DELIVER)
- Stock reservation and release
- VAT verification (async manual review)
- Loyalty accrual (ledger)
- Shipment creation (PAY_DELIVER)

These flows require side effects such as:

- updating loyalty transactions after order completion
- releasing expired reservations
- notifying admins of pending VAT verification
- creating shipment records and sending notifications

Implementing all side effects directly inside services leads to:

- large, hard-to-test services
- tight coupling between features
- fragile change management

## Decision

Introduce internal domain events to decouple side effects from core use-cases.

### Event scope

- Events are INTERNAL to the monolith.
- No external message broker is required initially.
- Events are emitted only by the service layer.

### Event mechanism

- Use Spring application events for in-process dispatch.
- Use transactional event listeners to ensure events are handled only after successful commits.

### Naming and location

- Events are named in past tense: `OrderCompleted`, `VatVerificationRequested`, `ReservationExpired`.
- Events live in `it.bibs.events.domain` (or equivalent), not inside feature packages.

### When to emit events

Emit events only at meaningful business milestones, e.g.:

- `OrderPaid`
- `OrderCompleted`
- `OrderCancelled`
- `ReservationCreated`
- `ReservationExpired`
- `VatVerificationRequested`
- `VatVerified`
- `VatRejected`
- `ProductDeleted` (if storage cleanup required)

### Handlers

Event handlers:

- MUST be idempotent where possible
- MUST not change core state already committed in the same transaction
- MUST not throw exceptions that break user flows
- SHOULD be small and focused (one responsibility)

### Transactional rules

- Events that trigger side effects relying on DB state MUST be handled after commit.
- Event handlers can start their own transaction if needed.

### Outbox policy (future)

If events must be delivered externally, introduce an Outbox table and publisher.
This is explicitly out-of-scope for now.

## Rationale

- Keeps core services small and focused
- Reduces coupling across features
- Improves testability
- Enables future evolution to async/broker or outbox without rewriting business logic

## Consequences

- Event contracts become part of the architecture and must be versioned carefully
- Requires conventions for event naming, packaging, and listener behavior
- Debugging needs tracing/logging for emitted events

## Implementation Notes

- Use `@TransactionalEventListener(phase = AFTER_COMMIT)` for handlers that require committed DB state.
- Prefer publishing events from the service layer after state changes.
- Add structured logging: event name, correlation id, aggregate id.
