package com.example.serviceproviders_service.entity.serviceProvider.Hotel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_bookings")
@Getter
@Setter
public class RoomBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private Long serviceProviderId;

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
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(nullable = false)
    private Integer numberOfGuests;

    @Column(nullable = false)
    private Integer numberOfNights;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // Card Payment Details (stored for record-keeping)
    @Column(nullable = false, length = 100)
    private String cardHolderName;

    @Column(nullable = false, length = 19)
    private String cardNumber; // Note: In production, this should be encrypted

    @Column(nullable = false, length = 7)
    private String expiryDate; // Format: MM/YYYY

    @Column(nullable = false, length = 4)
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

    // Timestamps
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = BookingStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum BookingStatus {
        PENDING,      // Initial booking created
        CONFIRMED,    // Payment processed and booking confirmed
        CHECKED_IN,   // Customer has checked in
        CHECKED_OUT,  // Customer has checked out
        CANCELLED,    // Booking cancelled
        COMPLETED     // Booking completed (after checkout)
    }

    public RoomBooking() {}
}