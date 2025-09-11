package com.example.serviceproviders_service.entity.serviceProvider;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bank_details")
@Getter
@Setter
public class BankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long serviceProviderId;

    @Column(nullable = false, length = 200)
    private String accountHolderName;

    @Column(nullable = false, length = 20)
    private String accountNumber;

    @Column(nullable = false, length = 11)
    private String ifscCode;

    @Column(nullable = false, length = 100)
    private String bankName;

    @Column(nullable = false, length = 100)
    private String branchName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType = AccountType.SAVINGS;

    @Column(nullable = false, length = 15)
    private String mobileNumber;

    @Column(nullable = false, length = 100)
    private String emailId;

    @Column(nullable = false)
    private Boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(name = "last_verified")
    private LocalDateTime lastVerified;

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

    public enum AccountType {
        SAVINGS,
        CURRENT
    }

    public enum VerificationStatus {
        PENDING,
        VERIFIED,
        FAILED
    }

    public BankDetails() {}
}