# Vehicle Booking API Documentation

## Overview
The Vehicle Booking system allows customers to book vehicles from travel agencies. Similar to tour package bookings, this system requires **travel agency approval** and **no payment at booking time**.

## Booking Flow
1. **Customer creates booking** → Status: `PENDING_APPROVAL`
2. **Travel agency reviews** → Approves or Rejects
3. **If approved** → Status: `APPROVED`
4. **After trip completion** → Status: `COMPLETED`

## Booking Statuses
- `PENDING_APPROVAL` - Waiting for travel agency to approve
- `APPROVED` - Travel agency approved the booking
- `REJECTED` - Travel agency rejected the booking
- `CANCELLED` - Customer cancelled the booking
- `COMPLETED` - Trip completed

---

## API Endpoints

### 1. Create Vehicle Booking (Public)
**POST** `/service/vehicle-bookings/create`

Creates a new vehicle booking without payment.

**Request Body:**
```json
{
  "vehicleId": 1,
  "userId": 123,
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "+1234567890",
  "startDate": "2025-11-01",
  "endDate": "2025-11-05",
  "pickupLocation": "Colombo Airport",
  "dropoffLocation": "Galle Fort",
  "estimatedKilometers": 250,
  "withAC": true,
  "specialRequests": "Need baby seat"
}
```

**Response:**
```json
{
  "id": 1,
  "vehicleId": 1,
  "serviceProviderId": 10,
  "userId": 123,
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "+1234567890",
  "startDate": "2025-11-01",
  "endDate": "2025-11-05",
  "pickupLocation": "Colombo Airport",
  "dropoffLocation": "Galle Fort",
  "estimatedKilometers": 250,
  "withAC": true,
  "pricePerKm": 50.00,
  "estimatedTotalAmount": 12500.00,
  "status": "PENDING_APPROVAL",
  "specialRequests": "Need baby seat",
  "createdAt": "2025-10-19T10:30:00"
}
```

---

### 2. Get Booking by ID (Public)
**GET** `/service/vehicle-bookings/{bookingId}`

Retrieves booking details by ID.

**Response:** Same as create booking response

---

### 3. Get Bookings by Customer Email (Public)
**GET** `/service/vehicle-bookings/customer/{email}`

Returns all bookings for a customer email.

**Response:** Array of booking objects

---

### 4. Get Bookings by User ID (Public)
**GET** `/service/vehicle-bookings/user/{userId}`

Returns all bookings for a user.

**Response:** Array of booking objects

---

### 5. Check Vehicle Availability (Public)
**GET** `/service/vehicle-bookings/vehicle/{vehicleId}/availability?startDate=2025-11-01&endDate=2025-11-05`

Checks if vehicle is available for date range.

**Response:**
```json
{
  "vehicleId": 1,
  "startDate": "2025-11-01",
  "endDate": "2025-11-05",
  "isAvailable": true
}
```

---

### 6. Cancel Booking (Public)
**PUT** `/service/vehicle-bookings/{bookingId}/cancel`

Customer cancels their booking (must be before start date).

**Request Body:**
```json
{
  "cancellationReason": "Plans changed"
}
```

**Response:** Updated booking object with status `CANCELLED`

---

## Travel Agency Endpoints (Require TRAVEL_AGENT role)

### 7. Approve Booking
**PUT** `/service/vehicle-bookings/{bookingId}/approve`

**Request Body:**
```json
{
  "travelAgencyId": 10
}
```

**Response:** Updated booking object with status `APPROVED`

---

### 8. Reject Booking
**PUT** `/service/vehicle-bookings/{bookingId}/reject`

**Request Body:**
```json
{
  "travelAgencyId": 10,
  "rejectionReason": "Vehicle not available for those dates"
}
```

**Response:** Updated booking object with status `REJECTED`

---

### 9. Complete Booking
**PUT** `/service/vehicle-bookings/{bookingId}/complete`

Mark booking as completed after trip ends.

**Request Body:**
```json
{
  "travelAgencyId": 10
}
```

**Response:** Updated booking object with status `COMPLETED`

---

### 10. Get All Bookings for Travel Agency
**GET** `/service/vehicle-bookings/agency/{travelAgencyId}`

Returns all bookings for the travel agency.

---

### 11. Get Pending Approval Bookings
**GET** `/service/vehicle-bookings/agency/{travelAgencyId}/pending`

