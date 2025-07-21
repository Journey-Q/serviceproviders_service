// DTO
package com.example.serviceproviders_service.dto.ServiceProvider;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAgencyProfileDTO {
    private Long serviceProviderId;
    private String agencyName;
    private String profilePhoto;
    private String description;
    private AgencyInfoDTO agencyInfo;
    private ContactInfoDTO contactInfo;

    @Getter
    @Setter
    public static class AgencyInfoDTO {
        private String establishedYear;
        private String fleetSize;
    }

    @Getter
    @Setter
    public static class ContactInfoDTO {
        private String phone;
        private String address;
        private String email;
    }
}