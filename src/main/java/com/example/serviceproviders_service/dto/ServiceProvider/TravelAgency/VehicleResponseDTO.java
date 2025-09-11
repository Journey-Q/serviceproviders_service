package com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.Vehicle;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class VehicleResponseDTO {
    private Long id;
    private Long serviceProviderId;
    private String name;
    private String image;
    private Vehicle.VehicleType type;
    private String brand;
    private String model;
    private Integer year;
    private String licensePlate;
    private Boolean ac;
    private Integer numberOfSeats;
    private BigDecimal pricePerKmWithAC;
    private BigDecimal pricePerKmWithoutAC;
    private Vehicle.FuelType fuelType;
    private Vehicle.Transmission transmission;
    private List<String> features;
    private Vehicle.VehicleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VehicleResponseDTO() {}

    public static VehicleResponseDTO fromEntity(Vehicle vehicle) {
        VehicleResponseDTO dto = new VehicleResponseDTO();
        dto.setId(vehicle.getId());
        dto.setServiceProviderId(vehicle.getServiceProviderId());
        dto.setName(vehicle.getName());
        dto.setImage(vehicle.getImage());
        dto.setType(vehicle.getType());
        dto.setBrand(vehicle.getBrand());
        dto.setModel(vehicle.getModel());
        dto.setYear(vehicle.getYear());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setAc(vehicle.getAc());
        dto.setNumberOfSeats(vehicle.getNumberOfSeats());
        dto.setPricePerKmWithAC(vehicle.getPricePerKmWithAC());
        dto.setPricePerKmWithoutAC(vehicle.getPricePerKmWithoutAC());
        dto.setFuelType(vehicle.getFuelType());
        dto.setTransmission(vehicle.getTransmission());
        dto.setFeatures(vehicle.getFeatures());
        dto.setStatus(vehicle.getStatus());
        dto.setCreatedAt(vehicle.getCreatedAt());
        dto.setUpdatedAt(vehicle.getUpdatedAt());
        return dto;
    }
}

