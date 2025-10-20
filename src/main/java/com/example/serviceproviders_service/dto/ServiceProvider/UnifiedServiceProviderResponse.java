package com.example.serviceproviders_service.dto.ServiceProvider;

import com.example.serviceproviders_service.entity.serviceProvider.Hotel.HotelProfile;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.Room;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProviderType;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.Tour;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourGuideProfile;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.AgencyProfile;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Unified DTO that represents any service provider with their complete details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifiedServiceProviderResponse {
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

    // Resources - populated based on serviceType
    private List<RoomData> rooms;
    private List<TourData> tours;
    private List<VehicleData> vehicles;

    // Revenue data
    private RevenueData revenue;

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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomData {
        private Long id;
        private Long serviceProviderId;
        private String name;
        private String price;
        private Integer maxOccupancy;
        private Integer area;
        private String beds;
        private Integer bathrooms;
        private List<String> amenities;
        private List<String> images;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static RoomData from(Room room) {
            if (room == null) return null;
            return RoomData.builder()
                    .id(room.getId())
                    .serviceProviderId(room.getServiceProviderId())
                    .name(room.getName())
                    .price(room.getPrice() != null ? room.getPrice().toString() : null)
                    .maxOccupancy(room.getMaxOccupancy())
                    .area(room.getArea())
                    .beds(room.getBeds())
                    .bathrooms(room.getBathrooms())
                    .amenities(room.getAmenities())
                    .images(room.getImages())
                    .status(room.getStatus() != null ? room.getStatus().toString() : null)
                    .createdAt(room.getCreatedAt())
                    .updatedAt(room.getUpdatedAt())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TourData {
        private Long id;
        private Long serviceProviderId;
        private String name;
        private String image;
        private String originalPrice;
        private Integer discount;
        private String finalPrice;
        private String pricePerPerson;
        private String duration;
        private List<String> places;
        private List<String> highlights;
        private List<String> included;
        private List<String> importantNotes;
        private String status;
        private String rating;
        private Integer maxPeople;
        private Integer minPeople;
        private String aboutTour;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static TourData from(Tour tour) {
            if (tour == null) return null;
            return TourData.builder()
                    .id(tour.getId())
                    .serviceProviderId(tour.getServiceProviderId())
                    .name(tour.getName())
                    .image(tour.getImage())
                    .originalPrice(tour.getOriginalPrice() != null ? tour.getOriginalPrice().toString() : null)
                    .discount(tour.getDiscount())
                    .finalPrice(tour.getFinalPrice() != null ? tour.getFinalPrice().toString() : null)
                    .pricePerPerson(tour.getPricePerPerson() != null ? tour.getPricePerPerson().toString() : null)
                    .duration(tour.getDuration())
                    .places(tour.getPlaces())
                    .highlights(tour.getHighlights())
                    .included(tour.getIncluded())
                    .importantNotes(tour.getImportantNotes())
                    .status(tour.getStatus() != null ? tour.getStatus().toString() : null)
                    .rating(tour.getRating() != null ? tour.getRating().toString() : null)
                    .maxPeople(tour.getMaxPeople())
                    .minPeople(tour.getMinPeople())
                    .aboutTour(tour.getAboutTour())
                    .createdAt(tour.getCreatedAt())
                    .updatedAt(tour.getUpdatedAt())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleData {
        private Long id;
        private Long serviceProviderId;
        private String name;
        private String image;
        private String type;
        private String brand;
        private String model;
        private Integer year;
        private String licensePlate;
        private Boolean ac;
        private Integer numberOfSeats;
        private String pricePerKmWithAC;
        private String pricePerKmWithoutAC;
        private String fuelType;
        private String transmission;
        private List<String> features;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static VehicleData from(Vehicle vehicle) {
            if (vehicle == null) return null;
            return VehicleData.builder()
                    .id(vehicle.getId())
                    .serviceProviderId(vehicle.getServiceProviderId())
                    .name(vehicle.getName())
                    .image(vehicle.getImage())
                    .type(vehicle.getType() != null ? vehicle.getType().toString() : null)
                    .brand(vehicle.getBrand())
                    .model(vehicle.getModel())
                    .year(vehicle.getYear())
                    .licensePlate(vehicle.getLicensePlate())
                    .ac(vehicle.getAc())
                    .numberOfSeats(vehicle.getNumberOfSeats())
                    .pricePerKmWithAC(vehicle.getPricePerKmWithAC() != null ? vehicle.getPricePerKmWithAC().toString() : null)
                    .pricePerKmWithoutAC(vehicle.getPricePerKmWithoutAC() != null ? vehicle.getPricePerKmWithoutAC().toString() : null)
                    .fuelType(vehicle.getFuelType() != null ? vehicle.getFuelType().toString() : null)
                    .transmission(vehicle.getTransmission() != null ? vehicle.getTransmission().toString() : null)
                    .features(vehicle.getFeatures())
                    .status(vehicle.getStatus() != null ? vehicle.getStatus().toString() : null)
                    .createdAt(vehicle.getCreatedAt())
                    .updatedAt(vehicle.getUpdatedAt())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueData {
        private String totalRevenue;
        private String completedRevenue;
        private String pendingRevenue;
        private Integer totalBookings;
        private Integer completedBookings;
        private Integer pendingBookings;
        private Integer cancelledBookings;
        private String averageBookingValue;
    }
}