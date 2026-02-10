# VAT Verification

## Status

- PENDING
- VERIFIED
- REJECTED

---

## Flow

1. Owner registers with VAT number
2. VAT status = PENDING (automatic)
3. ADMIN reviews VAT asynchronously
4. ADMIN sets VERIFIED or REJECTED

---

## Rules

- VAT verification is **asynchronous** and **ADMIN-only**
- Only ADMIN can modify VAT status (verify or reject)
- VERIFIED is required before any seller feature (store creation, product management)
- All status transitions must be auditable
- `SellerProfileService.requireVerifiedSeller()` enforces the gate
- VAT status is stored in `SellerProfile.vatVerificationStatus`
