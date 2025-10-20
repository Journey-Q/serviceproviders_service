package com.example.serviceproviders_service.entity.serviceProvider;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotions")
@Getter
@Setter
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long serviceProviderId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private String image;

    @Column(nullable = false)
    private Integer discount; // Percentage discount

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column(nullable = false)
    private LocalDate validTo;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionStatus status = PromotionStatus.REQUESTED;

    // Payment related fields
    @Column(name = "is_paid", nullable = false , columnDefinition = "boolean default false")
    private Boolean isPaid = false;

    @Column(name = "payment_amount", precision = 10, scale = 2)
    private BigDecimal paymentAmount;

    @Column(name = "payment_plan")
    private String paymentPlan; // basic, premium, featured

    @Column(name = "advertisement_duration")
    private String advertisementDuration; // e.g., "7 days", "30 days"

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "payment_reference_number", unique = true)
    private String paymentReferenceNumber;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    // Card details (last 4 digits only for reference)
    @Column(name = "card_last_four_digits")
    private String cardLastFourDigits;

    @Column(name = "cardholder_name")
    private String cardholderName;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum PromotionStatus {
        REQUESTED,   // Submitted, waiting for admin approval
        APPROVED,    // Approved by admin, ready for payment
        ADVERTISED,  // Paid and currently being advertised
        REJECTED     // Rejected by admin
    }

    public Promotion() {}
}