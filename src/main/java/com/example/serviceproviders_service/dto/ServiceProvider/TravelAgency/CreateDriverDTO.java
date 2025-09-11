package com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.Driver;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateDriverDTO {
    private Long serviceProviderId;
    private String name;
    private String profilePhoto;
    private Integer experience;
    private List<String> languages;
    private String contactNumber;
    private String licenseNumber;
    private BigDecimal rating;
    private Driver.DriverStatus status;

    public CreateDriverDTO() {}
}
