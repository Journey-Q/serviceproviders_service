package com.example.serviceproviders_service.dto.ServiceProvider.Hotel;

import com.example.serviceproviders_service.entity.serviceProvider.Hotel.RoomBooking;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class RoomBookingResponseDTO {
    private Long id;
    private Long roomId;
    private Long serviceProviderId;
    private Long userId;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private String specialRequests;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private Integer numberOfNights;
    private BigDecimal subtotal;
    private BigDecimal serviceCharge;
    private BigDecimal taxes;
    private BigDecimal totalAmount;
    private RoomBooking.RoomBookingStatus status;
    private String bookingReference;

    // Payment related fields
    private String stripeSessionId;
    private String stripePaymentIntentId;
    private String currency;
    private RoomBooking.PaymentStatus paymentStatus;
    private RoomBooking.PaymentMethod paymentMethod;
    private String paymentFailureReason;
    private LocalDateTime paidAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RoomBookingResponseDTO() {}

    public static RoomBookingResponseDTO fromEntity(RoomBooking roomBooking) {
        RoomBookingResponseDTO dto = new RoomBookingResponseDTO();
        dto.setId(roomBooking.getId());
        dto.setRoomId(roomBooking.getRoomId());
        dto.setServiceProviderId(roomBooking.getServiceProviderId());
        dto.setUserId(roomBooking.getUserId());
        dto.setGuestName(roomBooking.getGuestName());
        dto.setGuestEmail(roomBooking.getGuestEmail());
        dto.setGuestPhone(roomBooking.getGuestPhone());
        dto.setSpecialRequests(roomBooking.getSpecialRequests());
        dto.setCheckInDate(roomBooking.getCheckInDate());
        dto.setCheckOutDate(roomBooking.getCheckOutDate());
        dto.setNumberOfGuests(roomBooking.getNumberOfGuests());
        dto.setNumberOfNights(roomBooking.getNumberOfNights());
        dto.setSubtotal(roomBooking.getSubtotal());
        dto.setServiceCharge(roomBooking.getServiceCharge());
        dto.setTaxes(roomBooking.getTaxes());
        dto.setTotalAmount(roomBooking.getTotalAmount());
        dto.setStatus(roomBooking.getStatus());
        dto.setBookingReference(roomBooking.getBookingReference());

        // Payment fields
        dto.setStripeSessionId(roomBooking.getStripeSessionId());
        dto.setStripePaymentIntentId(roomBooking.getStripePaymentIntentId());
        dto.setCurrency(roomBooking.getCurrency());
        dto.setPaymentStatus(roomBooking.getPaymentStatus());
        dto.setPaymentMethod(roomBooking.getPaymentMethod());
        dto.setPaymentFailureReason(roomBooking.getPaymentFailureReason());
        dto.setPaidAt(roomBooking.getPaidAt());

        dto.setCreatedAt(roomBooking.getCreatedAt());
        dto.setUpdatedAt(roomBooking.getUpdatedAt());
        return dto;
    }
}