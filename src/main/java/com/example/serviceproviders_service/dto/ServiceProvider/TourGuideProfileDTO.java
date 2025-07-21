package com.example.serviceproviders_service.dto.ServiceProvider;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourGuideProfileDTO {

    @NotNull(message = "Service provider ID is required")
    private Long serviceProviderId;

    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name must be less than 100 characters")
    private String companyName;

    @Size(max = 255, message = "Profile photo URL must be less than 255 characters")
    private String profilePhotoUrl;

    @Size(max = 2000, message = "Description must be less than 2000 characters")
    private String description;

    @Min(value = 1900, message = "Established year must be after 1900")
    private Integer establishedYear;

    @Min(value = 1, message = "Number of guides must be at least 1")
    private Integer numberOfGuides;

    @Valid
    private ContactInfoDTO contactInfo;

    @Getter
    @Setter
    public static class ContactInfoDTO {
        @Email(message = "Email should be valid")
        private String email;

        @Pattern(regexp = "^\\+?[0-9\\s-]{10,20}$", message = "Invalid phone number format")
        private String phone;

        @Size(max = 500, message = "Address must be less than 500 characters")
        private String address;
    }
}