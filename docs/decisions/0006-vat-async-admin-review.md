# ADR-0006 – VAT Verification as Async Admin Review

## Status

Accepted

## Context

Store owners must provide VAT number.

VAT must be verified before allowing commercial activity.

Verification is not automatic.

## Decision

VAT status stored in SellerProfile:

- PENDING
- VERIFIED
- REJECTED

Flow:

1. Owner registers → VAT = PENDING
2. ADMIN reviews VAT
3. ADMIN sets status

Only VERIFIED owners can:

- Create stores
- Manage products

## Enforcement

Service layer must enforce:

if (vatStatus != VERIFIED) → throw UnauthorizedException

Only ADMIN can modify VAT status.

## Rationale

- Business compliance requirement
- Clear authorization boundary
- Explicit state machine

## Consequences

- Store activation gated by VAT
- ADMIN APIs required
- Status transitions must be auditable
