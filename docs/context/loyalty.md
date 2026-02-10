# Loyalty System

## Model

Loyalty is **ledger-based**: `LoyaltyPointTransaction` records are the **source of truth**.

`CustomerProfile.pointsBalance` is always derived from `SUM(transactions)`.

## Rules

- Points earned on completed order
- Points recorded as immutable transactions
- No direct mutation of balance — ever
- Balance derived from transaction sum

---

## Transaction Types

- ACCRUAL — points earned on purchase
- REDEEM — points spent
- EXPIRATION — points expired

---

## Invariants

- No negative balance
- Every balance change must have a corresponding `LoyaltyPointTransaction`
- Balance is recalculable from transaction history at any time
- `CustomerProfile` holds the cached balance; transactions are the source of truth
