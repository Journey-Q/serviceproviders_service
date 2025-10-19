# Room Booking API Documentation

## Overview
This API allows customers to book hotel rooms with card payment details stored in the database. It includes comprehensive validations for dates, room availability, guest capacity, and card details.

---

## Base URL
```
http://localhost:8080/service/room-bookings
```

---

## Endpoints

### 1. Create Room Booking (Public)

**Endpoint:** `POST /service/room-bookings/create`

**Description:** Create a new room booking with customer and payment details. Available to all users (no authentication required).

**Request Body:**
```json
{
  "roomId": 1,
  "userId": 123,
  "customerName": "John Doe",
  "customerEmail": "john.doe@example.com",
  "customerPhone": "+1234567890",
  "checkInDate": "2025-11-01",
  "checkOutDate": "2025-11-05",
  "numberOfGuests": 2,
  "specialRequests": "Late check-in requested",
  "cardDetails": {
    "cardHolderName": "John Doe",
    "cardNumber": "4532015112830366",
    "expiryDate": "12/2026",
    "cvv": "123",
    "billingAddress": "123 Main St, New York, NY 10001"
  }
}
```

**Validations:**
- `roomId`: Required, must exist
- `userId`: Required, ID of the user making the booking
- `customerName`: Required, 2-100 characters
- `customerEmail`: Required, valid email format, max 100 characters
- `customerPhone`: Required, valid phone format (8-20 characters with numbers, +, -, spaces, parentheses)
- `checkInDate`: Required, must be in the future
- `checkOutDate`: Required, must be after check-in date
- `numberOfGuests`: Required, min 1, max 20, must not exceed room capacity
- `specialRequests`: Optional, max 500 characters
- `cardHolderName`: Required, 2-100 characters
- `cardNumber`: Required, 13-19 digits
- `expiryDate`: Required, format MM/YYYY
- `cvv`: Required, 3-4 digits
- `billingAddress`: Optional, max 200 characters

**Additional Business Validations:**
- Room must exist and be AVAILABLE
- Check-in date must be today or in the future
- Check-out date must be after check-in date
- Minimum booking: 1 night
- Maximum booking: 30 nights
- Number of guests must not exceed room's maxOccupancy
- No overlapping bookings for the same room
- Card must not be expired

**Success Response (201 Created):**
```json
{
  "id": 1,
  "roomId": 1,
  "serviceProviderId": 5,
  "userId": 123,
  "customerName": "John Doe",
  "customerEmail": "john.doe@example.com",
  "customerPhone": "+1234567890",
  "checkInDate": "2025-11-01",
  "checkOutDate": "2025-11-05",
  "numberOfGuests": 2,
  "numberOfNights": 4,
  "pricePerNight": 150.00,
  "totalAmount": 600.00,
  "cardHolderName": "John Doe",
  "maskedCardNumber": "**** **** **** 0366",
  "expiryDate": "12/2026",
  "status": "CONFIRMED",
  "specialRequests": "Late check-in requested",
  "cancellationReason": null,
  "createdAt": "2025-10-19T10:30:00",
  "confirmedAt": "2025-10-19T10:30:00",
  "cancelledAt": null
}
```

**Error Responses:**

*400 Bad Request - Validation Error:*
```json
{
  "error": "Validation Error",
  "message": "Number of guests (5) exceeds room maximum occupancy (4)"
}
```

*409 Conflict - Booking Conflict:*
```json
{
  "error": "Booking Conflict",
  "message": "Room is already booked for the selected dates. Please choose different dates."
}
```

---

### 2. Get Booking by ID (Public)

**Endpoint:** `GET /service/room-bookings/{bookingId}`

**Description:** Retrieve booking details by booking ID. Available to all users.

**Path Parameters:**
- `bookingId` (Long): The booking ID

**Success Response (200 OK):**
```json
{
  "id": 1,
  "roomId": 1,
  "serviceProviderId": 5,
  "customerName": "John Doe",
  "customerEmail": "john.doe@example.com",
  "customerPhone": "+1234567890",
  "checkInDate": "2025-11-01",
  "checkOutDate": "2025-11-05",
  "numberOfGuests": 2,
  "numberOfNights": 4,
  "pricePerNight": 150.00,
  "totalAmount": 600.00,
  "cardHolderName": "John Doe",
  "maskedCardNumber": "**** **** **** 0366",
  "expiryDate": "12/2026",
  "status": "CONFIRMED",
  "specialRequests": "Late check-in requested",
  "cancellationReason": null,
  "createdAt": "2025-10-19T10:30:00",
  "confirmedAt": "2025-10-19T10:30:00",
  "cancelledAt": null
}
```

**Error Response (404 Not Found):**
```json
{
  "error": "Not Found",
  "message": "Booking not found with ID: 999"
}
```

