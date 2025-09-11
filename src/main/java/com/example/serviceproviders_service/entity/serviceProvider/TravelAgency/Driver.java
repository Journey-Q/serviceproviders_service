package com.example.serviceproviders_service.entity.serviceProvider.TravelAgency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "drivers")
@Getter
@Setter
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long serviceProviderId;

    @Column(nullable = false, length = 200)
    private String name;

    private String profilePhoto;

    @Column(nullable = false)
    private Integer experience; // Years of experience

    @ElementCollection
    @CollectionTable(name = "driver_languages", joinColumns = @JoinColumn(name = "driver_id"))
    @Column(name = "language")
    private List<String> languages;

    @Column(nullable = false, length = 20)
    private String contactNumber;

    @Column(nullable = false, unique = true, length = 20)
    private String licenseNumber;

    @Column(nullable = false)
    private BigDecimal rating = BigDecimal.valueOf(0.0); // Default rating for new drivers

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status = DriverStatus.AVAILABLE;

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

    public enum DriverStatus {
        AVAILABLE,
        ON_LEAVE,
        TRAINING,
        UNAVAILABLE
    }

    public Driver() {}
}
