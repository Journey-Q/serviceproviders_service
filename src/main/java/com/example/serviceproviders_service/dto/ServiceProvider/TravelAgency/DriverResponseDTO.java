package com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.Driver;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DriverResponseDTO {
    private Long id;
    private Long serviceProviderId;
    private String name;
    private String profilePhoto;
    private Integer experience;
    private List<String> languages;
    private String contactNumber;
    private String licenseNumber;
    private BigDecimal rating;
    private Driver.DriverStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DriverResponseDTO() {}

    public static DriverResponseDTO fromEntity(Driver driver) {
        DriverResponseDTO dto = new DriverResponseDTO();
        dto.setId(driver.getId());
        dto.setServiceProviderId(driver.getServiceProviderId());
        dto.setName(driver.getName());
        dto.setProfilePhoto(driver.getProfilePhoto());
        dto.setExperience(driver.getExperience());
        dto.setLanguages(driver.getLanguages());
        dto.setContactNumber(driver.getContactNumber());
        dto.setLicenseNumber(driver.getLicenseNumber());
        dto.setRating(driver.getRating());
        dto.setStatus(driver.getStatus());
        dto.setCreatedAt(driver.getCreatedAt());
        dto.setUpdatedAt(driver.getUpdatedAt());
        return dto;
    }
}