---

### 3. Get Bookings by Customer Email (Public)

**Endpoint:** `GET /service/room-bookings/customer/{email}`

**Description:** Get all bookings for a specific customer email. Available to all users.

**Path Parameters:**
- `email` (String): Customer email address

**Example:** `GET /service/room-bookings/customer/john.doe@example.com`

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "roomId": 1,
    "serviceProviderId": 5,
    "userId": 123,
    "customerName": "John Doe",
    "customerEmail": "john.doe@example.com",
    "checkInDate": "2025-11-01",
    "checkOutDate": "2025-11-05",
    "numberOfGuests": 2,
    "numberOfNights": 4,
    "pricePerNight": 150.00,
    "totalAmount": 600.00,
    "status": "CONFIRMED",
    "createdAt": "2025-10-19T10:30:00"
  },
  {
    "id": 5,
    "roomId": 3,
    "serviceProviderId": 5,
    "userId": 123,
    "customerName": "John Doe",
    "customerEmail": "john.doe@example.com",
    "checkInDate": "2025-12-20",
    "checkOutDate": "2025-12-25",
    "numberOfGuests": 3,
    "numberOfNights": 5,
    "pricePerNight": 200.00,
    "totalAmount": 1000.00,
    "status": "PENDING",
    "createdAt": "2025-10-18T15:20:00"
  }
]
```

---

### 4. Get Bookings by User ID (Public)

**Endpoint:** `GET /service/room-bookings/user/{userId}`

**Description:** Get all bookings for a specific user. Available to all users.

**Path Parameters:**
- `userId` (Long): User ID

**Example:** `GET /service/room-bookings/user/123`

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "roomId": 1,
    "serviceProviderId": 5,
    "userId": 123,
    "customerName": "John Doe",
    "customerEmail": "john.doe@example.com",
    "checkInDate": "2025-11-01",
    "checkOutDate": "2025-11-05",
    "numberOfGuests": 2,
    "numberOfNights": 4,
    "pricePerNight": 150.00,
    "totalAmount": 600.00,
    "status": "CONFIRMED",
    "createdAt": "2025-10-19T10:30:00"
  },
  {
    "id": 5,
    "roomId": 3,
    "serviceProviderId": 5,
    "userId": 123,
    "customerName": "John Doe",
    "customerEmail": "john.doe@example.com",
    "checkInDate": "2025-12-20",
    "checkOutDate": "2025-12-25",
    "numberOfGuests": 3,
    "numberOfNights": 5,
    "pricePerNight": 200.00,
    "totalAmount": 1000.00,
    "status": "PENDING",
    "createdAt": "2025-10-18T15:20:00"
  }
]
```

---

### 5. Cancel Booking (Public)

**Endpoint:** `PUT /service/room-bookings/{bookingId}/cancel`

**Description:** Cancel a booking. Available to all users (customers can cancel their own bookings).

**Path Parameters:**
- `bookingId` (Long): The booking ID to cancel

**Request Body:**
```json
{
  "cancellationReason": "Change of travel plans"
}
```

**Note:** If `cancellationReason` is not provided, default reason "Customer requested cancellation" will be used.

**Success Response (200 OK):**
```json
{
  "id": 1,
  "roomId": 1,
  "serviceProviderId": 5,
  "customerName": "John Doe",
  "customerEmail": "john.doe@example.com",
  "status": "CANCELLED",
  "cancellationReason": "Change of travel plans",
  "cancelledAt": "2025-10-20T14:30:00",
  "totalAmount": 600.00
}
```

**Validations:**
- Booking cannot be cancelled if already CANCELLED or COMPLETED
- Booking cannot be cancelled after check-in date

**Error Responses:**

*400 Bad Request - Already Cancelled:*
```json
{
  "error": "Invalid Operation",
  "message": "Booking is already cancelled"
}
```

*400 Bad Request - Cannot Cancel After Check-in:*
```json
{
  "error": "Invalid Operation",
  "message": "Cannot cancel booking after check-in date"
}
```

---

### 5. Check Room Availability (Public)

**Endpoint:** `GET /service/room-bookings/check-availability`

**Description:** Check if a room is available for specific dates. Available to all users.

**Query Parameters:**
- `roomId` (Long): Room ID to check
- `checkInDate` (String): Check-in date in ISO format (YYYY-MM-DD)
- `checkOutDate` (String): Check-out date in ISO format (YYYY-MM-DD)

**Example:**
```
GET /service/room-bookings/check-availability?roomId=1&checkInDate=2025-11-01&checkOutDate=2025-11-05
```

**Success Response (200 OK):**
```json
{
  "roomId": 1,
  "checkInDate": "2025-11-01",
  "checkOutDate": "2025-11-05",
  "available": true
}
```

---

