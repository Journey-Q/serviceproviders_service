package com.example.serviceproviders_service.dto.ServiceProvider;

import com.example.serviceproviders_service.entity.serviceProvider.Hotel.HotelProfile;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProviderType;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourGuideProfile;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.AgencyProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for service provider with profile details only (no resources or revenue)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileOnlyResponse {
    private Long id;
    private String username;
    private String email;
    private String businessRegistrationNumber;
    private String address;
    private String contactNo;
    private ServiceProviderType serviceType;
    private Boolean isApproved;
    private Boolean isActive;
    private Boolean isProfileCreated;
    private LocalDateTime createdAt;

    // Profile data - only one will be populated based on serviceType
    private HotelProfileData hotelProfile;
    private TourGuideProfileData tourGuideProfile;
    private AgencyProfileData agencyProfile;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HotelProfileData {
        private Long serviceProviderId;
        private String hotelName;
        private String location;
        private Double latitude;
        private Double longitude;
        private String description;
        private String hotelPhoto;
        private List<String> amenities;
        private String contactPhone;
        private String contactEmail;

        public static HotelProfileData from(HotelProfile profile) {
            if (profile == null) return null;
            return HotelProfileData.builder()
                    .serviceProviderId(profile.getServiceProviderId())
                    .hotelName(profile.getHotelName())
                    .location(profile.getLocation())
                    .latitude(profile.getCoordinates() != null ? profile.getCoordinates().getLat() : null)
                    .longitude(profile.getCoordinates() != null ? profile.getCoordinates().getLng() : null)
                    .description(profile.getDescription())
                    .hotelPhoto(profile.getHotelPhoto())
                    .amenities(profile.getAmenities())
                    .contactPhone(profile.getContactInfo() != null ? profile.getContactInfo().getPhone() : null)
                    .contactEmail(profile.getContactInfo() != null ? profile.getContactInfo().getEmail() : null)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TourGuideProfileData {
        private Long serviceProviderId;
        private String companyName;
        private String profilePhotoUrl;
        private String description;
        private Integer establishedYear;
        private Integer numberOfGuides;
        private String contactPhone;
        private String contactEmail;
        private String contactAddress;

        public static TourGuideProfileData from(TourGuideProfile profile) {
            if (profile == null) return null;
            return TourGuideProfileData.builder()
                    .serviceProviderId(profile.getServiceProviderId())
                    .companyName(profile.getCompanyName())
                    .profilePhotoUrl(profile.getProfilePhotoUrl())
                    .description(profile.getDescription())
                    .establishedYear(profile.getEstablishedYear())
                    .numberOfGuides(profile.getNumberOfGuides())
                    .contactPhone(profile.getContactInfo() != null ? profile.getContactInfo().getPhone() : null)
                    .contactEmail(profile.getContactInfo() != null ? profile.getContactInfo().getEmail() : null)
                    .contactAddress(profile.getContactInfo() != null ? profile.getContactInfo().getAddress() : null)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgencyProfileData {
        private Long serviceProviderId;
        private String agencyName;
        private String profilePhoto;
        private String description;
        private String establishedYear;
        private String fleetSize;
        private String contactPhone;
        private String contactEmail;
        private String contactAddress;

        public static AgencyProfileData from(AgencyProfile profile) {
            if (profile == null) return null;
            return AgencyProfileData.builder()
                    .serviceProviderId(profile.getServiceProviderId())
                    .agencyName(profile.getAgencyName())
                    .profilePhoto(profile.getProfilePhoto())
                    .description(profile.getDescription())
                    .establishedYear(profile.getAgencyInfo() != null ? profile.getAgencyInfo().getEstablishedYear() : null)
                    .fleetSize(profile.getAgencyInfo() != null ? profile.getAgencyInfo().getFleetSize() : null)
                    .contactPhone(profile.getContactInfo() != null ? profile.getContactInfo().getPhone() : null)
                    .contactEmail(profile.getContactInfo() != null ? profile.getContactInfo().getEmail() : null)
                    .contactAddress(profile.getContactInfo() != null ? profile.getContactInfo().getAddress() : null)
                    .build();
        }
    }
}