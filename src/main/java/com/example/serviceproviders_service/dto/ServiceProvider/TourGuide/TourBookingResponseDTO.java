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

    // Customer Information
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // Booking Details
    private LocalDate tourDate;
    private Integer numberOfPeople;
    private BigDecimal pricePerPerson;
    private BigDecimal totalAmount;

    // Booking Status
    private String status;
    private String specialRequests;
    private String cancellationReason;
    private String rejectionReason;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime completedAt;

    public TourBookingResponseDTO() {}

    // Constructor to convert entity to DTO
    public TourBookingResponseDTO(TourBooking booking) {
        this.id = booking.getId();
        this.tourId = booking.getTourId();
        this.serviceProviderId = booking.getServiceProviderId();
        this.userId = booking.getUserId();
        this.customerName = booking.getCustomerName();
        this.customerEmail = booking.getCustomerEmail();
        this.customerPhone = booking.getCustomerPhone();
        this.tourDate = booking.getTourDate();
        this.numberOfPeople = booking.getNumberOfPeople();
        this.pricePerPerson = booking.getPricePerPerson();
        this.totalAmount = booking.getTotalAmount();
        this.status = booking.getStatus().name();
        this.specialRequests = booking.getSpecialRequests();
        this.cancellationReason = booking.getCancellationReason();
        this.rejectionReason = booking.getRejectionReason();
        this.createdAt = booking.getCreatedAt();
        this.approvedAt = booking.getApprovedAt();
        this.rejectedAt = booking.getRejectedAt();
        this.cancelledAt = booking.getCancelledAt();
        this.completedAt = booking.getCompletedAt();
    }
}
