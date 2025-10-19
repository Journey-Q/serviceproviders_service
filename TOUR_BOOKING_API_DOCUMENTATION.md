# Tour Booking API Documentation

## Overview
This API allows customers to send tour booking requests directly to tour guides **without payment**. Tours require **approval from the tour guide** before they are confirmed. Payment happens directly between customer and tour guide. The system includes comprehensive validations for tour capacity and dates.

---

## Base URL
```
http://localhost:8080/service/tour-bookings
```

---

## Key Difference from Room Bookings

**Tour Booking Approval Workflow:**
- Customer creates a booking → Status: `PENDING_APPROVAL`
- Tour Guide reviews and **approves** → Status: `APPROVED`
- OR Tour Guide **rejects** → Status: `REJECTED`
- Customer can cancel anytime → Status: `CANCELLED`
- After tour completion → Status: `COMPLETED`

---

## Endpoints

### 1. Create Tour Booking (Public)

**Endpoint:** `POST /service/tour-bookings/create`

**Description:** Create a new tour booking. The booking will be in `PENDING_APPROVAL` status and requires tour guide approval.

**Request Body:**
```json
{
  "tourId": 1,
  "userId": 123,
  "customerName": "Jane Smith",
  "customerEmail": "jane.smith@example.com",
  "customerPhone": "+1234567890",
  "tourDate": "2025-11-15",
  "numberOfPeople": 4,
  "specialRequests": "Vegetarian meals required"
}
```

**Validations:**
- `tourId`: Required, must exist
- `userId`: Required, ID of the user making the booking
- `customerName`: Required, 2-100 characters
- `customerEmail`: Required, valid email format, max 100 characters
- `customerPhone`: Required, valid phone format (8-20 characters)
- `tourDate`: Required, must be in the future
- `numberOfPeople`: Required, min 1, max 50
- Must meet tour's `minPeople` requirement
- Must not exceed tour's `maxPeople` capacity
- Must not exceed available capacity for the selected date
- `specialRequests`: Optional, max 500 characters

**Additional Business Validations:**
- Tour must exist and be AVAILABLE
- Tour date must be in the future (not more than 1 year)
- Number of people must be between tour's minPeople and maxPeople
- Available capacity on the date must accommodate the number of people

**Success Response (201 Created):**
```json
{
  "id": 1,
  "tourId": 1,
  "serviceProviderId": 10,
  "userId": 123,
  "customerName": "Jane Smith",
  "customerEmail": "jane.smith@example.com",
  "customerPhone": "+1234567890",
  "tourDate": "2025-11-15",
  "numberOfPeople": 4,
  "pricePerPerson": 250.00,
  "totalAmount": 1000.00,
  "status": "PENDING_APPROVAL",
  "specialRequests": "Vegetarian meals required",
  "cancellationReason": null,
  "rejectionReason": null,
  "createdAt": "2025-10-19T14:30:00",
  "approvedAt": null,
  "rejectedAt": null,
  "cancelledAt": null,
  "completedAt": null
}
```

**Error Responses:**

*400 Bad Request - Validation Error:*
```json
{
  "error": "Validation Error",
  "message": "Minimum 5 people required for this tour"
}
```

*409 Conflict - Capacity Issue:*
```json
{
  "error": "Booking Conflict",
  "message": "Not enough capacity available. Requested: 10, Available: 5"
}
```

---

### 2. Get Booking by ID (Public)

**Endpoint:** `GET /service/tour-bookings/{bookingId}`

**Description:** Retrieve booking details by booking ID.

**Success Response (200 OK):**
```json
{
  "id": 1,
  "tourId": 1,
  "serviceProviderId": 10,
  "customerName": "Jane Smith",
  "customerEmail": "jane.smith@example.com",
  "tourDate": "2025-11-15",
  "numberOfPeople": 4,
  "pricePerPerson": 250.00,
  "totalAmount": 1000.00,
  "status": "PENDING_APPROVAL",
  "createdAt": "2025-10-19T14:30:00"
}
```

---

### 3. Get Bookings by Customer Email (Public)

**Endpoint:** `GET /service/tour-bookings/customer/{email}`

**Description:** Get all tour bookings for a specific customer.