### 6. Get Bookings by Room (Hotel Staff Only)

**Endpoint:** `GET /service/room-bookings/room/{roomId}`

**Description:** Get all bookings for a specific room. Requires HOTEL role.

**Authentication:** Required (JWT token with ROLE_HOTEL)

**Path Parameters:**
- `roomId` (Long): The room ID

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "roomId": 1,
    "customerName": "John Doe",
    "customerEmail": "john.doe@example.com",
    "checkInDate": "2025-11-01",
    "checkOutDate": "2025-11-05",
    "status": "CONFIRMED",
    "totalAmount": 600.00
  }
]
```

---

### 7. Get Bookings by Service Provider (Hotel Staff Only)

**Endpoint:** `GET /service/room-bookings/provider/{serviceProviderId}`

**Description:** Get all bookings for a specific hotel/service provider. Requires HOTEL role.

**Authentication:** Required (JWT token with ROLE_HOTEL)

**Path Parameters:**
- `serviceProviderId` (Long): The service provider ID

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "roomId": 1,
    "customerName": "John Doe",
    "checkInDate": "2025-11-01",
    "checkOutDate": "2025-11-05",
    "status": "CONFIRMED",
    "totalAmount": 600.00
  },
  {
    "id": 2,
    "roomId": 2,
    "customerName": "Jane Smith",
    "checkInDate": "2025-11-10",
    "checkOutDate": "2025-11-12",
    "status": "PENDING",
    "totalAmount": 400.00
  }
]
```

---

### 8. Get Upcoming Bookings (Hotel Staff Only)

**Endpoint:** `GET /service/room-bookings/provider/{serviceProviderId}/upcoming`

**Description:** Get all upcoming bookings (CONFIRMED or PENDING) for a service provider. Requires HOTEL role.

**Authentication:** Required (JWT token with ROLE_HOTEL)

**Path Parameters:**
- `serviceProviderId` (Long): The service provider ID

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "roomId": 1,
    "customerName": "John Doe",
    "checkInDate": "2025-11-01",
    "checkOutDate": "2025-11-05",
    "status": "CONFIRMED",
    "totalAmount": 600.00
  }
]
```

---

### 9. Update Booking Status (Hotel Staff Only)

**Endpoint:** `PUT /service/room-bookings/{bookingId}/status`

**Description:** Update booking status (for check-in, check-out, etc.). Requires HOTEL role.

**Authentication:** Required (JWT token with ROLE_HOTEL)

**Path Parameters:**
- `bookingId` (Long): The booking ID

**Request Body:**
```json
{
  "status": "CHECKED_IN"
}
```

**Valid Status Values:**
- `PENDING`
- `CONFIRMED`
- `CHECKED_IN`
- `CHECKED_OUT`
- `CANCELLED`
- `COMPLETED`

**Status Transition Rules:**
- PENDING → CONFIRMED or CANCELLED
- CONFIRMED → CHECKED_IN or CANCELLED
- CHECKED_IN → CHECKED_OUT
- CHECKED_OUT → COMPLETED
- CANCELLED and COMPLETED are final states (cannot be changed)

**Success Response (200 OK):**
```json
{
  "id": 1,
  "roomId": 1,
  "customerName": "John Doe",
  "status": "CHECKED_IN",
  "checkInDate": "2025-11-01",
  "totalAmount": 600.00
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Invalid Operation",
  "message": "CONFIRMED bookings can only be CHECKED_IN or CANCELLED"
}
```

---

## Booking Status Workflow

```
PENDING ──┬──> CONFIRMED ──┬──> CHECKED_IN ──> CHECKED_OUT ──> COMPLETED
          │                │
          └──> CANCELLED   └──> CANCELLED