Returns bookings waiting for approval.

---

### 12. Get Approved Bookings
**GET** `/service/vehicle-bookings/agency/{travelAgencyId}/approved`

Returns all approved bookings.

---

### 13. Get Upcoming Bookings
**GET** `/service/vehicle-bookings/agency/{travelAgencyId}/upcoming`

Returns approved bookings with future start dates.

---

### 14. Get Bookings by Vehicle
**GET** `/service/vehicle-bookings/vehicle/{vehicleId}`

Returns all bookings for a specific vehicle.

---

## Important Notes

### No Payment Required
- Unlike room bookings, vehicle bookings **do not require payment** at booking time
- Payment is handled separately (offline or after service)

### Approval Workflow
- All bookings start with `PENDING_APPROVAL` status
- Travel agency must approve before booking is confirmed
- Only approved bookings can be completed

### Validation Rules
1. **Start date** must be in the future
2. **End date** must be after start date
3. **Booking duration** cannot exceed 90 days
4. **Estimated kilometers** must be between 1 and 10,000
5. Vehicle must be `AVAILABLE` status
6. No conflicting bookings for the vehicle in the same date range

### Cancellation Rules
- Can only cancel `PENDING_APPROVAL` or `APPROVED` bookings
- Cannot cancel after start date has passed
- Cannot cancel `COMPLETED` or `REJECTED` bookings

### Completion Rules
- Only `APPROVED` bookings can be completed
- Can only complete after end date has passed

---

## Example Workflow

### Customer Books a Vehicle
```bash
# 1. Customer creates booking
POST /service/vehicle-bookings/create
{
  "vehicleId": 1,
  "userId": 123,
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "+1234567890",
  "startDate": "2025-11-01",
  "endDate": "2025-11-05",
  "pickupLocation": "Colombo Airport",
  "dropoffLocation": "Galle Fort",
  "estimatedKilometers": 250,
  "withAC": true
}

# Response: booking created with status PENDING_APPROVAL
```

### Travel Agency Approves
```bash
# 2. Travel agency approves the booking
PUT /service/vehicle-bookings/1/approve
{
  "travelAgencyId": 10
}

# Response: booking status changed to APPROVED
```

### After Trip Completion
```bash
# 3. Travel agency marks as completed
PUT /service/vehicle-bookings/1/complete
{
  "travelAgencyId": 10
}

# Response: booking status changed to COMPLETED
```

---

## Error Responses

### 400 Bad Request
```json
{
  "error": "Validation Error",
  "message": "Start date must be in the future"
}
```

### 404 Not Found
```json
{
  "error": "Not Found",
  "message": "Booking not found with ID: 123"
}
```

### 409 Conflict
```json
{
  "error": "Booking Conflict",
  "message": "Vehicle is not available for the selected dates (2025-11-01 to 2025-11-05). There are 2 conflicting bookings."
}
```

---

## Database Schema

### vehicle_bookings Table
```sql
CREATE TABLE vehicle_bookings (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT NOT NULL,
    service_provider_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_email VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    pickup_location VARCHAR(200) NOT NULL,
    dropoff_location VARCHAR(200) NOT NULL,
    estimated_kilometers INTEGER NOT NULL,
    with_ac BOOLEAN NOT NULL,
    price_per_km DECIMAL(10,2) NOT NULL,
    estimated_total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
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

---

## Comparison: Vehicle Booking vs Tour Booking

| Feature | Vehicle Booking | Tour Booking |
|---------|----------------|--------------|
| Payment Required | No | No |
| Approval Needed | Yes (Travel Agency) | Yes (Tour Guide) |
| Date Type | Start/End Date Range | Single Tour Date |
| Capacity Check | Vehicle Availability | Tour Capacity (min/max people) |
| Price Calculation | Kilometers × Price/km | People × Price/person |
| AC Option | Yes (affects pricing) | No |

---

## Files Created

### Entity
- `VehicleBooking.java` - Main booking entity

### Repository
- `VehicleBookingRepository.java` - Database access layer

### Service
- `VehicleBookingService.java` - Business logic

### Controller
- `VehicleBookingController.java` - REST API endpoints

### DTOs
- `CreateVehicleBookingDTO.java` - Request DTO for creating bookings
- `VehicleBookingResponseDTO.java` - Response DTO

### Configuration
- `SecurityConfig.java` - Updated with vehicle booking endpoints
