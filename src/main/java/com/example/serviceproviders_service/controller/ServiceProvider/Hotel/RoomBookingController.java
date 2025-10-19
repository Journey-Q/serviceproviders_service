package com.example.serviceproviders_service.controller.ServiceProvider.Hotel;

import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.CreateRoomBookingDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.RoomBookingResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.RoomBooking;
import com.example.serviceproviders_service.services.ServiceProvider.Hotel.RoomBookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/service/room-bookings")
@CrossOrigin(origins = "*")
public class RoomBookingController {

    @Autowired
    private RoomBookingService bookingService;

    /**
     * Create a new room booking (Public - any customer can book)
     */
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@Valid @RequestBody CreateRoomBookingDTO dto) {
        try {
            RoomBookingResponseDTO response = bookingService.createBooking(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("Booking Conflict", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Get booking by ID (Public - customer needs to check their booking)
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId) {
        try {
            RoomBookingResponseDTO response = bookingService.getBookingById(bookingId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        }
    }

    /**
     * Get all bookings by customer email (Public - customer can see their bookings)
     */
    @GetMapping("/customer/{email}")
    public ResponseEntity<?> getBookingsByCustomerEmail(@PathVariable String email) {
        try {
            List<RoomBookingResponseDTO> bookings = bookingService.getBookingsByCustomerEmail(email);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get all bookings by user ID (Public - user can see their bookings)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingsByUserId(@PathVariable Long userId) {
        try {
            List<RoomBookingResponseDTO> bookings = bookingService.getBookingsByUserId(userId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get all bookings for a specific room (Hotel staff only)
     */
    @GetMapping("/room/{roomId}")
    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<?> getBookingsByRoom(@PathVariable Long roomId) {
        try {
            List<RoomBookingResponseDTO> bookings = bookingService.getBookingsByRoom(roomId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get all bookings for a service provider (Hotel staff only)
     */
    @GetMapping("/provider/{serviceProviderId}")
    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<?> getBookingsByServiceProvider(@PathVariable Long serviceProviderId) {
        try {
            List<RoomBookingResponseDTO> bookings = bookingService.getBookingsByServiceProvider(serviceProviderId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get upcoming bookings for a service provider (Hotel staff only)
     */
    @GetMapping("/provider/{serviceProviderId}/upcoming")
    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<?> getUpcomingBookings(@PathVariable Long serviceProviderId) {
        try {
            List<RoomBookingResponseDTO> bookings = bookingService.getUpcomingBookings(serviceProviderId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Cancel a booking (Public - customers can cancel their bookings)
     */
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long bookingId,
            @RequestBody Map<String, String> requestBody) {
        try {
            String cancellationReason = requestBody.getOrDefault("cancellationReason", "Customer requested cancellation");
            RoomBookingResponseDTO response = bookingService.cancelBooking(bookingId, cancellationReason);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Invalid Operation", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Update booking status (Hotel staff only - for check-in/check-out)
     */
    @PutMapping("/{bookingId}/status")
    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<?> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestBody Map<String, String> requestBody) {
        try {
            String statusStr = requestBody.get("status");
            if (statusStr == null || statusStr.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Validation Error", "Status is required"));
            }

            RoomBooking.BookingStatus newStatus = RoomBooking.BookingStatus.valueOf(statusStr.toUpperCase());
            RoomBookingResponseDTO response = bookingService.updateBookingStatus(bookingId, newStatus);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Invalid Status", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Invalid Operation", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Check room availability for specific dates (Public)
     */
    @GetMapping("/check-availability")
    public ResponseEntity<?> checkAvailability(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {
        try {
            boolean isAvailable = bookingService.isRoomAvailable(roomId, checkInDate, checkOutDate);
            Map<String, Object> response = new HashMap<>();
            response.put("roomId", roomId);
            response.put("checkInDate", checkInDate);
            response.put("checkOutDate", checkOutDate);
            response.put("available", isAvailable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Helper method to create error response
     */
    private Map<String, String> createErrorResponse(String error, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        return errorResponse;
    }
}
