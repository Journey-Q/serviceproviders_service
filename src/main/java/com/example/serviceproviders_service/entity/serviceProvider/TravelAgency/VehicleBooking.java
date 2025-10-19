package com.example.serviceproviders_service.entity.serviceProvider.TravelAgency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_bookings")
@Getter
@Setter
public class VehicleBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long vehicleId;

    @Column(nullable = false)
    private Long serviceProviderId; // Travel Agency ID

    @Column(nullable = false)
    private Long userId; // User who made the booking

    // Customer Information
    @Column(nullable = false, length = 100)
    private String customerName;

    @Column(nullable = false, length = 100)
    private String customerEmail;

    @Column(nullable = false, length = 20)
    private String customerPhone;

    // Booking Details
    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, length = 200)
    private String pickupLocation;

    @Column(nullable = false, length = 200)
    private String dropoffLocation;

    @Column(nullable = false)
    private Integer estimatedKilometers;

    @Column(nullable = false)
    private Boolean withAC = true;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerKm;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedTotalAmount;

    // Booking Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(length = 500)
    private String specialRequests;

    @Column(length = 500)
    private String cancellationReason;

    @Column(length = 500)
    private String rejectionReason;

    // Timestamps
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = BookingStatus.PENDING_APPROVAL;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum BookingStatus {
        PENDING_APPROVAL,  // Waiting for travel agency to approve
        APPROVED,          // Travel agency approved the booking
        REJECTED,          // Travel agency rejected the booking
        CANCELLED,         // Customer cancelled the booking
        COMPLETED          // Trip completed
    }

    public VehicleBooking() {}
}
