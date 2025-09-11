package com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.Vehicle;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateVehicleDTO {
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

    public CreateVehicleDTO() {}
}