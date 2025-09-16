package com.example.serviceproviders_service.controller.ServiceProvider.Hotel;

import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.RoomBookingResponseDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.CreateRoomBookingDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.RoomBookingPaymentDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.RoomBookingPaymentResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.RoomBooking;
import com.example.serviceproviders_service.services.ServiceProvider.Hotel.RoomBookingService;
import com.example.serviceproviders_service.services.ServiceProvider.Hotel.HotelRoomBookingStripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/service/roombookings")
@CrossOrigin(origins = "*")
public class RoomBookingController {

    private final RoomBookingService roomBookingService;
    private final HotelRoomBookingStripeService stripeService;

    public RoomBookingController(RoomBookingService roomBookingService,
                                 HotelRoomBookingStripeService stripeService) {
        this.roomBookingService = roomBookingService;
        this.stripeService = stripeService;
    }

    @PostMapping("/create")
    public ResponseEntity<RoomBookingResponseDTO> createRoomBooking(@RequestBody CreateRoomBookingDTO dto) {
        RoomBookingResponseDTO createdRoomBooking = roomBookingService.createRoomBooking(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoomBooking);
    }

    @PostMapping("/create-with-payment")
    public ResponseEntity<RoomBookingPaymentResponseDTO> createRoomBookingWithPayment(@RequestBody RoomBookingPaymentDTO dto) {
        RoomBookingPaymentResponseDTO response = stripeService.createRoomBookingWithPayment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomBookingResponseDTO> getRoomBookingById(@PathVariable Long id) {
        RoomBookingResponseDTO roomBooking = roomBookingService.getRoomBookingById(id);
        return ResponseEntity.ok(roomBooking);
    }

    @GetMapping("/reference/{bookingReference}")
    public ResponseEntity<RoomBookingResponseDTO> getRoomBookingByReference(@PathVariable String bookingReference) {
        RoomBookingResponseDTO roomBooking = roomBookingService.getRoomBookingByReference(bookingReference);
        return ResponseEntity.ok(roomBooking);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<RoomBookingResponseDTO> getRoomBookingBySessionId(@PathVariable String sessionId) {
        RoomBookingResponseDTO roomBooking = roomBookingService.getRoomBookingByStripeSessionId(sessionId);
        return ResponseEntity.ok(roomBooking);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RoomBookingResponseDTO>> getAllRoomBookings() {
        List<RoomBookingResponseDTO> roomBookings = roomBookingService.getAllRoomBookings();
        return ResponseEntity.ok(roomBookings);
    }

    @GetMapping("/service-provider/{serviceProviderId}")
    public ResponseEntity<List<RoomBookingResponseDTO>> getRoomBookingsByServiceProviderId(@PathVariable Long serviceProviderId) {
        List<RoomBookingResponseDTO> roomBookings = roomBookingService.getRoomBookingsByServiceProviderId(serviceProviderId);
        return ResponseEntity.ok(roomBookings);
    }

    @GetMapping("/guest/{guestEmail}")
    public ResponseEntity<List<RoomBookingResponseDTO>> getRoomBookingsByGuestEmail(@PathVariable String guestEmail) {
        List<RoomBookingResponseDTO> roomBookings = roomBookingService.getRoomBookingsByGuestEmail(guestEmail);
        return ResponseEntity.ok(roomBookings);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoomBookingResponseDTO>> getRoomBookingsByUserId(@PathVariable Long userId) {
        List<RoomBookingResponseDTO> roomBookings = roomBookingService.getRoomBookingsByUserId(userId);
        return ResponseEntity.ok(roomBookings);
    }

    // Payment related endpoints
    @GetMapping("/payments/status/{paymentStatus}")
    public ResponseEntity<List<RoomBookingResponseDTO>> getRoomBookingsByPaymentStatus(@PathVariable RoomBooking.PaymentStatus paymentStatus) {
        List<RoomBookingResponseDTO> roomBookings = roomBookingService.getRoomBookingsByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(roomBookings);
    }

    @GetMapping("/payments/successful")
    public ResponseEntity<List<RoomBookingResponseDTO>> getSuccessfulPayments() {
        List<RoomBookingResponseDTO> roomBookings = roomBookingService.getSuccessfulPayments();
        return ResponseEntity.ok(roomBookings);
    }

    @GetMapping("/payments/date-range")
    public ResponseEntity<List<RoomBookingResponseDTO>> getPaymentsByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<RoomBookingResponseDTO> roomBookings = roomBookingService.getPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(roomBookings);
    }

    @GetMapping("/revenue/total")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        BigDecimal totalRevenue = roomBookingService.getTotalRevenue();
        return ResponseEntity.ok(totalRevenue);
    }

    @GetMapping("/count/payment-status/{paymentStatus}")
    public ResponseEntity<Long> countBookingsByPaymentStatus(@PathVariable RoomBooking.PaymentStatus paymentStatus) {
        long count = roomBookingService.countBookingsByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(count);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RoomBookingResponseDTO> updateRoomBookingStatus(
            @PathVariable Long id,
            @RequestParam RoomBooking.RoomBookingStatus status) {
        RoomBookingResponseDTO updatedRoomBooking = roomBookingService.updateRoomBookingStatus(id, status);
        return ResponseEntity.ok(updatedRoomBooking);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<String> cancelRoomBooking(@PathVariable Long id) {
        boolean response = roomBookingService.cancelRoomBooking(id);
        if (response) {
            return ResponseEntity.ok("Room booking cancelled successfully");
        }
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/refund")
    public ResponseEntity<RoomBookingResponseDTO> refundPayment(
            @PathVariable Long id,
            @RequestParam String refundReason) {
        RoomBookingResponseDTO refundedBooking = roomBookingService.refundPayment(id, refundReason);
        return ResponseEntity.ok(refundedBooking);
    }

    // Stripe webhook endpoints
    @PostMapping("/webhook/payment-success")
    public ResponseEntity<RoomBookingResponseDTO> handlePaymentSuccess(
            @RequestParam String sessionId,
            @RequestParam String paymentIntentId) {
        RoomBookingResponseDTO updatedBooking = stripeService.handlePaymentSuccess(sessionId, paymentIntentId);
        return ResponseEntity.ok(updatedBooking);
    }

    @PostMapping("/webhook/payment-failure")
    public ResponseEntity<RoomBookingResponseDTO> handlePaymentFailure(
            @RequestParam String sessionId,
            @RequestParam String failureReason) {
        RoomBookingResponseDTO updatedBooking = stripeService.handlePaymentFailure(sessionId, failureReason);
        return ResponseEntity.ok(updatedBooking);
    }
}