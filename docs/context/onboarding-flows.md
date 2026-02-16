# Onboarding Flows

This document describes the complete onboarding flows for **Customers** and **Sellers** in BIBS, including
authentication, profile creation, API calls, and backend behaviour.

---

## Overview

| User Type | Keycloak Client | First API Call      | Profile Creation                                 |
|-----------|-----------------|---------------------|--------------------------------------------------|
| Customer  | `bibs-customer` | `GET /api/users/me` | Automatic at first login                         |
| Seller    | `bibs-seller`   | `GET /api/users/me` | Explicit via `POST /api/users/me/seller-profile` |

Identity is managed by Keycloak (realm `bibs`). Business profiles (`CustomerProfile`, `SellerProfile`) are stored in the
database and created/synchronized based on the Keycloak client used during login.

---

## Customer Onboarding

### Flow Summary

1. User registers on the customer app (e.g. bibs.it) via Keycloak
2. Keycloak redirects back with an authorization code
3. App exchanges code for tokens and establishes session
4. **First API request**: `GET /api/users/me` — triggers `UserSynchronizationService`
5. Backend creates `User` + `CustomerProfile` automatically (points balance = 0)
6. User is fully onboarded — can search, browse, and purchase

### Step-by-Step (Backend Perspective)

| Step | Actor    | Action                                                    | Result                             |
|------|----------|-----------------------------------------------------------|------------------------------------|
| 1    | User     | Clicks "Registrati" on customer app                       | Redirect to Keycloak registration  |
| 2    | User     | Enters email, password, name in Keycloak                  | Account created in Keycloak        |
| 3    | Keycloak | Redirects to `{app}/api/auth/callback?code=...&state=...` |                                    |
| 4    | App      | Exchanges code for tokens, validates state                | Session with JWT                   |
| 5    | App      | First authenticated request (e.g. `GET /api/users/me`)    | Bearer token sent                  |
| 6    | Backend  | `UserSynchronizationService` sees `azp=bibs-customer`     | Creates `User` + `CustomerProfile` |
| 7    | Backend  | Returns `UserDTO` with `customerProfile`                  | User fully onboarded               |

### Automatic Profile Creation

The `UserSynchronizationService` listens to `AuthenticationSuccessEvent`. On first successful JWT authentication:

- Reads `azp` (authorized party) claim from the JWT
- If `azp == "bibs-customer"` and user is **new** (not in DB): creates `User` + `CustomerProfile` with
  `pointsBalance = 0`
- If user already exists: only updates email, firstName, lastName

**No explicit API call is required** to create the customer profile when using `bibs-customer`.

### API Reference (Customer Context)

| Method | Endpoint                         | Description                    | When Used                                                             |
|--------|----------------------------------|--------------------------------|-----------------------------------------------------------------------|
| GET    | `/api/users/me`                  | Get current user with profiles | After login — triggers sync; returns `UserDTO` with `customerProfile` |
| POST   | `/api/users/me/customer-profile` | Create customer profile        | Cross-onboarding only (seller becoming customer)                      |

### Cross-Onboarding: Seller → Customer

If a user already has a `SellerProfile` and wants to also become a customer (e.g. to purchase as well as sell):

```
POST /api/users/me/customer-profile
Authorization: Bearer <token>
```

- **201**: Customer profile created (zero loyalty points)
- **409**: User already has a customer profile

---

## Seller Onboarding

### Flow Summary

1. User registers on the seller portal (e.g. seller.bibs.it) via Keycloak
2. Keycloak redirects back with authorization code
3. App exchanges code for tokens and establishes session
4. **First API request**: `GET /api/users/me` — triggers `UserSynchronizationService`
5. Backend creates `User` only (no profiles yet, because `azp=bibs-seller`)
6. App detects no `sellerProfile` → redirects to `/onboarding`
7. User enters VAT number in onboarding form
8. **Explicit API call**: `POST /api/users/me/seller-profile` with `{ "vatNumber": "12345678901" }`
9. Backend creates `SellerProfile` with `vatVerificationStatus = PENDING`
10. ADMIN verifies VAT asynchronously via `PUT /api/seller-profiles/{id}/verify`
11. Once VERIFIED → seller can create stores and manage products

### Step-by-Step (Backend + API)

| Step | Actor    | Action                                                    | API / Backend                                    |
|------|----------|-----------------------------------------------------------|--------------------------------------------------|
| 1    | User     | Clicks "Vendi su BIBS" / login on seller portal           | Redirect to Keycloak                             |
| 2    | User     | Registers with email, password, name (no VAT in Keycloak) |                                                  |
| 3    | Keycloak | Redirects to callback                                     |                                                  |
| 4    | App      | Exchanges code for tokens                                 | Session established                              |
| 5    | App      | `GET /api/users/me`                                       | `UserSynchronizationService` creates `User` only |
| 6    | App      | Response: `UserDTO` without `sellerProfile`               | Redirect to `/onboarding`                        |
| 7    | User     | Enters VAT number (11 digits) in onboarding form          |                                                  |
| 8    | App      | `POST /api/users/me/seller-profile`                       | See below                                        |
| 9    | Backend  | Creates `SellerProfile` (PENDING)                         |                                                  |
| 10   | ADMIN    | Reviews pending profiles                                  | `GET /api/seller-profiles?status=PENDING`        |
| 11   | ADMIN    | Verifies VAT                                              | `PUT /api/seller-profiles/{id}/verify`           |
| 12   | Seller   | Can create stores / manage products                       | `vatVerificationStatus == VERIFIED`              |

