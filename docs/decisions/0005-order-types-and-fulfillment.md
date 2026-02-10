# ADR-0005 – Explicit Order Types and Fulfillment

## Status

Accepted

## Context

System must support multiple purchasing modes:

- Direct purchase
- Reserve and pickup
- Pay and pickup
- Pay and deliver

These flows have different lifecycle and stock behavior.

## Decision

Introduce explicit OrderType:

- DIRECT
- RESERVE_PICKUP
- PAY_PICKUP
- PAY_DELIVER

OrderFulfillment entity handles:

- Pickup logic
- Delivery logic
- Expiration logic

Shipment exists only for PAY_DELIVER.

## Rationale

- Avoid implicit branching
- Explicit domain modeling
- Clear business rules per type

## Stock Rules

- Stock reduction happens at order creation
- Expired RESERVE_PICKUP restores stock
- PAY_DELIVER creates Shipment record

## Consequences

- OrderService must branch by OrderType
- Order invariants must be enforced transactionally
