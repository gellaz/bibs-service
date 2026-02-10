# Business Flows

## Customer Registration

1. User registers
2. Address required
3. Customer profile created (loyalty account)

---

## Owner Registration

1. Owner registers
2. VAT status = PENDING
3. ADMIN reviews VAT
4. If approved → VERIFIED

---

## Employee Invitation

1. Owner invites via email
2. Employee linked to store
3. Employee receives STORE_EMPLOYEE role

---

## Reserve & Pickup

1. Customer reserves product
2. Stock reduced
3. Expiration timestamp set
4. If expired → stock restored

---

## Pay & Pickup

1. Customer pays
2. Stock reduced
3. Await pickup

---

## Pay & Deliver

1. Customer pays
2. Shipment created
3. Courier tracking stored

---

## Loyalty Accrual

1. Order completed
2. Points calculated
3. LoyaltyPointTransaction created
4. Balance updated
