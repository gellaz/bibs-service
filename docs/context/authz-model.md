# Authorization Model

## Keycloak Realm Roles

- ADMIN
- USER

## Keycloak Clients

| Client          | Purpose                  | Port |
|-----------------|--------------------------|------|
| `bibs-customer` | Customer-facing web app  | 3000 |
| `bibs-seller`   | Seller management portal | 3001 |
| `bibs-swagger`  | Swagger UI (dev only)    | 8080 |

On first login, `UserSynchronizationService` creates the user and, for `bibs-customer`, auto-creates a
`CustomerProfile`.

---

## Capabilities (derived from profiles)

| Profile                   | Meaning                          |
|---------------------------|----------------------------------|
| `SellerProfile` present   | User is a seller (store owner)   |
| `CustomerProfile` present | User is a customer               |
| Both present              | User is both seller and customer |

---

## Store Membership Roles

Stored in `store_members` table:

- OWNER — created automatically when a seller creates a store
- MANAGER — invited by the owner
- CLERK — invited by the owner

---

## Authorization Rules

Only VERIFIED sellers (`SellerProfile.vatVerificationStatus == VERIFIED`) can:

- Create stores
- Manage products

Only store OWNER can:

- Invite employees (MANAGER, CLERK)
- Remove employees

Only ADMIN can:

- Verify VAT
- Reject VAT
- Manage seller profiles

Store MANAGER and CLERK can:

- Manage products of assigned stores

Customers can:

- Search products
- Place orders
