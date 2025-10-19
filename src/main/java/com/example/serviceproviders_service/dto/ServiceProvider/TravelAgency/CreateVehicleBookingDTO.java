package com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateVehicleBookingDTO {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "User ID is required")
    private Long userId;

    // Customer Information
    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String customerEmail;

    @NotBlank(message = "Customer phone is required")
    @Pattern(regexp = "^[0-9+\\-\\s()]{8,20}$", message = "Invalid phone number format")
    private String customerPhone;

    // Booking Details
    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @NotBlank(message = "Pickup location is required")
    @Size(min = 5, max = 200, message = "Pickup location must be between 5 and 200 characters")
    private String pickupLocation;

    @NotBlank(message = "Dropoff location is required")
    @Size(min = 5, max = 200, message = "Dropoff location must be between 5 and 200 characters")
    private String dropoffLocation;

    @NotNull(message = "Estimated kilometers is required")
    @Min(value = 1, message = "Estimated kilometers must be at least 1")
    @Max(value = 10000, message = "Estimated kilometers cannot exceed 10000")
    private Integer estimatedKilometers;

    @NotNull(message = "AC preference is required")
    private Boolean withAC;

    @Size(max = 500, message = "Special requests must not exceed 500 characters")
    private String specialRequests;

    public CreateVehicleBookingDTO() {}
}