**Example:** `GET /service/tour-bookings/customer/jane.smith@example.com`

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "tourId": 1,
    "userId": 123,
    "customerName": "Jane Smith",
    "tourDate": "2025-11-15",
    "numberOfPeople": 4,
    "totalAmount": 1000.00,
    "status": "PENDING_APPROVAL"
  },
  {
    "id": 5,
    "tourId": 3,
    "userId": 123,
    "customerName": "Jane Smith",
    "tourDate": "2025-12-01",
    "numberOfPeople": 2,
    "totalAmount": 600.00,
    "status": "APPROVED"
  }
]
```

---

### 4. Get Bookings by User ID (Public)

**Endpoint:** `GET /service/tour-bookings/user/{userId}`

**Description:** Get all tour bookings for a specific user.

**Example:** `GET /service/tour-bookings/user/123`

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "tourId": 1,
    "userId": 123,
    "customerName": "Jane Smith",
    "customerEmail": "jane.smith@example.com",
    "tourDate": "2025-11-15",
    "numberOfPeople": 4,
    "totalAmount": 1000.00,
    "status": "PENDING_APPROVAL"
  },
  {
    "id": 5,
    "tourId": 3,
    "userId": 123,
    "customerName": "Jane Smith",
    "customerEmail": "jane.smith@example.com",
    "tourDate": "2025-12-01",
    "numberOfPeople": 2,
    "totalAmount": 600.00,
    "status": "APPROVED"
  }
]
```

---

### 5. Cancel Booking (Public)

**Endpoint:** `PUT /service/tour-bookings/{bookingId}/cancel`

**Description:** Customer cancels their booking. Can cancel bookings in `PENDING_APPROVAL` or `APPROVED` status.

**Request Body:**
```json
{
  "cancellationReason": "Travel plans changed"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "tourId": 1,
  "customerName": "Jane Smith",
  "status": "CANCELLED",
  "cancellationReason": "Travel plans changed",
  "cancelledAt": "2025-10-20T16:00:00"
}
```

**Restrictions:**
- Cannot cancel if already CANCELLED, REJECTED, or COMPLETED
- Cannot cancel after tour date has passed

---

### 5. Get Available Capacity (Public)

**Endpoint:** `GET /service/tour-bookings/tour/{tourId}/capacity`

**Description:** Check available capacity for a tour on a specific date.

**Query Parameters:**
- `tourDate` (String): Tour date in ISO format (YYYY-MM-DD)

**Example:**
```
GET /service/tour-bookings/tour/1/capacity?tourDate=2025-11-15
```

**Success Response (200 OK):**
```json
{
  "tourId": 1,
  "tourDate": "2025-11-15",
  "availableCapacity": 12
}
```

---

### 6. Tour Guide Approves Booking (Tour Guide Only)

**Endpoint:** `PUT /service/tour-bookings/{bookingId}/approve`

**Description:** Tour guide approves a pending booking.

**Authentication:** Required (JWT token with ROLE_TOUR_GUIDE)

**Request Body:**
```json
{
  "tourGuideId": 10
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "tourId": 1,
  "serviceProviderId": 10,
  "customerName": "Jane Smith",
  "tourDate": "2025-11-15",
  "numberOfPeople": 4,
  "totalAmount": 1000.00,
  "status": "APPROVED",
  "approvedAt": "2025-10-20T10:00:00"
}
```

**Validations:**
- Only the tour guide who owns the tour can approve
- Only `PENDING_APPROVAL` bookings can be approved
- Tour date must not have passed

**Error Response:**
```json
{
  "error": "Invalid Operation",
  "message": "You are not authorized to approve this booking"
}
```

---

### 7. Tour Guide Rejects Booking (Tour Guide Only)

**Endpoint:** `PUT /service/tour-bookings/{bookingId}/reject`

**Description:** Tour guide rejects a pending booking.

**Authentication:** Required (JWT token with ROLE_TOUR_GUIDE)

**Request Body:**
```json
{
  "tourGuideId": 10,
  "rejectionReason": "Tour schedule fully booked"
}
```

**Note:** If `rejectionReason` is not provided, a default message will be used.

