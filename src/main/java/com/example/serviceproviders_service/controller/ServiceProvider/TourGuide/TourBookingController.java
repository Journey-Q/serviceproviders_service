package com.example.serviceproviders_service.controller.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.CreateTourBookingDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.TourBookingPaymentDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.TourBookingPaymentResponseDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.TourBookingResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourBooking;
import com.example.serviceproviders_service.services.ServiceProvider.TourGuide.TourBookingService;
import com.example.serviceproviders_service.services.ServiceProvider.TourGuide.TourBookingStripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/service/tourbookings")
@CrossOrigin(origins = "*")
public class TourBookingController {

    private final TourBookingService tourBookingService;
    private final TourBookingStripeService stripeService;

    public TourBookingController(TourBookingService tourBookingService,
                                 TourBookingStripeService stripeService) {
        this.tourBookingService = tourBookingService;
        this.stripeService = stripeService;
    }

    @PostMapping("/create")
    public ResponseEntity<TourBookingResponseDTO> createTourBooking(@RequestBody CreateTourBookingDTO dto) {
        TourBookingResponseDTO createdTourBooking = tourBookingService.createTourBooking(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTourBooking);
    }

    @PostMapping("/create-with-payment")
    public ResponseEntity<TourBookingPaymentResponseDTO> createTourBookingWithPayment(@RequestBody TourBookingPaymentDTO dto) {
        TourBookingPaymentResponseDTO response = stripeService.createTourBookingWithPayment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourBookingResponseDTO> getTourBookingById(@PathVariable Long id) {
        TourBookingResponseDTO tourBooking = tourBookingService.getTourBookingById(id);
        return ResponseEntity.ok(tourBooking);
    }

    @GetMapping("/reference/{bookingReference}")
    public ResponseEntity<TourBookingResponseDTO> getTourBookingByReference(@PathVariable String bookingReference) {
        TourBookingResponseDTO tourBooking = tourBookingService.getTourBookingByReference(bookingReference);
        return ResponseEntity.ok(tourBooking);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<TourBookingResponseDTO> getTourBookingBySessionId(@PathVariable String sessionId) {
        TourBookingResponseDTO tourBooking = tourBookingService.getTourBookingByStripeSessionId(sessionId);
        return ResponseEntity.ok(tourBooking);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TourBookingResponseDTO>> getAllTourBookings() {
        List<TourBookingResponseDTO> tourBookings = tourBookingService.getAllTourBookings();
        return ResponseEntity.ok(tourBookings);
    }

    @GetMapping("/service-provider/{serviceProviderId}")
    public ResponseEntity<List<TourBookingResponseDTO>> getTourBookingsByServiceProviderId(@PathVariable Long serviceProviderId) {
        List<TourBookingResponseDTO> tourBookings = tourBookingService.getTourBookingsByServiceProviderId(serviceProviderId);
        return ResponseEntity.ok(tourBookings);
    }

    @GetMapping("/customer/{customerEmail}")
    public ResponseEntity<List<TourBookingResponseDTO>> getTourBookingsByCustomerEmail(@PathVariable String customerEmail) {
        List<TourBookingResponseDTO> tourBookings = tourBookingService.getTourBookingsByCustomerEmail(customerEmail);
        return ResponseEntity.ok(tourBookings);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TourBookingResponseDTO>> getTourBookingsByUserId(@PathVariable Long userId) {
        List<TourBookingResponseDTO> tourBookings = tourBookingService.getTourBookingsByUserId(userId);
        return ResponseEntity.ok(tourBookings);
    }

    // Payment related endpoints
    @GetMapping("/payments/status/{paymentStatus}")
    public ResponseEntity<List<TourBookingResponseDTO>> getTourBookingsByPaymentStatus(@PathVariable TourBooking.PaymentStatus paymentStatus) {
        List<TourBookingResponseDTO> tourBookings = tourBookingService.getTourBookingsByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(tourBookings);
    }

    @GetMapping("/payments/successful")
    public ResponseEntity<List<TourBookingResponseDTO>> getSuccessfulPayments() {
        List<TourBookingResponseDTO> tourBookings = tourBookingService.getSuccessfulPayments();
        return ResponseEntity.ok(tourBookings);
    }

    @GetMapping("/payments/date-range")
    public ResponseEntity<List<TourBookingResponseDTO>> getPaymentsByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<TourBookingResponseDTO> tourBookings = tourBookingService.getPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(tourBookings);
    }

    @GetMapping("/revenue/total")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        BigDecimal totalRevenue = tourBookingService.getTotalRevenue();
        return ResponseEntity.ok(totalRevenue);
    }

    @GetMapping("/count/payment-status/{paymentStatus}")
    public ResponseEntity<Long> countBookingsByPaymentStatus(@PathVariable TourBooking.PaymentStatus paymentStatus) {
        long count = tourBookingService.countBookingsByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(count);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TourBookingResponseDTO> updateTourBookingStatus(
            @PathVariable Long id,
            @RequestParam TourBooking.TourBookingStatus status) {
        TourBookingResponseDTO updatedTourBooking = tourBookingService.updateTourBookingStatus(id, status);
        return ResponseEntity.ok(updatedTourBooking);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<String> cancelTourBooking(@PathVariable Long id) {
        boolean response = tourBookingService.cancelTourBooking(id);
        if (response) {
            return ResponseEntity.ok("Tour booking cancelled successfully");
        }
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/refund")
    public ResponseEntity<TourBookingResponseDTO> refundPayment(
            @PathVariable Long id,
            @RequestParam String refundReason) {
        TourBookingResponseDTO refundedBooking = tourBookingService.refundPayment(id, refundReason);
        return ResponseEntity.ok(refundedBooking);
    }

    // Stripe webhook endpoints
    @PostMapping("/webhook/payment-success")
    public ResponseEntity<TourBookingResponseDTO> handlePaymentSuccess(
            @RequestParam String sessionId,
            @RequestParam String paymentIntentId) {
        TourBookingResponseDTO updatedBooking = stripeService.handlePaymentSuccess(sessionId, paymentIntentId);
        return ResponseEntity.ok(updatedBooking);
    }

    @PostMapping("/webhook/payment-failure")
    public ResponseEntity<TourBookingResponseDTO> handlePaymentFailure(
            @RequestParam String sessionId,
            @RequestParam String failureReason) {
        TourBookingResponseDTO updatedBooking = stripeService.handlePaymentFailure(sessionId, failureReason);
        return ResponseEntity.ok(updatedBooking);
    }
}