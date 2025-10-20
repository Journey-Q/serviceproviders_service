package com.example.serviceproviders_service.controller.ServiceProvider.Hotel;

import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.PaymentHistoryResponseDTO;
import com.example.serviceproviders_service.services.ServiceProvider.Hotel.PaymentHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/service/payment-history")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PaymentHistoryController {

    private final PaymentHistoryService paymentHistoryService;

    /**
     * Get all completed payments for a service provider (Hotel staff only)
     * GET /service/payment-history/provider/{serviceProviderId}
     */
    @GetMapping("/provider/{serviceProviderId}")
    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<?> getAllCompletedPayments(@PathVariable Long serviceProviderId) {
        try {
            List<PaymentHistoryResponseDTO> payments = paymentHistoryService.getAllCompletedPayments(serviceProviderId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get payments filtered by month and year (Hotel staff only)
     * GET /service/payment-history/provider/{serviceProviderId}/filter?month=6&year=2025
     * Note: If month is omitted or 0, returns all payments for the year
     */
    @GetMapping("/provider/{serviceProviderId}/filter")
    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<?> getPaymentsByMonthAndYear(
            @PathVariable Long serviceProviderId,
            @RequestParam(required = false, defaultValue = "0") Integer month,
            @RequestParam(required = true) Integer year) {
        try {
            List<PaymentHistoryResponseDTO> payments = paymentHistoryService.getPaymentsByMonthAndYear(
                    serviceProviderId, month, year
            );
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get payment details by ID (Hotel staff only)
     * GET /service/payment-history/{paymentId}
     */
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<?> getPaymentById(@PathVariable Long paymentId) {
        try {
            PaymentHistoryResponseDTO payment = paymentHistoryService.getPaymentById(paymentId);
            return ResponseEntity.ok(payment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get payment details by payment ID (Hotel staff only)
     * GET /service/payment-history/payment/{paymentId}
     */
    @GetMapping("/payment/{paymentId}")
    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<?> getPaymentByPaymentId(@PathVariable String paymentId) {
        try {
            PaymentHistoryResponseDTO payment = paymentHistoryService.getPaymentByPaymentId(paymentId);
            return ResponseEntity.ok(payment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Not Found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get all payments for a specific room (Hotel staff only)
     * GET /service/payment-history/room/{roomId}
     */
    @GetMapping("/room/{roomId}")
    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<?> getPaymentsByRoom(@PathVariable Long roomId) {
        try {
            List<PaymentHistoryResponseDTO> payments = paymentHistoryService.getPaymentsByRoom(roomId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server Error", e.getMessage()));
        }
    }

    /**
     * Get all payments by guest email (Hotel staff only)
     * GET /service/payment-history/guest/{email}
     */
    @GetMapping("/guest/{email}")
    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<?> getPaymentsByGuestEmail(@PathVariable String email) {
        try {
            List<PaymentHistoryResponseDTO> payments = paymentHistoryService.getPaymentsByGuestEmail(email);
            return ResponseEntity.ok(payments);
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