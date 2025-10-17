package com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateVehicleBookingDTO {
    private Long vehicleId;
    private Long userId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String specialRequests;
    private LocalDate pickupDate;
    private LocalDate returnDate;
    private String pickupLocation;
    private String dropoffLocation;
    private BigDecimal estimatedDistance; // in kilometers
    private Boolean withAC;
    private BigDecimal pricePerKm;
    private String currency;

    public CreateVehicleBookingDTO() {}
}
