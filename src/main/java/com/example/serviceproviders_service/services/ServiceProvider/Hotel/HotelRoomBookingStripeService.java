package com.example.serviceproviders_service.services.ServiceProvider.Hotel;

import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.RoomBookingPaymentDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.RoomBookingPaymentResponseDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.RoomBookingResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.RoomBooking;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class HotelRoomBookingStripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    @Autowired
    private RoomBookingService roomBookingService;

    @Transactional
    public RoomBookingPaymentResponseDTO createRoomBookingWithPayment(RoomBookingPaymentDTO dto) {
        Stripe.apiKey = secretKey;

        // Create room booking first
        RoomBookingResponseDTO roomBooking = roomBookingService.createRoomBooking(dto.getRoomBookingDetails());

        try {
            // Calculate amount in cents (Stripe requires amount in smallest currency unit)
            long amountInCents = roomBooking.getTotalAmount().multiply(new BigDecimal("100")).longValue();

            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName("Hotel Room Booking - " + roomBooking.getBookingReference())
                            .setDescription("Room booking from " + roomBooking.getCheckInDate() + " to " + roomBooking.getCheckOutDate())
                            .build();

            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(roomBooking.getCurrency() != null ?
                                    roomBooking.getCurrency().toLowerCase() : "usd")
                            .setUnitAmount(amountInCents)
                            .setProductData(productData)
                            .build();

            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(priceData)
                            .build();

            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(dto.getSuccessUrl() != null ? dto.getSuccessUrl() : "http://localhost:8080/roombooking/success")
                            .setCancelUrl(dto.getCancelUrl() != null ? dto.getCancelUrl() : "http://localhost:8080/roombooking/cancel")
                            .addLineItem(lineItem)
                            .putMetadata("roomBookingId", roomBooking.getId().toString())
                            .putMetadata("bookingReference", roomBooking.getBookingReference())
                            .build();

            Session session = Session.create(params);

            // Update room booking with Stripe session details
            roomBookingService.updatePaymentDetails(
                    roomBooking.getId(),
                    session.getId(),
                    null, // Payment intent ID will be set later when payment completes
                    RoomBooking.PaymentStatus.PROCESSING
            );

            return RoomBookingPaymentResponseDTO.builder()
                    .status("SUCCESS")
                    .message("Room booking created successfully. Please complete payment.")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .roomBookingId(roomBooking.getId())
                    .bookingReference(roomBooking.getBookingReference())
                    .build();

        } catch (StripeException ex) {
            throw new RuntimeException("Failed to create payment session: " + ex.getMessage());
        }
    }

    @Transactional
    public RoomBookingResponseDTO handlePaymentSuccess(String sessionId, String paymentIntentId) {
        RoomBookingResponseDTO booking = roomBookingService.getRoomBookingByStripeSessionId(sessionId);
        return roomBookingService.updatePaymentDetails(
                booking.getId(),
                sessionId,
                paymentIntentId,
                RoomBooking.PaymentStatus.SUCCEEDED
        );
    }

    @Transactional
    public RoomBookingResponseDTO handlePaymentFailure(String sessionId, String failureReason) {
        return roomBookingService.updatePaymentFailure(sessionId, failureReason);
    }
}