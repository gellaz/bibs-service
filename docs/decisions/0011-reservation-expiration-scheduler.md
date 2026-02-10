# ADR-0011 – Reservation Expiration via Scheduled Job + Domain Event

## Status

Accepted

## Context

The system supports the purchase mode: RESERVE_PICKUP.

Requirement:

- Reserved items are held for a limited period.
- If the customer does not pick up before expiry, items must be released and returned for sale.

Constraints:

- The system is a Spring Boot monolith.
- No external message broker or scheduler is required initially.
- Expiration must be reliable and safe under concurrency.

Reservations are represented as Orders with:

- OrderType = RESERVE_PICKUP
- A hold expiry timestamp (e.g. `holdExpiresAt`)
- A status representing reservation lifecycle (e.g. RESERVED -> EXPIRED or COMPLETED)

## Decision

Implement reservation expiration as a periodic scheduled job that:

1. Finds expired reservations in the database
2. Transitions them to EXPIRED
3. Releases stock transactionally
4. Publishes an internal domain event: `ReservationExpired`

Event handlers may then perform non-critical side effects such as notifications or analytics.

### Scheduling approach

- Use a periodic job (e.g. every 1 minute) inside the monolith.
- The job is responsible only for expiration detection + state transition.
- Side effects are handled via internal events.

### Concurrency strategy

To avoid double-processing in multi-instance deployments:

- Use database-level locking during selection/processing (e.g. `SELECT ... FOR UPDATE SKIP LOCKED`)
- Process in batches

### Idempotency

The job must be idempotent:

- Only reservations in a valid state (e.g. RESERVED) are eligible.
- If already EXPIRED/COMPLETED/CANCELLED, no action is performed.
- Stock release must be protected from double-release by state transition guard.

### Transactional boundary

For each reservation processed:

- Perform state transition and stock release in a single transaction.
- Publish `ReservationExpired` as an AFTER_COMMIT domain event.

### Failure handling

- If the job fails mid-batch, unprocessed rows remain eligible for future runs.
- If an event handler fails, the reservation remains expired (core correctness preserved).

## Rationale

- Simple and reliable for a monolith
- Does not require external infrastructure
- Keeps core correctness in one transactional flow
- Decouples secondary side effects via events
- Works in single-instance and multi-instance deployments with DB locking

## Consequences

- Requires an index on `holdExpiresAt` and reservation status for fast queries
- Adds a background process that must be monitored
- Requires careful design of statuses and transitions

## Implementation Notes

### Eligibility query (conceptual)

Eligible reservations:

- OrderType = RESERVE_PICKUP
- status = RESERVED
- holdExpiresAt <= now()

### Suggested processing loop

- Fetch N eligible reservations with locking
- For each reservation:
  - transition to EXPIRED
  - release stock
  - publish `ReservationExpired(orderId, storeId, userId, expiredAt)`

### Suggested indexes

- Composite index on (order_type, status, hold_expires_at)

### Observability

- Log expired reservation counts per run
- Emit metrics:
  - processed_count
  - failed_count
  - duration_ms

## Alternatives Considered

1. Per-reservation timers

- Rejected: too complex and unreliable across restarts.

1. External scheduler + queue (Kafka/Rabbit/Quartz cluster)

- Rejected for now: increases operational complexity.

1. Database triggers

- Rejected: logic hidden from application code and harder to test.