**Success Response (200 OK):**
```json
{
  "id": 1,
  "tourId": 1,
  "customerName": "Jane Smith",
  "status": "REJECTED",
  "rejectionReason": "Tour schedule fully booked",
  "rejectedAt": "2025-10-20T10:30:00"
}
```

**Validations:**
- Only the tour guide who owns the tour can reject
- Only `PENDING_APPROVAL` bookings can be rejected

---

### 8. Tour Guide Completes Booking (Tour Guide Only)

**Endpoint:** `PUT /service/tour-bookings/{bookingId}/complete`

**Description:** Mark a booking as completed after the tour is finished.

**Authentication:** Required (JWT token with ROLE_TOUR_GUIDE)

**Request Body:**
```json
{
  "tourGuideId": 10
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "tourId": 1,
  "customerName": "Jane Smith",
  "status": "COMPLETED",
  "completedAt": "2025-11-15T18:00:00"
}
```

**Validations:**
- Only the tour guide who owns the tour can complete
- Only `APPROVED` bookings can be completed
- Tour date must be today or in the past

---

### 9. Get Bookings by Tour (Tour Guide Only)

**Endpoint:** `GET /service/tour-bookings/tour/{tourId}`

**Description:** Get all bookings for a specific tour.

**Authentication:** Required (JWT token with ROLE_TOUR_GUIDE)

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "tourId": 1,
    "customerName": "Jane Smith",
    "tourDate": "2025-11-15",
    "numberOfPeople": 4,
    "status": "PENDING_APPROVAL"
  },
  {
    "id": 2,
    "tourId": 1,
    "customerName": "Bob Johnson",
    "tourDate": "2025-11-20",
    "numberOfPeople": 6,
    "status": "APPROVED"
  }
]
```

---

### 10. Get All Bookings for Tour Guide (Tour Guide Only)

**Endpoint:** `GET /service/tour-bookings/guide/{tourGuideId}`

**Description:** Get all bookings for all tours of a tour guide.

**Authentication:** Required (JWT token with ROLE_TOUR_GUIDE)

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "tourId": 1,
    "customerName": "Jane Smith",
    "tourDate": "2025-11-15",
    "status": "PENDING_APPROVAL"
  },
  {
    "id": 3,
    "tourId": 2,
    "customerName": "Alice Brown",
    "tourDate": "2025-11-22",
    "status": "APPROVED"
  }
]
```

---

### 11. Get Pending Approval Bookings (Tour Guide Only)

**Endpoint:** `GET /service/tour-bookings/guide/{tourGuideId}/pending`

**Description:** Get all bookings waiting for tour guide approval.

**Authentication:** Required (JWT token with ROLE_TOUR_GUIDE)

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "tourId": 1,
    "customerName": "Jane Smith",
    "customerEmail": "jane.smith@example.com",
    "customerPhone": "+1234567890",
    "tourDate": "2025-11-15",
    "numberOfPeople": 4,
    "totalAmount": 1000.00,
    "status": "PENDING_APPROVAL",
    "createdAt": "2025-10-19T14:30:00"
  }
]
```

---

### 12. Get Approved Bookings (Tour Guide Only)

**Endpoint:** `GET /service/tour-bookings/guide/{tourGuideId}/approved`

**Description:** Get all approved bookings for a tour guide.

**Authentication:** Required (JWT token with ROLE_TOUR_GUIDE)

**Success Response (200 OK):**
```json
[
  {
    "id": 2,
    "tourId": 1,
    "customerName": "Bob Johnson",
    "tourDate": "2025-11-20",
    "numberOfPeople": 6,
    "status": "APPROVED",
    "approvedAt": "2025-10-18T12:00:00"
  }
]
```

---

### 13. Get Upcoming Tours (Tour Guide Only)

**Endpoint:** `GET /service/tour-bookings/guide/{tourGuideId}/upcoming`

**Description:** Get all upcoming approved tours (future dates only).

**Authentication:** Required (JWT token with ROLE_TOUR_GUIDE)

**Success Response (200 OK):**
```json
[
  {
    "id": 2,
    "tourId": 1,
    "customerName": "Bob Johnson",
    "tourDate": "2025-11-20",
    "numberOfPeople": 6,
    "status": "APPROVED"
  },
  {
    "id": 5,
    "tourId": 3,
    "customerName": "Jane Smith",
    "tourDate": "2025-12-01",
    "numberOfPeople": 2,
    "status": "APPROVED"
  }
]
```

---

## Booking Status Workflow

```
                    ┌──> REJECTED (Tour guide rejects)
                    │
