package com.example.serviceproviders_service.services.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.TourBookingPaymentDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.TourBookingPaymentResponseDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.TourBookingResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourBooking;
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
public class TourBookingStripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    @Autowired
    private TourBookingService tourBookingService;

    @Transactional
    public TourBookingPaymentResponseDTO createTourBookingWithPayment(TourBookingPaymentDTO dto) {
        Stripe.apiKey = secretKey;

        // Create tour booking first
        TourBookingResponseDTO tourBooking = tourBookingService.createTourBooking(dto.getTourBookingDetails());

        try {
            // Calculate amount in cents (Stripe requires amount in smallest currency unit)
            long amountInCents = tourBooking.getTotalAmount().multiply(new BigDecimal("100")).longValue();

            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName("Tour Booking - " + tourBooking.getBookingReference())
                            .setDescription("Tour booking for " + tourBooking.getNumberOfPeople() + " people on " + tourBooking.getTourDate())
                            .build();

            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(tourBooking.getCurrency() != null ?
                                    tourBooking.getCurrency().toLowerCase() : "usd")
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
                            .setSuccessUrl(dto.getSuccessUrl() != null ? dto.getSuccessUrl() : "http://localhost:8080/tourbooking/success")
                            .setCancelUrl(dto.getCancelUrl() != null ? dto.getCancelUrl() : "http://localhost:8080/tourbooking/cancel")
                            .addLineItem(lineItem)
                            .putMetadata("tourBookingId", tourBooking.getId().toString())
                            .putMetadata("bookingReference", tourBooking.getBookingReference())
                            .build();

            Session session = Session.create(params);

            // Update tour booking with Stripe session details
            tourBookingService.updatePaymentDetails(
                    tourBooking.getId(),
                    session.getId(),
                    null, // Payment intent ID will be set later when payment completes
                    TourBooking.PaymentStatus.PROCESSING
            );

            return TourBookingPaymentResponseDTO.builder()
                    .status("SUCCESS")
                    .message("Tour booking created successfully. Please complete payment.")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .tourBookingId(tourBooking.getId())
                    .bookingReference(tourBooking.getBookingReference())
                    .build();

        } catch (StripeException ex) {
            throw new RuntimeException("Failed to create payment session: " + ex.getMessage());
        }
    }

    @Transactional
    public TourBookingResponseDTO handlePaymentSuccess(String sessionId, String paymentIntentId) {
        TourBookingResponseDTO booking = tourBookingService.getTourBookingByStripeSessionId(sessionId);
        return tourBookingService.updatePaymentDetails(
                booking.getId(),
                sessionId,
                paymentIntentId,
                TourBooking.PaymentStatus.SUCCEEDED
        );
    }

    @Transactional
    public TourBookingResponseDTO handlePaymentFailure(String sessionId, String failureReason) {
        return tourBookingService.updatePaymentFailure(sessionId, failureReason);
    }
}