### API Reference (Seller Onboarding)

#### Get Current User

```
GET /api/users/me
Authorization: Bearer <JWT>
```

**Response 200:**

```json
{
  "id": "uuid",
  "email": "user@example.com",
  "firstName": "Mario",
  "lastName": "Rossi",
  "identitySubject": "keycloak-sub-...",
  "sellerProfile": null,
  "customerProfile": null
}
```

After onboarding:

```json
{
  "sellerProfile": {
    "id": "uuid",
    "vatNumber": "12345678901",
    "vatVerificationStatus": "PENDING"
  }
}
```

#### Onboard as Seller (Create Seller Profile)

```
POST /api/users/me/seller-profile
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "vatNumber": "12345678901"
}
```

| Status | Meaning                                                            |
|--------|--------------------------------------------------------------------|
| 201    | Seller profile created — VAT status = PENDING                      |
| 400    | Validation error (VAT must be exactly 11 digits)                   |
| 409    | User already has a seller profile **or** VAT number already in use |

**Request body (`SellerOnboardingRequest`):**

- `vatNumber`: string, exactly 11 characters, digits only (Italian VAT format)

#### Admin: List Pending Seller Profiles

```
GET /api/seller-profiles?status=PENDING
Authorization: Bearer <ADMIN_JWT>
```

Requires `ADMIN` realm role.

#### Admin: Verify VAT

```
PUT /api/seller-profiles/{sellerProfileId}/verify
Authorization: Bearer <ADMIN_JWT>
```

| Status | Meaning                      |
|--------|------------------------------|
| 200    | VAT verified successfully    |
| 404    | Seller profile not found     |
| 409    | VAT is not in PENDING status |

#### Admin: Reject VAT

```
PUT /api/seller-profiles/{sellerProfileId}/reject
Authorization: Bearer <ADMIN_JWT>
```

| Status | Meaning                      |
|--------|------------------------------|
| 200    | VAT rejected successfully    |
| 404    | Seller profile not found     |
| 409    | VAT is not in PENDING status |

### VAT Verification Status

| Status   | Meaning               | Seller Can…                                  |
|----------|-----------------------|----------------------------------------------|
| PENDING  | Awaiting ADMIN review | No — cannot create stores or manage products |
| VERIFIED | Approved by ADMIN     | Yes — full seller capabilities               |
| REJECTED | Rejected by ADMIN     | No                                           |

See [vat-verification.md](vat-verification.md) for details.

### Cross-Onboarding: Customer → Seller

If a user already has a `CustomerProfile` and wants to become a seller:

```
POST /api/users/me/seller-profile
Authorization: Bearer <token>
Content-Type: application/json

{ "vatNumber": "12345678901" }
```

Same behaviour as initial seller onboarding. Both profiles can coexist.

---

## Cross-Onboarding Summary

| Already has     | Wants to become | API Endpoint                                          |
|-----------------|-----------------|-------------------------------------------------------|
| CustomerProfile | Seller          | `POST /api/users/me/seller-profile` (with VAT number) |
| SellerProfile   | Customer        | `POST /api/users/me/customer-profile`                 |

Both endpoints are idempotent guards: if the profile already exists, they return `409 Conflict`.

---

## Keycloak Clients

| Client          | App                            | Port | Profile Creation on First Login |
|-----------------|--------------------------------|------|---------------------------------|
| `bibs-customer` | Customer app (bibs.it)         | 3000 | `User` + `CustomerProfile`      |
| `bibs-seller`   | Seller portal (seller.bibs.it) | 3001 | `User` only                     |
| `bibs-swagger`  | Swagger UI (dev)               | 8080 | `User` only                     |

The `azp` claim in the JWT identifies which client the user authenticated with.

---

## Backend Components

| Component                                          | Responsibility                                                                                             |
|----------------------------------------------------|------------------------------------------------------------------------------------------------------------|
| `UserSynchronizationService`                       | Creates/updates `User` on `AuthenticationSuccessEvent`; auto-creates `CustomerProfile` for `bibs-customer` |
| `UserService.onboardAsSeller()`                    | Creates `SellerProfile` with PENDING status; checks for duplicates                                         |
| `UserService.onboardAsCustomer()`                  | Creates `CustomerProfile` with zero balance; checks for duplicates                                         |
| `SellerProfileService.requireVerifiedSeller()`     | Enforces VAT VERIFIED gate before store/product operations                                                 |
| `SellerProfileService.verifyVat()` / `rejectVat()` | ADMIN-only VAT status transitions                                                                          |

---

## Related Documentation

- [flows.md](flows.md) — High-level business flows
- [authz-model.md](authz-model.md) — Authorization and capabilities
- [vat-verification.md](vat-verification.md) — VAT verification rules
- [decisions/0003-auth-keycloak.md](../decisions/0003-auth-keycloak.md) — Keycloak setup
