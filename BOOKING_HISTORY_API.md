# Booking History API Documentation

This API provides unified access to all booking types (Room Bookings, Tour Bookings, and Vehicle Bookings) for a user.

## Base URL
```
/api/booking-history
```

## Endpoints

### 1. Get All Bookings for a User
Get all bookings (rooms, tours, vehicles) for a specific user in one unified response.

**Endpoint:** `GET /api/booking-history/user/{userId}`

**Parameters:**
- `userId` (path) - The ID of the user

**Response:**
```json
[
  {
    "bookingId": 1,
    "bookingType": "ROOM_BOOKING",
    "serviceProviderId": 5,
    "serviceProviderName": "Grand Hotel & Spa",
    "userId": 123,
    "customerName": "John Doe",
    "customerEmail": "john@example.com",
    "customerPhone": "+1234567890",
    "startDate": "2025-01-15",
    "endDate": "2025-01-18",
    "totalAmount": 450.00,
    "status": "CONFIRMED",
    "createdAt": "2025-01-10T10:30:00",
    "updatedAt": "2025-01-10T10:35:00",
    "specialRequests": "Late check-in requested",
    "cancellationReason": null,
    "roomBookingDetails": {
      "roomId": 10,
      "roomType": "Deluxe Suite",
      "numberOfGuests": 2,
      "numberOfNights": 3,
      "pricePerNight": 150.00
    },
    "tourBookingDetails": null,
    "vehicleBookingDetails": null
  },
  {
    "bookingId": 2,
    "bookingType": "TOUR_BOOKING",
    "serviceProviderId": 8,
    "serviceProviderName": "Adventure Tours Lanka",
    "userId": 123,
    "customerName": "John Doe",
    "customerEmail": "john@example.com",
    "customerPhone": "+1234567890",
    "startDate": "2025-01-20",
    "endDate": null,
    "totalAmount": 200.00,
    "status": "APPROVED",
    "createdAt": "2025-01-12T14:20:00",
    "updatedAt": "2025-01-12T15:00:00",
    "specialRequests": "Vegetarian lunch option",
    "cancellationReason": null,
    "roomBookingDetails": null,
    "tourBookingDetails": {
      "tourId": 15,
      "tourTitle": "Sigiriya Rock & Dambulla Cave Temple Day Tour",
      "numberOfPeople": 4,
      "pricePerPerson": 50.00
    },
    "vehicleBookingDetails": null
  },
  {
    "bookingId": 3,
    "bookingType": "VEHICLE_BOOKING",
    "serviceProviderId": 12,
    "serviceProviderName": "Safe Travel Agency",
    "userId": 123,
    "customerName": "John Doe",
    "customerEmail": "john@example.com",
    "customerPhone": "+1234567890",
    "startDate": "2025-02-01",
    "endDate": "2025-02-03",
    "totalAmount": 180.00,
    "status": "PENDING_APPROVAL",
    "createdAt": "2025-01-14T09:00:00",
    "updatedAt": "2025-01-14T09:00:00",
    "specialRequests": "Airport pickup at 6 AM",
    "cancellationReason": null,
    "roomBookingDetails": null,
    "tourBookingDetails": null,
    "vehicleBookingDetails": {
      "vehicleId": 20,
      "vehicleType": "Van",
      "vehicleBrand": "Toyota",
      "vehicleModel": "KDH",
      "pickupLocation": "Bandaranaike International Airport",
      "dropoffLocation": "Colombo Fort Railway Station",
      "estimatedKilometers": 45,
      "withAC": true,
      "pricePerKm": 4.00
    }
  }
]
```

**Booking Types:**
- `ROOM_BOOKING` - Hotel room bookings
- `TOUR_BOOKING` - Tour guide bookings
- `VEHICLE_BOOKING` - Travel agency vehicle bookings

**Booking Statuses:**
- Room Bookings: `PENDING`, `CONFIRMED`, `CHECKED_IN`, `CHECKED_OUT`, `CANCELLED`, `COMPLETED`
- Tour Bookings: `PENDING_APPROVAL`, `APPROVED`, `REJECTED`, `CANCELLED`, `COMPLETED`
- Vehicle Bookings: `PENDING_APPROVAL`, `APPROVED`, `REJECTED`, `CANCELLED`, `COMPLETED`

---

### 2. Get Room Bookings for a User
Get only room bookings for a specific user.

**Endpoint:** `GET /api/booking-history/user/{userId}/rooms`

**Parameters:**
- `userId` (path) - The ID of the user

**Response:** Array of booking history objects with `bookingType: "ROOM_BOOKING"`

---

### 3. Get Tour Bookings for a User
Get only tour bookings for a specific user.

**Endpoint:** `GET /api/booking-history/user/{userId}/tours`

**Parameters:**
- `userId` (path) - The ID of the user

**Response:** Array of booking history objects with `bookingType: "TOUR_BOOKING"`

---

### 4. Get Vehicle Bookings for a User
Get only vehicle bookings for a specific user.

**Endpoint:** `GET /api/booking-history/user/{userId}/vehicles`

**Parameters:**
- `userId` (path) - The ID of the user

**Response:** Array of booking history objects with `bookingType: "VEHICLE_BOOKING"`

---

### 5. Get Specific Booking Details
Get details of a specific booking by ID and type.

**Endpoint:** `GET /api/booking-history/{bookingId}?type={BOOKING_TYPE}`

**Parameters:**
- `bookingId` (path) - The ID of the booking
- `type` (query) - The booking type: `ROOM_BOOKING`, `TOUR_BOOKING`, or `VEHICLE_BOOKING`

**Example:**
```
GET /api/booking-history/15?type=TOUR_BOOKING
```

**Response:** Single booking history object

---

## Security

All booking history endpoints are publicly accessible (no authentication required). However, in a production environment, you should:

1. Add authentication to ensure users can only access their own booking history
2. Implement proper authorization checks
3. Add rate limiting to prevent abuse

## Example Usage

### JavaScript/Fetch
```javascript
// Get all bookings for user
const userId = 123;
const response = await fetch(`/api/booking-history/user/${userId}`);
const bookings = await response.json();

// Filter by booking type on client-side
const roomBookings = bookings.filter(b => b.bookingType === 'ROOM_BOOKING');
const tourBookings = bookings.filter(b => b.bookingType === 'TOUR_BOOKING');
const vehicleBookings = bookings.filter(b => b.bookingType === 'VEHICLE_BOOKING');

// Or use dedicated endpoints
const roomBookingsOnly = await fetch(`/api/booking-history/user/${userId}/rooms`).then(r => r.json());
const tourBookingsOnly = await fetch(`/api/booking-history/user/${userId}/tours`).then(r => r.json());
const vehicleBookingsOnly = await fetch(`/api/booking-history/user/${userId}/vehicles`).then(r => r.json());
```

### cURL
```bash
# Get all bookings
curl http://localhost:8080/api/booking-history/user/123

# Get only room bookings
curl http://localhost:8080/api/booking-history/user/123/rooms

# Get specific booking
curl "http://localhost:8080/api/booking-history/15?type=TOUR_BOOKING"
```

## Notes

- All bookings are sorted by creation date (newest first)
- The `roomBookingDetails`, `tourBookingDetails`, and `vehicleBookingDetails` fields are mutually exclusive - only one will be populated based on the booking type
- The `startDate` represents:
  - `checkInDate` for room bookings
  - `tourDate` for tour bookings
  - `startDate` for vehicle bookings
- The `endDate` represents:
  - `checkOutDate` for room bookings
  - `null` for tour bookings (tours are typically single-day)
  - `endDate` for vehicle bookings
