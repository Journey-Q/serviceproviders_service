package com.example.serviceproviders_service.controller.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.VehicleBookingResponseDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.CreateVehicleBookingDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.VehicleBookingPaymentDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.VehicleBookingPaymentResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.VehicleBooking;
import com.example.serviceproviders_service.services.ServiceProvider.TravelAgency.VehicleBookingService;
import com.example.serviceproviders_service.services.ServiceProvider.TravelAgency.VehicleBookingStripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/service/vehiclebookings")
@CrossOrigin(origins = "*")
public class VehicleBookingController {

    private final VehicleBookingService vehicleBookingService;
    private final VehicleBookingStripeService stripeService;

    public VehicleBookingController(VehicleBookingService vehicleBookingService,
                                    VehicleBookingStripeService stripeService) {
        this.vehicleBookingService = vehicleBookingService;
        this.stripeService = stripeService;
    }

    @PostMapping("/create")
    public ResponseEntity<VehicleBookingResponseDTO> createVehicleBooking(@RequestBody CreateVehicleBookingDTO dto) {
        VehicleBookingResponseDTO createdVehicleBooking = vehicleBookingService.createVehicleBooking(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicleBooking);
    }

    @PostMapping("/create-with-payment")
    public ResponseEntity<VehicleBookingPaymentResponseDTO> createVehicleBookingWithPayment(@RequestBody VehicleBookingPaymentDTO dto) {
        VehicleBookingPaymentResponseDTO response = stripeService.createVehicleBookingWithPayment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleBookingResponseDTO> getVehicleBookingById(@PathVariable Long id) {
        VehicleBookingResponseDTO vehicleBooking = vehicleBookingService.getVehicleBookingById(id);
        return ResponseEntity.ok(vehicleBooking);
    }

    @GetMapping("/reference/{bookingReference}")
    public ResponseEntity<VehicleBookingResponseDTO> getVehicleBookingByReference(@PathVariable String bookingReference) {
        VehicleBookingResponseDTO vehicleBooking = vehicleBookingService.getVehicleBookingByReference(bookingReference);
        return ResponseEntity.ok(vehicleBooking);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<VehicleBookingResponseDTO> getVehicleBookingBySessionId(@PathVariable String sessionId) {
        VehicleBookingResponseDTO vehicleBooking = vehicleBookingService.getVehicleBookingByStripeSessionId(sessionId);
        return ResponseEntity.ok(vehicleBooking);
    }

    @GetMapping("/all")
    public ResponseEntity<List<VehicleBookingResponseDTO>> getAllVehicleBookings() {
        List<VehicleBookingResponseDTO> vehicleBookings = vehicleBookingService.getAllVehicleBookings();
        return ResponseEntity.ok(vehicleBookings);
    }

    @GetMapping("/service-provider/{serviceProviderId}")
    public ResponseEntity<List<VehicleBookingResponseDTO>> getVehicleBookingsByServiceProviderId(@PathVariable Long serviceProviderId) {
        List<VehicleBookingResponseDTO> vehicleBookings = vehicleBookingService.getVehicleBookingsByServiceProviderId(serviceProviderId);
        return ResponseEntity.ok(vehicleBookings);
    }

    @GetMapping("/customer/{customerEmail}")
    public ResponseEntity<List<VehicleBookingResponseDTO>> getVehicleBookingsByCustomerEmail(@PathVariable String customerEmail) {
        List<VehicleBookingResponseDTO> vehicleBookings = vehicleBookingService.getVehicleBookingsByCustomerEmail(customerEmail);
        return ResponseEntity.ok(vehicleBookings);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VehicleBookingResponseDTO>> getVehicleBookingsByUserId(@PathVariable Long userId) {
        List<VehicleBookingResponseDTO> vehicleBookings = vehicleBookingService.getVehicleBookingsByUserId(userId);
        return ResponseEntity.ok(vehicleBookings);
    }

    // Payment related endpoints
    @GetMapping("/payments/status/{paymentStatus}")
    public ResponseEntity<List<VehicleBookingResponseDTO>> getVehicleBookingsByPaymentStatus(@PathVariable VehicleBooking.PaymentStatus paymentStatus) {
        List<VehicleBookingResponseDTO> vehicleBookings = vehicleBookingService.getVehicleBookingsByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(vehicleBookings);
    }

    @GetMapping("/payments/successful")
    public ResponseEntity<List<VehicleBookingResponseDTO>> getSuccessfulPayments() {
        List<VehicleBookingResponseDTO> vehicleBookings = vehicleBookingService.getSuccessfulPayments();
        return ResponseEntity.ok(vehicleBookings);
    }

    @GetMapping("/payments/date-range")
    public ResponseEntity<List<VehicleBookingResponseDTO>> getPaymentsByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<VehicleBookingResponseDTO> vehicleBookings = vehicleBookingService.getPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(vehicleBookings);
    }

    @GetMapping("/revenue/total")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        BigDecimal totalRevenue = vehicleBookingService.getTotalRevenue();
        return ResponseEntity.ok(totalRevenue);
    }

    @GetMapping("/count/payment-status/{paymentStatus}")
    public ResponseEntity<Long> countBookingsByPaymentStatus(@PathVariable VehicleBooking.PaymentStatus paymentStatus) {
        long count = vehicleBookingService.countBookingsByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(count);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<VehicleBookingResponseDTO> updateVehicleBookingStatus(
            @PathVariable Long id,
            @RequestParam VehicleBooking.VehicleBookingStatus status) {
        VehicleBookingResponseDTO updatedVehicleBooking = vehicleBookingService.updateVehicleBookingStatus(id, status);
        return ResponseEntity.ok(updatedVehicleBooking);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<String> cancelVehicleBooking(@PathVariable Long id) {
        boolean response = vehicleBookingService.cancelVehicleBooking(id);
        if (response) {
            return ResponseEntity.ok("Vehicle booking cancelled successfully");
        }
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/refund")
    public ResponseEntity<VehicleBookingResponseDTO> refundPayment(
            @PathVariable Long id,
            @RequestParam String refundReason) {
        VehicleBookingResponseDTO refundedBooking = vehicleBookingService.refundPayment(id, refundReason);
        return ResponseEntity.ok(refundedBooking);
    }

    // Stripe webhook endpoints
    @PostMapping("/webhook/payment-success")
    public ResponseEntity<VehicleBookingResponseDTO> handlePaymentSuccess(
            @RequestParam String sessionId,
            @RequestParam String paymentIntentId) {
        VehicleBookingResponseDTO updatedBooking = stripeService.handlePaymentSuccess(sessionId, paymentIntentId);
        return ResponseEntity.ok(updatedBooking);
    }

    @PostMapping("/webhook/payment-failure")
    public ResponseEntity<VehicleBookingResponseDTO> handlePaymentFailure(
            @RequestParam String sessionId,
            @RequestParam String failureReason) {
        VehicleBookingResponseDTO updatedBooking = stripeService.handlePaymentFailure(sessionId, failureReason);
        return ResponseEntity.ok(updatedBooking);
    }
}
