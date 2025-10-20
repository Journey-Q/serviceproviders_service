package com.example.serviceproviders_service.entity.serviceProvider.Hotel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_history")
@Getter
@Setter
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentId; // Format: PMT-YYYY-MMDD-XXX

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private Long serviceProviderId;

    @Column(nullable = false)
    private Long roomId;

    // Guest Information
    @Column(nullable = false, length = 100)
    private String guestName;

    @Column(nullable = false, length = 100)
    private String guestEmail;

    @Column(nullable = false, length = 20)
    private String guestPhone;

    @Column(length = 200)
    private String guestAddress;

    // Room Information
    @Column(nullable = false, length = 100)
    private String roomName;

    // Payment Details
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // Amount in cents for Stripe compatibility

    @Column(nullable = false, length = 50)
    private String paymentMethod; // e.g., "Credit Card (VISA)", "Bank Transfer"

    @Column(length = 4)
    private String cardLastFour;

    @Column(length = 50)
    private String bankReference;

    @Column(length = 100)
    private String transactionId;

    @Column(length = 50)
    private String invoiceNumber; // Format: #INV-YYYY-MMDD-XXX

    // Booking Stay Details
    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(nullable = false)
    private Integer numberOfNights;

    // Payment Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // Timestamps
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
        if (status == null) {
            status = PaymentStatus.COMPLETED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }

    public PaymentHistory() {}
}