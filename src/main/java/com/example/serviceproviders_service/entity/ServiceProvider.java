package com.example.serviceproviders_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_providers")
@Getter
@Setter
public class ServiceProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceProviderType serviceType;

    @Column(name = "business_registration_number", unique = true, nullable = false)
    private String businessRegistrationNumber;

    @Column(nullable = false)
    private String address;

    @Column(name = "contact_no", nullable = false)
    private String contactNo;

    @Column(name = "is_approved")
    private Boolean isApproved = true;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_expiration")
    private LocalDateTime verificationExpiration;

    // Default constructor
    public ServiceProvider() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.isApproved = false;
    }

    // Constructor with basic parameters
    public ServiceProvider(String username, String email, String password, ServiceProviderType serviceType) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.serviceType = serviceType;
    }

    // Constructor with all parameters
    public ServiceProvider(String username, String email, String password, ServiceProviderType serviceType,
                           String businessRegistrationNumber, String address, String contactNo) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.serviceType = serviceType;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.address = address;
        this.contactNo = contactNo;
    }

    @Override
    public String toString() {
        return "ServiceProvider{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", serviceType=" + serviceType +
                ", businessRegistrationNumber='" + businessRegistrationNumber + '\'' +
                ", address='" + address + '\'' +
                ", contactNo='" + contactNo + '\'' +
                ", isApproved=" + isApproved +
                ", createdAt=" + createdAt +
                ", isActive=" + isActive +
                '}';
    }
}