package com.example.serviceproviders_service.dto.ServiceProvider;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class CreateHotelProfileDTO {
    private Long serviceProviderId;
    private String hotelName;
    private String location;
    private CoordinatesDTO coordinates;
    private String description;
    private String hotelPhoto;
    private List<String> amenities;
    private ContactInfoDTO contactInfo;

    // Getters and setters
    // ...
    @Getter
    @Setter
    public static class CoordinatesDTO {
        private Double lat;
        private Double lng;

        // Getters and setters
    }

    @Getter
    @Setter
    public static class ContactInfoDTO {
        private String phone;
        private String email;

        // Getters and setters
    }
}