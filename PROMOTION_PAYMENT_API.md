# Promotion Payment API Documentation

## Overview
This document describes the promotion payment flow where service providers can create promotions, get them approved by admin, and pay to advertise them.

---

## Workflow

1. **Service Provider** creates a promotion → Status: `REQUESTED`
2. **Admin** approves the promotion → Status: `APPROVED`
3. **Service Provider** makes payment → Status: `ADVERTISED`, `isPaid: true`
4. **Admin** can view all advertised promotions

---

## API Endpoints

### 1. Create Promotion (Service Provider)

**Endpoint:** `POST /service/promotions/create`

**Authentication:** Required (HOTEL, TOUR_GUIDE, or TRAVEL_AGENT role)

**Request Body:**
```json
{
  "serviceProviderId": 1,
  "title": "Summer Sale - 50% Off",
  "description": "Get 50% discount on all room bookings this summer!",
  "image": "https://example.com/promo-image.jpg",
  "discount": 50,
  "validFrom": "2025-06-01",
  "validTo": "2025-08-31",
  "isActive": true
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "serviceProviderId": 1,
  "title": "Summer Sale - 50% Off",
  "description": "Get 50% discount on all room bookings this summer!",
  "image": "https://example.com/promo-image.jpg",
  "discount": 50,
  "validFrom": "2025-06-01",
  "validTo": "2025-08-31",
  "isActive": true,
  "status": "REQUESTED",
  "createdAt": "2025-10-20T10:30:00",
  "updatedAt": "2025-10-20T10:30:00"
}
```

---

### 2. Get All Promotions by Service Provider

**Endpoint:** `GET /service/promotions/service-provider/{serviceProviderId}`

**Authentication:** Public

**Example:** `GET /service/promotions/service-provider/1`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "serviceProviderId": 1,
    "title": "Summer Sale - 50% Off",
    "status": "REQUESTED",
    "isPaid": false,
    ...
  }
]
```

---

### 3. Admin Approves Promotion

**Endpoint:** `PATCH /service/promotions/{id}/status?status=APPROVED`

**Authentication:** Required (ADMIN role)

**Example:** `PATCH /service/promotions/1/status?status=APPROVED`

**Response:** `200 OK`
```json
{
  "id": 1,
  "serviceProviderId": 1,
  "title": "Summer Sale - 50% Off",
  "status": "APPROVED",
  ...
}
```

**Available Status Values:**
- `REQUESTED` - Initial state
- `APPROVED` - Approved by admin, ready for payment
- `ADVERTISED` - Paid and being advertised
- `REJECTED` - Rejected by admin

---

### 4. Get Approved Promotions (Pending Payment)

**Endpoint:** `GET /service/promotions/approved-pending-payment`

**Authentication:** Public

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "serviceProviderId": 1,
    "title": "Summer Sale - 50% Off",
    "status": "APPROVED",
    "isPaid": false,
    ...
  }
]
```

---

### 5. Process Payment for Promotion

**Endpoint:** `POST /service/promotions/pay`

**Authentication:** Public

**Request Body:**
```json
{
  "promotionId": 1,
  "plan": "premium",
  "cardNumber": "4532 1234 5678 9010",
  "expiryDate": "12/25",
  "cvv": "123",
  "cardholderName": "John Doe",
  "billingAddress": "123 Main Street",
  "city": "Mumbai",
  "state": "Maharashtra",
  "zipCode": "400001",
  "country": "India",
  "amount": 5899.82
}
```

**Pricing Plans:**
| Plan     | Price (Rs.) | Duration | GST (18%) | Total (Rs.) |
|----------|-------------|----------|-----------|-------------|
| basic    | 1,999       | 7 days   | 359.82    | 2,358.82    |
| premium  | 4,999       | 30 days  | 899.82    | 5,898.82    |
| featured | 9,999       | 60 days  | 1,799.82  | 11,798.82   |

**Response:** `200 OK`
```json
{
  "promotionId": 1,
  "transactionId": "TXN1729421400000",
  "paymentReferenceNumber": "PAY-A1B2C3D4",
  "amount": 5898.82,
  "plan": "premium",
  "duration": "30 days",
  "paymentDate": "2025-10-20T11:15:00",
  "cardLastFourDigits": "9010",
  "cardholderName": "John Doe",
  "status": "SUCCESS",
  "message": "Payment successful! Your promotion is now being advertised."
}
```

**Error Response:** `400 Bad Request`
```json
{
  "timestamp": "2025-10-20T11:15:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Promotion must be approved by admin before payment. Current status: REQUESTED"
}
```

---

### 6. Get Payment Details for a Promotion

**Endpoint:** `GET /service/promotions/{id}/payment-details`

**Authentication:** Public

**Example:** `GET /service/promotions/1/payment-details`

**Response:** `200 OK`
```json
{
  "promotionId": 1,
  "isPaid": true,
  "paymentAmount": 5898.82,
  "paymentPlan": "premium",
  "advertisementDuration": "30 days",
  "transactionId": "TXN1729421400000",
  "paymentReferenceNumber": "PAY-A1B2C3D4",
  "paymentDate": "2025-10-20T11:15:00",
  "cardLastFourDigits": "9010",
  "cardholderName": "John Doe",
  "status": "ADVERTISED"
}
```

