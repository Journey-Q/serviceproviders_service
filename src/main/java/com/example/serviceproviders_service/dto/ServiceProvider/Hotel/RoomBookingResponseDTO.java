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

    // Customer Information
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // Booking Details
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private Integer numberOfNights;
    private BigDecimal pricePerNight;
    private BigDecimal totalAmount;

    // Card Details (masked for security)
    private String cardHolderName;
    private String maskedCardNumber; // Only last 4 digits shown
    private String expiryDate;

    // Booking Status
    private String status;
    private String specialRequests;
    private String cancellationReason;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;

    public RoomBookingResponseDTO() {}

    // Constructor to convert entity to DTO
    public RoomBookingResponseDTO(RoomBooking booking) {
        this.id = booking.getId();
        this.roomId = booking.getRoomId();
        this.serviceProviderId = booking.getServiceProviderId();
        this.userId = booking.getUserId();
        this.customerName = booking.getCustomerName();
        this.customerEmail = booking.getCustomerEmail();
        this.customerPhone = booking.getCustomerPhone();
        this.checkInDate = booking.getCheckInDate();
        this.checkOutDate = booking.getCheckOutDate();
        this.numberOfGuests = booking.getNumberOfGuests();
        this.numberOfNights = booking.getNumberOfNights();
        this.pricePerNight = booking.getPricePerNight();
        this.totalAmount = booking.getTotalAmount();
        this.cardHolderName = booking.getCardHolderName();
        this.maskedCardNumber = maskCardNumber(booking.getCardNumber());
        this.expiryDate = booking.getExpiryDate();
        this.status = booking.getStatus().name();
        this.specialRequests = booking.getSpecialRequests();
        this.cancellationReason = booking.getCancellationReason();
        this.createdAt = booking.getCreatedAt();
        this.confirmedAt = booking.getConfirmedAt();
        this.cancelledAt = booking.getCancelledAt();
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