PENDING_APPROVAL ───┼──> APPROVED (Tour guide approves) ──> COMPLETED (After tour)
                    │
                    └──> CANCELLED (Customer cancels)
```

**Status Transitions:**
- `PENDING_APPROVAL` → `APPROVED` (tour guide approves)
- `PENDING_APPROVAL` → `REJECTED` (tour guide rejects)
- `PENDING_APPROVAL` → `CANCELLED` (customer cancels)
- `APPROVED` → `COMPLETED` (tour guide marks complete after tour)
- `APPROVED` → `CANCELLED` (customer cancels before tour date)

---

## Data Models

### CreateTourBookingDTO
```json
{
  "tourId": "Long (required)",
  "userId": "Long (required)",
  "customerName": "String (required, 2-100 chars)",
  "customerEmail": "String (required, valid email, max 100 chars)",
  "customerPhone": "String (required, 8-20 chars)",
  "tourDate": "LocalDate (required, future date)",
  "numberOfPeople": "Integer (required, 1-50, must be within tour's min/max)",
  "specialRequests": "String (optional, max 500 chars)"
}
```

### TourBookingResponseDTO
```json
{
  "id": "Long",
  "tourId": "Long",
  "serviceProviderId": "Long",
  "userId": "Long",
  "customerName": "String",
  "customerEmail": "String",
  "customerPhone": "String",
  "tourDate": "LocalDate",
  "numberOfPeople": "Integer",
  "pricePerPerson": "BigDecimal",
  "totalAmount": "BigDecimal",
  "status": "String (enum: PENDING_APPROVAL, APPROVED, REJECTED, CANCELLED, COMPLETED)",
  "specialRequests": "String",
  "cancellationReason": "String",
  "rejectionReason": "String",
  "createdAt": "LocalDateTime",
  "approvedAt": "LocalDateTime",
  "rejectedAt": "LocalDateTime",
  "cancelledAt": "LocalDateTime",
  "completedAt": "LocalDateTime"
}
```

---

## Authentication

### Public Endpoints (No Authentication Required)
- POST `/service/tour-bookings/create`
- GET `/service/tour-bookings/{bookingId}`
- GET `/service/tour-bookings/customer/{email}`
- GET `/service/tour-bookings/user/{userId}`
- PUT `/service/tour-bookings/{bookingId}/cancel`
- GET `/service/tour-bookings/tour/{tourId}/capacity`

### Protected Endpoints (Requires JWT Token with ROLE_TOUR_GUIDE)
- GET `/service/tour-bookings/tour/{tourId}`
- GET `/service/tour-bookings/guide/{tourGuideId}`
- GET `/service/tour-bookings/guide/{tourGuideId}/pending`
- GET `/service/tour-bookings/guide/{tourGuideId}/approved`
- GET `/service/tour-bookings/guide/{tourGuideId}/upcoming`
- PUT `/service/tour-bookings/{bookingId}/approve`
- PUT `/service/tour-bookings/{bookingId}/reject`
- PUT `/service/tour-bookings/{bookingId}/complete`

### How to Add Authentication Header
```
Authorization: Bearer <your-jwt-token>
```

---

## Error Codes

| HTTP Status | Error Type | Description |
|------------|------------|-------------|
| 400 | Validation Error | Invalid input data (validation failed) |
| 404 | Not Found | Resource not found (tour, booking) |
| 409 | Booking Conflict | Tour unavailable, capacity exceeded |
| 500 | Server Error | Unexpected server error |

---

## Example Use Cases

### Use Case 1: Customer Books a Tour

1. **Check available capacity:**
   ```
   GET /service/tour-bookings/tour/1/capacity?tourDate=2025-11-15
   ```

2. **Create booking:**
   ```
   POST /service/tour-bookings/create
   {
     "tourId": 1,
     "userId": 123,
     "customerName": "Jane Smith",
     "customerEmail": "jane.smith@example.com",
     "customerPhone": "+1234567890",
     "tourDate": "2025-11-15",
     "numberOfPeople": 4,
     "specialRequests": "Vegetarian meals required"
   }
   ```

3. **Receive confirmation with status PENDING_APPROVAL**
4. **Wait for tour guide to approve**

### Use Case 2: Tour Guide Reviews Pending Bookings

1. **Get all pending bookings:**
   ```
   GET /service/tour-bookings/guide/10/pending
   ```
   (Requires TOUR_GUIDE authentication)

2. **Review each booking and approve:**
   ```
   PUT /service/tour-bookings/1/approve
   {
     "tourGuideId": 10
   }
   ```

3. **Or reject if needed:**
   ```
   PUT /service/tour-bookings/2/reject
   {
     "tourGuideId": 10,
     "rejectionReason": "Schedule conflict"
   }
   ```

### Use Case 3: Customer Views Their Bookings

```
GET /service/tour-bookings/customer/jane.smith@example.com
```

Shows all bookings with their current status (PENDING_APPROVAL, APPROVED, etc.)

### Use Case 4: Customer Cancels Before Tour Date

```
PUT /service/tour-bookings/1/cancel
{
  "cancellationReason": "Personal emergency"
}
```

### Use Case 5: Tour Guide Completes Tour

After the tour is finished:
```
PUT /service/tour-bookings/1/complete
{
  "tourGuideId": 10
}
```
(Requires TOUR_GUIDE authentication)

### Use Case 6: Tour Guide Views Upcoming Tours

```
GET /service/tour-bookings/guide/10/upcoming
```
(Requires TOUR_GUIDE authentication)

Shows all approved tours scheduled for future dates.

---

## Key Differences: Room Booking vs Tour Booking

| Feature | Room Booking | Tour Booking |
|---------|--------------|--------------|
| **Initial Status** | CONFIRMED (auto-approved) | PENDING_APPROVAL (requires approval) |
| **Approval** | Not required | Tour guide must approve |
| **Payment** | Card details required (Stripe) | No payment - direct payment to tour guide |
| **Capacity Check** | One room = one booking | Multiple bookings per tour date |
| **Date Range** | Check-in to Check-out | Single tour date |
| **Rejection** | Not applicable | Tour guide can reject |
| **Provider Control** | Less control | Full control (approve/reject) |

---

## Security Notes

**⚠️ IMPORTANT FOR PRODUCTION:**

1. **HTTPS:** Always use HTTPS
2. **Rate Limiting:** Implement on booking creation
3. **Email Notifications:**
   - Notify customer when booking is created
   - Notify customer when approved/rejected
   - Notify tour guide of new pending bookings
4. **Payment Processing:** Payment is handled directly between customer and tour guide (not through the platform)

---

## Testing Tips

### Valid Test Data
```
Tour ID: 1 (ensure it exists and is AVAILABLE)
Tour Date: Tomorrow or any future date (within 1 year)
Number of People: Check tour's minPeople and maxPeople
Customer Name: Jane Smith
Customer Email: jane.smith@example.com
Customer Phone: +1234567890
```

### Testing Approval Workflow
1. Create booking → status = PENDING_APPROVAL
2. Login as tour guide → get pending bookings
3. Approve → status = APPROVED
4. Complete after tour date → status = COMPLETED

---

## Database Schema

### tour_bookings Table
```sql
CREATE TABLE tour_bookings (
    id BIGSERIAL PRIMARY KEY,
    tour_id BIGINT NOT NULL,
    service_provider_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_email VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    tour_date DATE NOT NULL,
    number_of_people INTEGER NOT NULL,
    price_per_person DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    card_holder_name VARCHAR(100),
    card_number VARCHAR(19),
    expiry_date VARCHAR(7),
    cvv VARCHAR(4),
    billing_address VARCHAR(200),
    status VARCHAR(20) NOT NULL,
    special_requests VARCHAR(500),
    cancellation_reason VARCHAR(500),
    rejection_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    approved_at TIMESTAMP,
    rejected_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    completed_at TIMESTAMP
);
```

**Note:**
- `user_id` tracks which user made the booking
- Card detail fields are nullable as they are no longer required for tour bookings

---

## Contact & Support

For issues or questions, please contact the development team or refer to the project repository.
