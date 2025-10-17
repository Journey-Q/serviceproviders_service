package com.example.serviceproviders_service.entity.serviceProvider.TravelAgency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehiclebookings")
@Getter
@Setter
public class VehicleBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long vehicleId;

    @Column(nullable = false)
    private Long serviceProviderId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String customerName;

    @Column(nullable = false, length = 150)
    private String customerEmail;

    @Column(nullable = false, length = 20)
    private String customerPhone;

    @Column(columnDefinition = "TEXT")
    private String specialRequests;

    @Column(nullable = false)
    private LocalDate pickupDate;

    @Column(nullable = false)
    private LocalDate returnDate;

    @Column(nullable = false, length = 200)
    private String pickupLocation;

    @Column(nullable = false, length = 200)
    private String dropoffLocation;

    @Column(nullable = false)
    private Integer numberOfDays;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedDistance; // in kilometers

    @Column(nullable = false)
    private Boolean withAC = true;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerKm;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal serviceCharge;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal taxes;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleBookingStatus status;

    @Column(unique = true, length = 50)
    private String bookingReference;

    // Payment related fields
    @Column(length = 100)
    private String stripeSessionId;

    @Column(length = 100)
    private String stripePaymentIntentId;

    @Column(length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    private String paymentFailureReason;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (bookingReference == null) {
            bookingReference = "VB" + System.currentTimeMillis();
        }
        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.PENDING;
        }
        if (paymentMethod == null) {
            paymentMethod = PaymentMethod.STRIPE_CARD;
        }
        if (currency == null) {
            currency = "USD";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum VehicleBookingStatus {
        PENDING,
        CONFIRMED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    public enum PaymentStatus {
        PENDING,
        PROCESSING,
        SUCCEEDED,
        FAILED,
        CANCELLED,
        REFUNDED
    }

    public enum PaymentMethod {
        STRIPE_CARD,
        STRIPE_WALLET,
        BANK_TRANSFER,
        CASH
    }

    public VehicleBooking() {}
}
