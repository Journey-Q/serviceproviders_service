package com.example.serviceproviders_service.entity.serviceProvider.TravelAgency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long serviceProviderId;

    @Column(nullable = false, length = 200)
    private String name;

    private String image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false, unique = true, length = 20)
    private String licensePlate;

    @Column(nullable = false)
    private Boolean ac = true;

    @Column(nullable = false)
    private Integer numberOfSeats;

    @Column(nullable = false)
    private BigDecimal pricePerKmWithAC;

    @Column(nullable = false)
    private BigDecimal pricePerKmWithoutAC;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Transmission transmission;

    @ElementCollection
    @CollectionTable(name = "vehicle_features", joinColumns = @JoinColumn(name = "vehicle_id"))
    @Column(name = "feature")
    private List<String> features;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status = VehicleStatus.AVAILABLE;

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

    public enum VehicleType {
        VAN,
        CAR,
        BUS,
        SUV,
        MOTORCYCLE
    }

    public enum FuelType {
        PETROL,
        DIESEL,
        ELECTRIC,
        HYBRID
    }

    public enum Transmission {
        AUTOMATIC,
        MANUAL
    }

    public enum VehicleStatus {
        AVAILABLE,
        MAINTENANCE,
        UNAVAILABLE
    }

    public Vehicle() {}
}