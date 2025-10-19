package com.example.serviceproviders_service.controller.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.CreateVehicleBookingDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.VehicleBookingResponseDTO;
import com.example.serviceproviders_service.services.ServiceProvider.TravelAgency.VehicleBookingService;
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
@RequestMapping("/service/vehicle-bookings")
@CrossOrigin(origins = "*")
public class VehicleBookingController {

    @Autowired
    private VehicleBookingService bookingService;

    /**
     * Create a new vehicle booking (Public - any customer can book)
     * Status will be PENDING_APPROVAL - waiting for travel agency to accept
     * No payment required at booking time
     */
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@Valid @RequestBody CreateVehicleBookingDTO dto) {
        try {
            VehicleBookingResponseDTO response = bookingService.createBooking(dto);
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
            VehicleBookingResponseDTO response = bookingService.getBookingById(bookingId);
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
            List<VehicleBookingResponseDTO> bookings = bookingService.getBookingsByCustomerEmail(email);
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
            List<VehicleBookingResponseDTO> bookings = bookingService.getBookingsByUserId(userId);
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
            @RequestBody(required = false) Map<String, String> requestBody) {
        try {
            String cancellationReason = requestBody != null ?
                    requestBody.getOrDefault("cancellationReason", "Customer cancelled the booking") :
                    "Customer cancelled the booking";
            VehicleBookingResponseDTO response = bookingService.cancelBooking(bookingId, cancellationReason);
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
     * Travel Agency approves a booking (Travel Agent only)
     */
    @PutMapping("/{bookingId}/approve")
    @PreAuthorize("hasRole('TRAVEL_AGENT')")
    public ResponseEntity<?> approveBooking(
            @PathVariable Long bookingId,
            @RequestBody Map<String, Long> requestBody) {
        try {
            Long travelAgencyId = requestBody.get("travelAgencyId");
            if (travelAgencyId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Validation Error", "Travel agency ID is required"));
            }

            VehicleBookingResponseDTO response = bookingService.approveBooking(bookingId, travelAgencyId);
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
     * Travel Agency rejects a booking (Travel Agent only)
     */
    @PutMapping("/{bookingId}/reject")
    @PreAuthorize("hasRole('TRAVEL_AGENT')")
    public ResponseEntity<?> rejectBooking(
            @PathVariable Long bookingId,
            @RequestBody Map<String, Object> requestBody) {
        try {
            Long travelAgencyId = requestBody.get("travelAgencyId") != null ?
                    Long.valueOf(requestBody.get("travelAgencyId").toString()) : null;
            String rejectionReason = (String) requestBody.get("rejectionReason");

            if (travelAgencyId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Validation Error", "Travel agency ID is required"));
            }

            VehicleBookingResponseDTO response = bookingService.rejectBooking(bookingId, travelAgencyId, rejectionReason);
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
     * Travel Agency marks booking as completed (Travel Agent only)
     */
    @PutMapping("/{bookingId}/complete")
    @PreAuthorize("hasRole('TRAVEL_AGENT')")
    public ResponseEntity<?> completeBooking(
            @PathVariable Long bookingId,
            @RequestBody Map<String, Long> requestBody) {
        try {
            Long travelAgencyId = requestBody.get("travelAgencyId");
            if (travelAgencyId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Validation Error", "Travel agency ID is required"));
            }

            VehicleBookingResponseDTO response = bookingService.completeBooking(bookingId, travelAgencyId);
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
     * Get all bookings for a specific vehicle (Travel Agent only)
     */
    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasRole('TRAVEL_AGENT')")
    public ResponseEntity<?> getBookingsByVehicle(@PathVariable Long vehicleId) {
        try {
            List<VehicleBookingResponseDTO> bookings = bookingService.getBookingsByVehicle(vehicleId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get all bookings for a travel agency (Travel Agent only)
     */
    @GetMapping("/agency/{travelAgencyId}")
    @PreAuthorize("hasRole('TRAVEL_AGENT')")
    public ResponseEntity<?> getBookingsByTravelAgency(@PathVariable Long travelAgencyId) {
        try {
            List<VehicleBookingResponseDTO> bookings = bookingService.getBookingsByTravelAgency(travelAgencyId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get pending approval bookings for a travel agency (Travel Agent only)
     */
    @GetMapping("/agency/{travelAgencyId}/pending")
    @PreAuthorize("hasRole('TRAVEL_AGENT')")
    public ResponseEntity<?> getPendingApprovalBookings(@PathVariable Long travelAgencyId) {
        try {
            List<VehicleBookingResponseDTO> bookings = bookingService.getPendingApprovalBookings(travelAgencyId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get approved bookings for a travel agency (Travel Agent only)
     */
    @GetMapping("/agency/{travelAgencyId}/approved")
    @PreAuthorize("hasRole('TRAVEL_AGENT')")
    public ResponseEntity<?> getApprovedBookings(@PathVariable Long travelAgencyId) {
        try {
            List<VehicleBookingResponseDTO> bookings = bookingService.getApprovedBookings(travelAgencyId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get upcoming bookings for a travel agency (Travel Agent only)
     */
    @GetMapping("/agency/{travelAgencyId}/upcoming")
    @PreAuthorize("hasRole('TRAVEL_AGENT')")
    public ResponseEntity<?> getUpcomingBookings(@PathVariable Long travelAgencyId) {
        try {
            List<VehicleBookingResponseDTO> bookings = bookingService.getUpcomingBookings(travelAgencyId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Check if vehicle is available for a date range (Public)
     */
    @GetMapping("/vehicle/{vehicleId}/availability")
    public ResponseEntity<?> checkVehicleAvailability(
            @PathVariable Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            boolean isAvailable = bookingService.isVehicleAvailable(vehicleId, startDate, endDate);
            Map<String, Object> response = new HashMap<>();
            response.put("vehicleId", vehicleId);
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("isAvailable", isAvailable);
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