---

### 7. Get All Advertised Promotions

**Endpoint:** `GET /service/promotions/advertised`

**Authentication:** Public

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "serviceProviderId": 1,
    "title": "Summer Sale - 50% Off",
    "status": "ADVERTISED",
    "isPaid": true,
    "paymentAmount": 5898.82,
    "paymentPlan": "premium",
    ...
  }
]
```

---

## Testing Flow (Step by Step)

### Step 1: Create a Promotion
```bash
curl -X POST http://localhost:8080/service/promotions/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "serviceProviderId": 1,
    "title": "Summer Sale - 50% Off",
    "description": "Get 50% discount on all room bookings!",
    "image": "https://example.com/image.jpg",
    "discount": 50,
    "validFrom": "2025-06-01",
    "validTo": "2025-08-31",
    "isActive": true
  }'
```

### Step 2: Admin Approves the Promotion
```bash
curl -X PATCH "http://localhost:8080/service/promotions/1/status?status=APPROVED" \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

### Step 3: Check Approved Promotions
```bash
curl -X GET http://localhost:8080/service/promotions/approved-pending-payment
```

### Step 4: Make Payment
```bash
curl -X POST http://localhost:8080/service/promotions/pay \
  -H "Content-Type: application/json" \
  -d '{
    "promotionId": 1,
    "plan": "premium",
    "cardNumber": "4532 1234 5678 9010",
    "expiryDate": "12/25",
    "cvv": "123",
    "cardholderName": "John Doe",
    "billingAddress": "123 Main Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "zipCode": "400001",
    "country": "India",
    "amount": 5898.82
  }'
```

### Step 5: Verify Payment
```bash
curl -X GET http://localhost:8080/service/promotions/1/payment-details
```

### Step 6: View Advertised Promotions
```bash
curl -X GET http://localhost:8080/service/promotions/advertised
```

---

## Validation Rules

### Promotion Creation
- Service Provider ID is required and must exist
- Title is required (max 200 characters)
- Description is required
- Discount must be between 0-100%
- Valid from/to dates are required
- Valid to date cannot be in the past
- Valid from must be before valid to

### Payment Processing
- Promotion must be in `APPROVED` status
- Promotion must not be already paid
- Valid plan selection (basic, premium, or featured)
- Card number: 13-19 digits
- Expiry date: MM/YY format
- CVV: 3-4 digits
- All billing information is required

---

## Status Flow

```
REQUESTED → (Admin approves) → APPROVED → (Payment) → ADVERTISED
    ↓
(Admin rejects) → REJECTED
```

---

## Important Notes

1. **Payment is simulated** - Card validation is basic, no actual charges are made
2. **Only last 4 digits** of the card are stored for reference
3. **CVV is never stored** - only validated during payment
4. **Transaction IDs** are generated as `TXN{timestamp}`
5. **Payment references** are generated as `PAY-{UUID}`
6. **GST is 18%** and automatically calculated
7. **Promotion status** automatically changes to `ADVERTISED` after successful payment

---

## Error Codes

| Status Code | Description |
|-------------|-------------|
| 200 | Success |
| 201 | Created successfully |
| 400 | Bad Request (validation error) |
| 401 | Unauthorized (missing/invalid token) |
| 403 | Forbidden (insufficient permissions) |
| 404 | Not Found (promotion doesn't exist) |

---

## Example Frontend Integration (React)

```javascript
// Step 1: Fetch approved promotions
const response = await fetch('http://localhost:8080/service/promotions/approved-pending-payment');
const approvedPromotions = await response.json();

// Step 2: Process payment
const paymentData = {
  promotionId: 1,
  plan: "premium",
  cardNumber: "4532 1234 5678 9010",
  expiryDate: "12/25",
  cvv: "123",
  cardholderName: "John Doe",
  billingAddress: "123 Main Street",
  city: "Mumbai",
  state: "Maharashtra",
  zipCode: "400001",
  country: "India"
};

const paymentResponse = await fetch('http://localhost:8080/service/promotions/pay', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(paymentData)
});

const result = await paymentResponse.json();

if (result.status === 'SUCCESS') {
  console.log('Payment successful!', result);
  // Show success message to user
}
```

---

## Database Schema Changes

The following columns were added to the `promotions` table:

```sql
ALTER TABLE promotions
ADD COLUMN is_paid BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN payment_amount DECIMAL(10,2),
ADD COLUMN payment_plan VARCHAR(50),
ADD COLUMN advertisement_duration VARCHAR(50),
ADD COLUMN transaction_id VARCHAR(100) UNIQUE,
ADD COLUMN payment_reference_number VARCHAR(100) UNIQUE,
ADD COLUMN payment_date TIMESTAMP,
ADD COLUMN card_last_four_digits VARCHAR(4),
ADD COLUMN cardholder_name VARCHAR(255);
```

These will be automatically created by Hibernate when the application starts (since `ddl-auto=update`).