# Functional Requirements

## Actors

- CUSTOMER
- STORE_OWNER
- STORE_EMPLOYEE
- ADMIN

---

## FR-01 Registration

### Customers

- Register via email + password
- Must provide residence address

### Store Owners

- Register via email + password
- Must provide VAT number
- VAT must be verified before store activation

### Employees

- Invited by store owner

---

## FR-02 VAT Verification

- VAT verification is asynchronous
- New owners start as VAT = PENDING
- Only ADMIN can verify or reject VAT
- Only VERIFIED owners can:
  - Create stores
  - Manage products

---

## FR-03 Store Management

Store owners can:

- Create multiple stores
- Provide geo location
- Provide storefront information

Owners can:

- Invite employees
- Remove employees
- Ban employees

---

## FR-04 Product Management

Store owners and employees can:

- Create products
- Update products
- Delete products
- Manage stock

---

## FR-05 Search

Customers can:

- Search via full-text
- Filter by product category
- Receive results ordered by distance
- Search within their geographic area

---

## FR-06 Purchase Modes

### DIRECT

Customer buys physically in store using mobile app.

### RESERVE_PICKUP

- Product reserved
- Held for limited time
- Automatically released if expired

### PAY_PICKUP

- Paid online
- Held until pickup

### PAY_DELIVER

- Paid online
- Shipped via courier

---

## FR-07 Loyalty

- Customers accumulate points on purchase
- Points are converted to vouchers
- Points are tracked via transactions
