# ADR-0007 – Loyalty Implemented as Ledger Model

## Status

Accepted

## Context

Customers accumulate points.

Points must be:

- Auditable
- Deterministic
- Safe from corruption

## Decision

Use transaction-based ledger model.

LoyaltyPointTransaction is source of truth.

Types:

- ACCRUAL
- REDEEM
- EXPIRATION

CustomerProfile.pointsBalance = sum(transactions)

Direct mutation of balance is forbidden.

## Rationale

- Prevent inconsistencies
- Auditability
- Easy debugging

## Invariants

- Balance must never go negative
- Every balance change must have a transaction

## Consequences

- All loyalty updates must create a transaction
- Balance recalculable from history
