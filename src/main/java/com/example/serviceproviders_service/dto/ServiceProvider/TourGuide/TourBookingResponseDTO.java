package com.example.serviceproviders_service.dto.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourBooking;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class TourBookingResponseDTO {
    private Long id;
    private Long tourId;
    private Long serviceProviderId;
    private Long userId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String specialRequests;
    private LocalDate tourDate;
    private Integer numberOfPeople;
    private BigDecimal pricePerPerson;
    private BigDecimal subtotal;
    private BigDecimal serviceCharge;
    private BigDecimal taxes;
    private BigDecimal totalAmount;
    private String status;
    private String bookingReference;
    private String stripeSessionId;
    private String stripePaymentIntentId;
    private String currency;
    private String paymentStatus;
    private String paymentMethod;
    private String paymentFailureReason;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TourBookingResponseDTO() {}

    public static TourBookingResponseDTO fromEntity(TourBooking booking) {
        TourBookingResponseDTO dto = new TourBookingResponseDTO();
        dto.setId(booking.getId());
        dto.setTourId(booking.getTourId());
        dto.setServiceProviderId(booking.getServiceProviderId());
        dto.setUserId(booking.getUserId());
        dto.setCustomerName(booking.getCustomerName());
        dto.setCustomerEmail(booking.getCustomerEmail());
        dto.setCustomerPhone(booking.getCustomerPhone());
        dto.setSpecialRequests(booking.getSpecialRequests());
        dto.setTourDate(booking.getTourDate());
        dto.setNumberOfPeople(booking.getNumberOfPeople());
        dto.setPricePerPerson(booking.getPricePerPerson());
        dto.setSubtotal(booking.getSubtotal());
        dto.setServiceCharge(booking.getServiceCharge());
        dto.setTaxes(booking.getTaxes());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setStatus(booking.getStatus() != null ? booking.getStatus().name() : null);
        dto.setBookingReference(booking.getBookingReference());
        dto.setStripeSessionId(booking.getStripeSessionId());
        dto.setStripePaymentIntentId(booking.getStripePaymentIntentId());
        dto.setCurrency(booking.getCurrency());
        dto.setPaymentStatus(booking.getPaymentStatus() != null ? booking.getPaymentStatus().name() : null);
        dto.setPaymentMethod(booking.getPaymentMethod() != null ? booking.getPaymentMethod().name() : null);
        dto.setPaymentFailureReason(booking.getPaymentFailureReason());
        dto.setPaidAt(booking.getPaidAt());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        return dto;
    }
}