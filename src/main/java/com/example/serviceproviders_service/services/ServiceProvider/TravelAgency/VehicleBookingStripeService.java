package com.example.serviceproviders_service.services.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.VehicleBookingPaymentDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.VehicleBookingPaymentResponseDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.VehicleBookingResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.VehicleBooking;
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
public class VehicleBookingStripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    @Autowired
    private VehicleBookingService vehicleBookingService;

    @Transactional
    public VehicleBookingPaymentResponseDTO createVehicleBookingWithPayment(VehicleBookingPaymentDTO dto) {
        Stripe.apiKey = secretKey;

        // Create vehicle booking first
        VehicleBookingResponseDTO vehicleBooking = vehicleBookingService.createVehicleBooking(dto.getVehicleBookingDetails());

        try {
            // Calculate amount in cents (Stripe requires amount in smallest currency unit)
            long amountInCents = vehicleBooking.getTotalAmount().multiply(new BigDecimal("100")).longValue();

            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName("Vehicle Booking - " + vehicleBooking.getBookingReference())
                            .setDescription("Vehicle booking from " + vehicleBooking.getPickupDate() +
                                          " to " + vehicleBooking.getReturnDate() +
                                          " (" + vehicleBooking.getEstimatedDistance() + " km)")
                            .build();

            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(vehicleBooking.getCurrency() != null ?
                                    vehicleBooking.getCurrency().toLowerCase() : "usd")
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
                            .setSuccessUrl(dto.getSuccessUrl() != null ? dto.getSuccessUrl() : "http://localhost:8080/vehiclebooking/success")
                            .setCancelUrl(dto.getCancelUrl() != null ? dto.getCancelUrl() : "http://localhost:8080/vehiclebooking/cancel")
                            .addLineItem(lineItem)
                            .putMetadata("vehicleBookingId", vehicleBooking.getId().toString())
                            .putMetadata("bookingReference", vehicleBooking.getBookingReference())
                            .build();

            Session session = Session.create(params);

            // Update vehicle booking with Stripe session details
            vehicleBookingService.updatePaymentDetails(
                    vehicleBooking.getId(),
                    session.getId(),
                    null, // Payment intent ID will be set later when payment completes
                    VehicleBooking.PaymentStatus.PROCESSING
            );

            return VehicleBookingPaymentResponseDTO.builder()
                    .status("SUCCESS")
                    .message("Vehicle booking created successfully. Please complete payment.")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .vehicleBookingId(vehicleBooking.getId())
                    .bookingReference(vehicleBooking.getBookingReference())
                    .build();

        } catch (StripeException ex) {
            throw new RuntimeException("Failed to create payment session: " + ex.getMessage());
        }
    }

    @Transactional
    public VehicleBookingResponseDTO handlePaymentSuccess(String sessionId, String paymentIntentId) {
        VehicleBookingResponseDTO booking = vehicleBookingService.getVehicleBookingByStripeSessionId(sessionId);
        return vehicleBookingService.updatePaymentDetails(
                booking.getId(),
                sessionId,
                paymentIntentId,
                VehicleBooking.PaymentStatus.SUCCEEDED
        );
    }

    @Transactional
    public VehicleBookingResponseDTO handlePaymentFailure(String sessionId, String failureReason) {
        return vehicleBookingService.updatePaymentFailure(sessionId, failureReason);
    }
}