```

---

## Data Models

### CreateRoomBookingDTO
```json
{
  "roomId": "Long (required)",
  "customerName": "String (required, 2-100 chars)",
  "customerEmail": "String (required, valid email, max 100 chars)",
  "customerPhone": "String (required, 8-20 chars)",
  "checkInDate": "LocalDate (required, future date)",
  "checkOutDate": "LocalDate (required, future date, after check-in)",
  "numberOfGuests": "Integer (required, 1-20)",
  "specialRequests": "String (optional, max 500 chars)",
  "cardDetails": {
    "cardHolderName": "String (required, 2-100 chars)",
    "cardNumber": "String (required, 13-19 digits)",
    "expiryDate": "String (required, MM/YYYY format)",
    "cvv": "String (required, 3-4 digits)",
    "billingAddress": "String (optional, max 200 chars)"
  }
}
```

### RoomBookingResponseDTO
```json
{
  "id": "Long",
  "roomId": "Long",
  "serviceProviderId": "Long",
  "customerName": "String",
  "customerEmail": "String",
  "customerPhone": "String",
  "checkInDate": "LocalDate",
  "checkOutDate": "LocalDate",
  "numberOfGuests": "Integer",
  "numberOfNights": "Integer",
  "pricePerNight": "BigDecimal",
  "totalAmount": "BigDecimal",
  "cardHolderName": "String",
  "maskedCardNumber": "String (last 4 digits only)",
  "expiryDate": "String",
  "status": "String (enum)",
  "specialRequests": "String",
  "cancellationReason": "String",
  "createdAt": "LocalDateTime",
  "confirmedAt": "LocalDateTime",
  "cancelledAt": "LocalDateTime"
}
```

---

## Authentication

### Public Endpoints (No Authentication Required)
- POST `/service/room-bookings/create`
- GET `/service/room-bookings/{bookingId}`
- GET `/service/room-bookings/customer/{email}`
- PUT `/service/room-bookings/{bookingId}/cancel`
- GET `/service/room-bookings/check-availability`

### Protected Endpoints (Requires JWT Token with ROLE_HOTEL)
- GET `/service/room-bookings/room/{roomId}`
- GET `/service/room-bookings/provider/{serviceProviderId}`
- GET `/service/room-bookings/provider/{serviceProviderId}/upcoming`
- PUT `/service/room-bookings/{bookingId}/status`

### How to Add Authentication Header
```
Authorization: Bearer <your-jwt-token>
```

---

## Error Codes

| HTTP Status | Error Type | Description |
|------------|------------|-------------|
| 400 | Validation Error | Invalid input data (validation failed) |
| 404 | Not Found | Resource not found (room, booking) |
| 409 | Booking Conflict | Room unavailable, overlapping dates |
| 500 | Server Error | Unexpected server error |

---

## Example Use Cases

### Use Case 1: Customer Books a Room

1. **Check availability:**
   ```
   GET /service/room-bookings/check-availability?roomId=1&checkInDate=2025-11-01&checkOutDate=2025-11-05
   ```

2. **Create booking:**
   ```
   POST /service/room-bookings/create
   {
     "roomId": 1,
     "customerName": "John Doe",
     "customerEmail": "john.doe@example.com",
     "customerPhone": "+1234567890",
     "checkInDate": "2025-11-01",
     "checkOutDate": "2025-11-05",
     "numberOfGuests": 2,
     "cardDetails": { ... }
   }
   ```

3. **Receive confirmation with booking ID**

### Use Case 2: Customer Views Their Bookings

```
GET /service/room-bookings/customer/john.doe@example.com
```

### Use Case 3: Customer Cancels Booking

```
PUT /service/room-bookings/1/cancel
{
  "cancellationReason": "Travel plans changed"
}
```

### Use Case 4: Hotel Staff Check-In Guest

```
PUT /service/room-bookings/1/status
{
  "status": "CHECKED_IN"
}
```
(Requires HOTEL authentication)

### Use Case 5: Hotel Views All Upcoming Bookings

```
GET /service/room-bookings/provider/5/upcoming
```
(Requires HOTEL authentication)

---

## Security Notes

**⚠️ IMPORTANT FOR PRODUCTION:**

1. **Card Number Storage:** Currently, full card numbers are stored in the database. In production:
   - Encrypt card numbers at rest
   - Consider using tokenization services
   - Comply with PCI DSS requirements

2. **CVV Storage:** CVV is currently stored. In production:
   - **NEVER store CVV** (violates PCI DSS)
   - Remove CVV field from entity

3. **HTTPS:** Always use HTTPS in production for sensitive data transmission

4. **Rate Limiting:** Implement rate limiting on booking creation to prevent abuse

5. **Email Verification:** Consider adding email verification for bookings

---

## Testing Tips

### Valid Test Card Number
```
4532015112830366 (16 digits, Visa format)
```

### Valid Expiry Date
```
12/2026 (format: MM/YYYY, future date)
```

### Valid CVV
```
123 (3 digits)
```

### Valid Date Range
```
checkInDate: tomorrow or any future date
checkOutDate: at least 1 day after checkInDate
Maximum duration: 30 nights
```

---

## Database Schema

### room_bookings Table
```sql
CREATE TABLE room_bookings (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    service_provider_id BIGINT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_email VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_guests INTEGER NOT NULL,
    number_of_nights INTEGER NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    card_holder_name VARCHAR(100) NOT NULL,
    card_number VARCHAR(19) NOT NULL,
    expiry_date VARCHAR(7) NOT NULL,
    cvv VARCHAR(4) NOT NULL,
    billing_address VARCHAR(200),
    status VARCHAR(20) NOT NULL,
    special_requests VARCHAR(500),
    cancellation_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    confirmed_at TIMESTAMP,
    cancelled_at TIMESTAMP
);
```

---

## Contact & Support

For issues or questions, please contact the development team or refer to the project repository.
