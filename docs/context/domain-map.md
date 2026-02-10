# Domain Map

## Aggregates

### User

- Identity (Keycloak subject mapping)
- Capabilities derived from profiles:
  - Has `SellerProfile` → is a Seller
  - Has `CustomerProfile` → is a Customer
  - Can have both

---

### SellerProfile

- Belongs to User (one-to-one)
- VAT number
- VAT status (PENDING, VERIFIED, REJECTED)
- VERIFIED required to create stores

---

### CustomerProfile

- Belongs to User (one-to-one)
- Loyalty points balance (derived from LoyaltyPointTransaction sum)

---

### Store

- Created by a verified seller
- Has geo location (latitude, longitude)
- Has members (StoreMember)

---

### StoreMember

- Links a User to a Store with a role
- Roles: OWNER, MANAGER, CLERK
- OWNER auto-assigned on store creation
- MANAGER/CLERK added by OWNER

---

### Product

- Belongs to Store
- Has stock (ProductStock)
- Has categories (ProductCategory, many-to-many)

---

### Order

- Belongs to User (customer)
- Belongs to Store
- Has OrderType (DIRECT, RESERVE_PICKUP, PAY_PICKUP, PAY_DELIVER)
- Has OrderItems
- Has OrderStatus

---

### Shipment

- Exists only for PAY_DELIVER orders

---

### LoyaltyPointTransaction

- Immutable
- Source of truth for customer balance
- Types: EARN, SPEND, ADJUST, EXPIRE
