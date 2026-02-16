# Business Flows

> **Detailed onboarding flows** (API calls, profile creation, step-by-step) are documented
> in [onboarding-flows.md](onboarding-flows.md).

## Registration & Onboarding

### Identity

All users register through **Keycloak** (single realm: `bibs`).
Keycloak handles only identity: email, password, first name, last name.
Keycloak does NOT store business data (VAT number, roles, loyalty points).

Two Keycloak clients exist:

| Client          | Used by                          |
|-----------------|----------------------------------|
| `bibs-customer` | Customer app (`bibs.it`)         |
| `bibs-seller`   | Seller portal (`seller.bibs.it`) |

At first login, `UserSynchronizationService` reads the JWT and creates the `User` entity.
The JWT `azp` (authorized party) claim identifies which client the user registered from.

### Customer Registration

1. User clicks "Registrati" on the customer app
2. Keycloak registration: email, password, name
3. Redirect to customer app
4. `UserSynchronizationService` creates `User` + `CustomerProfile` (points balance = 0)
5. User is onboarded — can search, browse, and purchase

### Seller Registration

1. User clicks "Vendi su BIBS" from the landing page footer
2. Keycloak registration: email, password, name (NO VAT number — Keycloak handles identity only)
3. Redirect to seller portal
4. `UserSynchronizationService` creates `User` (no profiles yet)
5. Seller portal shows "Completa la registrazione" form → user enters VAT number
6. `POST /api/users/me/seller-profile` → `SellerProfile` created with `vatVerificationStatus = PENDING`
7. ADMIN reviews and verifies VAT (via `PUT /api/seller-profiles/{id}/verify`)
8. Once VERIFIED → seller can create stores and manage products

From the user's perspective, steps 2–6 feel like a single continuous flow.

### Cross-Onboarding

A user can acquire both profiles at any time:

| Already has     | Wants to become | How                                                   |
|-----------------|-----------------|-------------------------------------------------------|
| CustomerProfile | Seller          | `POST /api/users/me/seller-profile` (with VAT number) |
| SellerProfile   | Customer        | `POST /api/users/me/customer-profile`                 |

Both endpoints are idempotent guards: if the profile already exists, they return `409 Conflict`.

### Employee Onboarding

Employees do NOT register separately as sellers. They are regular users invited to a store:

1. Store owner adds member: `POST /api/stores/{storeId}/members` with `{ "userId": "...", "role": "MANAGER" }`
2. Employee is linked to the store with the specified role (MANAGER or CLERK)
3. Employee can manage products of assigned stores — no `SellerProfile` required

---

## Purchase Flows

### Reserve & Pickup (RESERVE_PICKUP)

1. Customer reserves product
2. Stock reduced
3. Expiration timestamp set (`holdExpiresAt`)
4. If expired → scheduler restores stock + publishes `ReservationExpired` event

### Pay & Pickup (PAY_PICKUP)

1. Customer pays online
2. Stock reduced
3. Await pickup at store

### Pay & Deliver (PAY_DELIVER)

1. Customer pays online
2. Shipment record created
3. Courier tracking stored

### Direct Purchase (DIRECT)

1. Customer buys physically in store using mobile app
2. Stock reduced
3. Order completed immediately

---

## Loyalty Accrual

1. Order completed
2. Points calculated
3. `LoyaltyPointTransaction` created (type: EARN)
4. `CustomerProfile.pointsBalance` updated

---

## VAT Verification

1. Seller submits VAT number → `SellerProfile` created (PENDING)
2. ADMIN lists pending profiles: `GET /api/seller-profiles?status=PENDING`
3. ADMIN verifies: `PUT /api/seller-profiles/{id}/verify` → VERIFIED
4. Or rejects: `PUT /api/seller-profiles/{id}/reject` → REJECTED
5. Only VERIFIED sellers can create stores
