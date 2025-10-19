package com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.VehicleBooking;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class VehicleBookingResponseDTO {

    private Long id;
    private Long vehicleId;
    private Long serviceProviderId;
    private Long userId;

    // Customer Information
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // Booking Details
    private LocalDate startDate;
    private LocalDate endDate;
    private String pickupLocation;
    private String dropoffLocation;
    private Integer estimatedKilometers;
    private Boolean withAC;
    private BigDecimal pricePerKm;
    private BigDecimal estimatedTotalAmount;

    // Status and Reasons
    private VehicleBooking.BookingStatus status;
    private String specialRequests;
    private String cancellationReason;
    private String rejectionReason;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime completedAt;

    public VehicleBookingResponseDTO() {}

    public VehicleBookingResponseDTO(VehicleBooking booking) {
        this.id = booking.getId();
        this.vehicleId = booking.getVehicleId();
        this.serviceProviderId = booking.getServiceProviderId();
        this.userId = booking.getUserId();

        this.customerName = booking.getCustomerName();
        this.customerEmail = booking.getCustomerEmail();
        this.customerPhone = booking.getCustomerPhone();

        this.startDate = booking.getStartDate();
        this.endDate = booking.getEndDate();
        this.pickupLocation = booking.getPickupLocation();
        this.dropoffLocation = booking.getDropoffLocation();
        this.estimatedKilometers = booking.getEstimatedKilometers();
        this.withAC = booking.getWithAC();
        this.pricePerKm = booking.getPricePerKm();
        this.estimatedTotalAmount = booking.getEstimatedTotalAmount();

        this.status = booking.getStatus();
        this.specialRequests = booking.getSpecialRequests();
        this.cancellationReason = booking.getCancellationReason();
        this.rejectionReason = booking.getRejectionReason();

        this.createdAt = booking.getCreatedAt();
        this.updatedAt = booking.getUpdatedAt();
        this.approvedAt = booking.getApprovedAt();
        this.rejectedAt = booking.getRejectedAt();
        this.cancelledAt = booking.getCancelledAt();
        this.completedAt = booking.getCompletedAt();
    }
}
