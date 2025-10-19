package com.example.serviceproviders_service.entity.serviceProvider.TourGuide;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tour_bookings")
@Getter
@Setter
public class TourBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tourId;

    @Column(nullable = false)
    private Long serviceProviderId; // Tour Guide ID

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
    private LocalDate tourDate;

    @Column(nullable = false)
    private Integer numberOfPeople;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerPerson;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // Card Payment Details (optional - for future payment integration)
    @Column(length = 100)
    private String cardHolderName;

    @Column(length = 19)
    private String cardNumber; // Note: In production, this should be encrypted

    @Column(length = 7)
    private String expiryDate; // Format: MM/YYYY

    @Column(length = 4)
    private String cvv; // Note: In production, this should NOT be stored

    @Column(length = 200)
    private String billingAddress;

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
        PENDING_APPROVAL,  // Waiting for tour guide to approve
        APPROVED,          // Tour guide approved the booking
        REJECTED,          // Tour guide rejected the booking
        CANCELLED,         // Customer cancelled the booking
        COMPLETED          // Tour completed
    }

    public TourBooking() {}
}
