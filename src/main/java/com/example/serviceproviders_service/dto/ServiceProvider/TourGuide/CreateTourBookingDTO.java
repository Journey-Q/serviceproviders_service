package com.example.serviceproviders_service.dto.ServiceProvider.TourGuide;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateTourBookingDTO {

    @NotNull(message = "Tour ID is required")
    private Long tourId;

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
    @NotNull(message = "Tour date is required")
    @Future(message = "Tour date must be in the future")
    private LocalDate tourDate;

    @NotNull(message = "Number of people is required")
    @Min(value = 1, message = "At least 1 person is required")
    @Max(value = 50, message = "Maximum 50 people allowed")
    private Integer numberOfPeople;

    @Size(max = 500, message = "Special requests must not exceed 500 characters")
    private String specialRequests;

    public CreateTourBookingDTO() {}
}
