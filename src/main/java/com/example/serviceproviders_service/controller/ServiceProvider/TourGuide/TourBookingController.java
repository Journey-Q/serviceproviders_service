package com.example.serviceproviders_service.controller.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.CreateTourBookingDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.TourBookingResponseDTO;
import com.example.serviceproviders_service.services.ServiceProvider.TourGuide.TourBookingService;
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
@RequestMapping("/service/tour-bookings")
@CrossOrigin(origins = "*")
public class TourBookingController {

    @Autowired
    private TourBookingService bookingService;

    /**
     * Create a new tour booking (Public - any customer can book)
     * Status will be PENDING_APPROVAL - waiting for tour guide to accept
     */
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@Valid @RequestBody CreateTourBookingDTO dto) {
        try {
            TourBookingResponseDTO response = bookingService.createBooking(dto);
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
            TourBookingResponseDTO response = bookingService.getBookingById(bookingId);
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
            List<TourBookingResponseDTO> bookings = bookingService.getBookingsByCustomerEmail(email);
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
            List<TourBookingResponseDTO> bookings = bookingService.getBookingsByUserId(userId);
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
            TourBookingResponseDTO response = bookingService.cancelBooking(bookingId, cancellationReason);
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
     * Tour Guide approves a booking (Tour Guide only)
     */
    @PutMapping("/{bookingId}/approve")
    @PreAuthorize("hasRole('TOUR_GUIDE')")
    public ResponseEntity<?> approveBooking(
            @PathVariable Long bookingId,
            @RequestBody Map<String, Long> requestBody) {
        try {
            Long tourGuideId = requestBody.get("tourGuideId");
            if (tourGuideId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Validation Error", "Tour guide ID is required"));
            }

            TourBookingResponseDTO response = bookingService.approveBooking(bookingId, tourGuideId);
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
     * Tour Guide rejects a booking (Tour Guide only)
     */
    @PutMapping("/{bookingId}/reject")
    @PreAuthorize("hasRole('TOUR_GUIDE')")
    public ResponseEntity<?> rejectBooking(
            @PathVariable Long bookingId,
            @RequestBody Map<String, Object> requestBody) {
        try {
            Long tourGuideId = requestBody.get("tourGuideId") != null ?
                    Long.valueOf(requestBody.get("tourGuideId").toString()) : null;
            String rejectionReason = (String) requestBody.get("rejectionReason");

            if (tourGuideId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Validation Error", "Tour guide ID is required"));
            }

            TourBookingResponseDTO response = bookingService.rejectBooking(bookingId, tourGuideId, rejectionReason);
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
     * Tour Guide marks booking as completed (Tour Guide only)
     */
    @PutMapping("/{bookingId}/complete")
    @PreAuthorize("hasRole('TOUR_GUIDE')")
    public ResponseEntity<?> completeBooking(
            @PathVariable Long bookingId,
            @RequestBody Map<String, Long> requestBody) {
        try {
            Long tourGuideId = requestBody.get("tourGuideId");
            if (tourGuideId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Validation Error", "Tour guide ID is required"));
            }

            TourBookingResponseDTO response = bookingService.completeBooking(bookingId, tourGuideId);
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
     * Get all bookings for a specific tour (Tour Guide only)
     */
    @GetMapping("/tour/{tourId}")
    @PreAuthorize("hasRole('TOUR_GUIDE')")
    public ResponseEntity<?> getBookingsByTour(@PathVariable Long tourId) {
        try {
            List<TourBookingResponseDTO> bookings = bookingService.getBookingsByTour(tourId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get all bookings for a tour guide (Tour Guide only)
     */
    @GetMapping("/guide/{tourGuideId}")
    @PreAuthorize("hasRole('TOUR_GUIDE')")
    public ResponseEntity<?> getBookingsByTourGuide(@PathVariable Long tourGuideId) {
        try {
            List<TourBookingResponseDTO> bookings = bookingService.getBookingsByTourGuide(tourGuideId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get pending approval bookings for a tour guide (Tour Guide only)
     */
    @GetMapping("/guide/{tourGuideId}/pending")
    @PreAuthorize("hasRole('TOUR_GUIDE')")
    public ResponseEntity<?> getPendingApprovalBookings(@PathVariable Long tourGuideId) {
        try {
            List<TourBookingResponseDTO> bookings = bookingService.getPendingApprovalBookings(tourGuideId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get approved bookings for a tour guide (Tour Guide only)
     */
    @GetMapping("/guide/{tourGuideId}/approved")
    @PreAuthorize("hasRole('TOUR_GUIDE')")
    public ResponseEntity<?> getApprovedBookings(@PathVariable Long tourGuideId) {
        try {
            List<TourBookingResponseDTO> bookings = bookingService.getApprovedBookings(tourGuideId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get upcoming tours for a tour guide (Tour Guide only)
     */
    @GetMapping("/guide/{tourGuideId}/upcoming")
    @PreAuthorize("hasRole('TOUR_GUIDE')")
    public ResponseEntity<?> getUpcomingTours(@PathVariable Long tourGuideId) {
        try {
            List<TourBookingResponseDTO> bookings = bookingService.getUpcomingTours(tourGuideId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get available capacity for a tour on a specific date (Public)
     */
    @GetMapping("/tour/{tourId}/capacity")
    public ResponseEntity<?> getAvailableCapacity(
            @PathVariable Long tourId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tourDate) {
        try {
            int availableCapacity = bookingService.getAvailableCapacity(tourId, tourDate);
            Map<String, Object> response = new HashMap<>();
            response.put("tourId", tourId);
            response.put("tourDate", tourDate);
            response.put("availableCapacity", availableCapacity);
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
