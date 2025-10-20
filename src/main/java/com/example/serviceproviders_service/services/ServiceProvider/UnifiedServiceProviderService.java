package com.example.serviceproviders_service.services.ServiceProvider;

import com.example.serviceproviders_service.dto.ServiceProvider.AllProvidersResponse;
import com.example.serviceproviders_service.dto.ServiceProvider.ProfileOnlyResponse;
import com.example.serviceproviders_service.dto.ServiceProvider.UnifiedServiceProviderResponse;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.HotelProfile;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.Room;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.RoomBooking;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProvider;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProviderType;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.Tour;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourBooking;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourGuideProfile;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.AgencyProfile;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.Vehicle;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.VehicleBooking;
import com.example.serviceproviders_service.repository.ServiceProvider.HotelProfileRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.Hotel.RoomBookingRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.Hotel.RoomRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.ServiceProviderRepo;
import com.example.serviceproviders_service.repository.ServiceProvider.TourGuide.TourBookingRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TourGuideProfileRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TourGuide.TourRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.AgencyProfileRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TravelAgency.VehicleBookingRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TravelAgency.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service to provide unified access to all service providers with their complete details
 */
@Service
@RequiredArgsConstructor
public class UnifiedServiceProviderService {

    private final ServiceProviderRepo serviceProviderRepo;
    private final HotelProfileRepository hotelProfileRepository;
    private final TourGuideProfileRepository tourGuideProfileRepository;
    private final AgencyProfileRepository agencyProfileRepository;
    private final RoomRepository roomRepository;
    private final TourRepository tourRepository;
    private final VehicleRepository vehicleRepository;
    private final RoomBookingRepository roomBookingRepository;
    private final TourBookingRepository tourBookingRepository;
    private final VehicleBookingRepository vehicleBookingRepository;

    /**
     * Get all service providers grouped by type with their complete details
     */
    public AllProvidersResponse getAllServiceProviders() {
        List<ServiceProvider> allProviders = serviceProviderRepo.findAll();

        List<UnifiedServiceProviderResponse> hotels = new ArrayList<>();
        List<UnifiedServiceProviderResponse> tourGuides = new ArrayList<>();
        List<UnifiedServiceProviderResponse> agencies = new ArrayList<>();

        for (ServiceProvider provider : allProviders) {
            UnifiedServiceProviderResponse response = buildUnifiedResponse(provider);

            switch (provider.getServiceType()) {
                case HOTEL:
                    hotels.add(response);
                    break;
                case TOUR_GUIDE:
                    tourGuides.add(response);
                    break;
                case TRAVEL_AGENT:
                    agencies.add(response);
                    break;
            }
        }

        return AllProvidersResponse.builder()
                .totalProviders(allProviders.size())
                .totalHotels(hotels.size())
                .totalTourGuides(tourGuides.size())
                .totalAgencies(agencies.size())
                .hotels(hotels)
                .tourGuides(tourGuides)
                .agencies(agencies)
                .build();
    }

    /**
     * Get all approved service providers grouped by type
     */
    public AllProvidersResponse getAllApprovedServiceProviders() {
        List<ServiceProvider> approvedProviders = serviceProviderRepo.findByIsApproved(true);

        List<UnifiedServiceProviderResponse> hotels = new ArrayList<>();
        List<UnifiedServiceProviderResponse> tourGuides = new ArrayList<>();
        List<UnifiedServiceProviderResponse> agencies = new ArrayList<>();

        for (ServiceProvider provider : approvedProviders) {
            UnifiedServiceProviderResponse response = buildUnifiedResponse(provider);

            switch (provider.getServiceType()) {
                case HOTEL:
                    hotels.add(response);
                    break;
                case TOUR_GUIDE:
                    tourGuides.add(response);
                    break;
                case TRAVEL_AGENT:
                    agencies.add(response);
                    break;
            }
        }

        return AllProvidersResponse.builder()
                .totalProviders(approvedProviders.size())
                .totalHotels(hotels.size())
                .totalTourGuides(tourGuides.size())
                .totalAgencies(agencies.size())
                .hotels(hotels)
                .tourGuides(tourGuides)
                .agencies(agencies)
                .build();
    }

    /**
     * Get all service providers by specific type
     */
    public List<UnifiedServiceProviderResponse> getServiceProvidersByType(ServiceProviderType type) {
        List<ServiceProvider> providers = serviceProviderRepo.findByServiceType(type);
        return providers.stream()
                .map(this::buildUnifiedResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all approved service providers by specific type
     */
    public List<UnifiedServiceProviderResponse> getApprovedServiceProvidersByType(ServiceProviderType type) {
        List<ServiceProvider> providers = serviceProviderRepo.findByServiceTypeAndIsApproved(type, true);
        return providers.stream()
                .map(this::buildUnifiedResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a single service provider by ID with complete details
     */
    public UnifiedServiceProviderResponse getServiceProviderById(Long id) {
        ServiceProvider provider = serviceProviderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service provider not found with id: " + id));
        return buildUnifiedResponse(provider);
    }

    /**
     * Get all service providers with profile details only (no resources or revenue)
     */
    public List<ProfileOnlyResponse> getAllServiceProvidersProfileOnly() {
        List<ServiceProvider> allProviders = serviceProviderRepo.findAll();
        return allProviders.stream()
                .map(this::buildProfileOnlyResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all approved service providers with profile details only
     */
    public List<ProfileOnlyResponse> getAllApprovedServiceProvidersProfileOnly() {
        List<ServiceProvider> approvedProviders = serviceProviderRepo.findByIsApproved(true);
        return approvedProviders.stream()
                .map(this::buildProfileOnlyResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get service providers by type with profile details only
     */
    public List<ProfileOnlyResponse> getServiceProvidersByTypeProfileOnly(ServiceProviderType type) {
        List<ServiceProvider> providers = serviceProviderRepo.findByServiceType(type);
        return providers.stream()
                .map(this::buildProfileOnlyResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get approved service providers by type with profile details only
     */
    public List<ProfileOnlyResponse> getApprovedServiceProvidersByTypeProfileOnly(ServiceProviderType type) {
        List<ServiceProvider> providers = serviceProviderRepo.findByServiceTypeAndIsApproved(type, true);
        return providers.stream()
                .map(this::buildProfileOnlyResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a single service provider by ID with profile details only
     */
    public ProfileOnlyResponse getServiceProviderByIdProfileOnly(Long id) {
        ServiceProvider provider = serviceProviderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service provider not found with id: " + id));
        return buildProfileOnlyResponse(provider);
    }

    /**
     * Build profile-only response (no rooms/tours/vehicles or revenue data)
     */
    private ProfileOnlyResponse buildProfileOnlyResponse(ServiceProvider provider) {
        ProfileOnlyResponse.ProfileOnlyResponseBuilder builder = ProfileOnlyResponse.builder()
                .id(provider.getId())
                .username(provider.getUsername())
                .email(provider.getEmail())
                .businessRegistrationNumber(provider.getBusinessRegistrationNumber())
                .address(provider.getAddress())
                .contactNo(provider.getContactNo())
                .serviceType(provider.getServiceType())
                .isApproved(provider.getIsApproved())
                .isActive(provider.getIsActive())
                .isProfileCreated(provider.getIsProfileCreated())
                .createdAt(provider.getCreatedAt());

        // Load type-specific profile only (no resources or revenue)
        switch (provider.getServiceType()) {
            case HOTEL:
                loadHotelProfileOnly(provider.getId(), builder);
                break;
            case TOUR_GUIDE:
                loadTourGuideProfileOnly(provider.getId(), builder);
                break;
            case TRAVEL_AGENT:
                loadAgencyProfileOnly(provider.getId(), builder);
                break;
        }

        return builder.build();
    }

    /**
     * Load hotel profile only (no rooms or revenue)
     */
    private void loadHotelProfileOnly(Long serviceProviderId, ProfileOnlyResponse.ProfileOnlyResponseBuilder builder) {
        hotelProfileRepository.findById(serviceProviderId).ifPresent(hotelProfile -> {
            builder.hotelProfile(ProfileOnlyResponse.HotelProfileData.from(hotelProfile));
        });
    }

    /**
     * Load tour guide profile only (no tours or revenue)
     */
    private void loadTourGuideProfileOnly(Long serviceProviderId, ProfileOnlyResponse.ProfileOnlyResponseBuilder builder) {
        tourGuideProfileRepository.findById(serviceProviderId).ifPresent(tourGuideProfile -> {
            builder.tourGuideProfile(ProfileOnlyResponse.TourGuideProfileData.from(tourGuideProfile));
        });
    }

    /**
     * Load agency profile only (no vehicles or revenue)
     */
    private void loadAgencyProfileOnly(Long serviceProviderId, ProfileOnlyResponse.ProfileOnlyResponseBuilder builder) {
        agencyProfileRepository.findById(serviceProviderId).ifPresent(agencyProfile -> {
            builder.agencyProfile(ProfileOnlyResponse.AgencyProfileData.from(agencyProfile));
        });
    }

    /**
     * Build unified response with all details for a service provider
     */
    private UnifiedServiceProviderResponse buildUnifiedResponse(ServiceProvider provider) {
        UnifiedServiceProviderResponse.UnifiedServiceProviderResponseBuilder builder = UnifiedServiceProviderResponse.builder()
                .id(provider.getId())
                .username(provider.getUsername())
                .email(provider.getEmail())
                .businessRegistrationNumber(provider.getBusinessRegistrationNumber())
                .address(provider.getAddress())
                .contactNo(provider.getContactNo())
                .serviceType(provider.getServiceType())
                .isApproved(provider.getIsApproved())
                .isActive(provider.getIsActive())
                .isProfileCreated(provider.getIsProfileCreated())
                .createdAt(provider.getCreatedAt());

        // Load type-specific profile and resources
        switch (provider.getServiceType()) {
            case HOTEL:
                loadHotelData(provider.getId(), builder);
                break;
            case TOUR_GUIDE:
                loadTourGuideData(provider.getId(), builder);
                break;
            case TRAVEL_AGENT:
                loadAgencyData(provider.getId(), builder);
                break;
        }

        return builder.build();
    }

    /**
     * Load hotel profile and rooms
     */
    private void loadHotelData(Long serviceProviderId, UnifiedServiceProviderResponse.UnifiedServiceProviderResponseBuilder builder) {
        hotelProfileRepository.findById(serviceProviderId).ifPresent(hotelProfile -> {
            builder.hotelProfile(UnifiedServiceProviderResponse.HotelProfileData.from(hotelProfile));
        });

        List<Room> rooms = roomRepository.findByServiceProviderId(serviceProviderId);
        if (!rooms.isEmpty()) {
            List<UnifiedServiceProviderResponse.RoomData> roomDataList = rooms.stream()
                    .map(UnifiedServiceProviderResponse.RoomData::from)
                    .collect(Collectors.toList());
            builder.rooms(roomDataList);
        }

        // Calculate revenue for hotel
        builder.revenue(calculateHotelRevenue(serviceProviderId));
    }

    /**
     * Load tour guide profile and tours
     */
    private void loadTourGuideData(Long serviceProviderId, UnifiedServiceProviderResponse.UnifiedServiceProviderResponseBuilder builder) {
        tourGuideProfileRepository.findById(serviceProviderId).ifPresent(tourGuideProfile -> {
            builder.tourGuideProfile(UnifiedServiceProviderResponse.TourGuideProfileData.from(tourGuideProfile));
        });

        List<Tour> tours = tourRepository.findByServiceProviderId(serviceProviderId);
        if (!tours.isEmpty()) {
            List<UnifiedServiceProviderResponse.TourData> tourDataList = tours.stream()
                    .map(UnifiedServiceProviderResponse.TourData::from)
                    .collect(Collectors.toList());
            builder.tours(tourDataList);
        }

        // Calculate revenue for tour guide
        builder.revenue(calculateTourGuideRevenue(serviceProviderId));
    }

    /**
     * Load agency profile and vehicles
     */
    private void loadAgencyData(Long serviceProviderId, UnifiedServiceProviderResponse.UnifiedServiceProviderResponseBuilder builder) {
        agencyProfileRepository.findById(serviceProviderId).ifPresent(agencyProfile -> {
            builder.agencyProfile(UnifiedServiceProviderResponse.AgencyProfileData.from(agencyProfile));
        });

        List<Vehicle> vehicles = vehicleRepository.findByServiceProviderId(serviceProviderId);
        if (!vehicles.isEmpty()) {
            List<UnifiedServiceProviderResponse.VehicleData> vehicleDataList = vehicles.stream()
                    .map(UnifiedServiceProviderResponse.VehicleData::from)
                    .collect(Collectors.toList());
            builder.vehicles(vehicleDataList);
        }

        // Calculate revenue for travel agency
        builder.revenue(calculateAgencyRevenue(serviceProviderId));
    }

    /**
     * Calculate revenue for hotel service provider
     */
    private UnifiedServiceProviderResponse.RevenueData calculateHotelRevenue(Long serviceProviderId) {
        List<RoomBooking> allBookings = roomBookingRepository.findByServiceProviderId(serviceProviderId);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal completedRevenue = BigDecimal.ZERO;
        BigDecimal pendingRevenue = BigDecimal.ZERO;
        int totalBookings = allBookings.size();
        int completedBookings = 0;
        int pendingBookings = 0;
        int cancelledBookings = 0;

        for (RoomBooking booking : allBookings) {
            BigDecimal amount = booking.getTotalAmount() != null ? booking.getTotalAmount() : BigDecimal.ZERO;

            switch (booking.getStatus()) {
                case COMPLETED:
                case CHECKED_OUT:
                    completedRevenue = completedRevenue.add(amount);
                    completedBookings++;
                    break;
                case CONFIRMED:
                case CHECKED_IN:
                    pendingRevenue = pendingRevenue.add(amount);
                    pendingBookings++;
                    break;
                case CANCELLED:
                    cancelledBookings++;
                    break;
                case PENDING:
                    pendingBookings++;
                    break;
            }
        }

        totalRevenue = completedRevenue.add(pendingRevenue);
        BigDecimal averageBookingValue = totalBookings > 0
            ? totalRevenue.divide(BigDecimal.valueOf(totalBookings), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        return UnifiedServiceProviderResponse.RevenueData.builder()
                .totalRevenue(totalRevenue.toString())
                .completedRevenue(completedRevenue.toString())
                .pendingRevenue(pendingRevenue.toString())
                .totalBookings(totalBookings)
                .completedBookings(completedBookings)
                .pendingBookings(pendingBookings)
                .cancelledBookings(cancelledBookings)
                .averageBookingValue(averageBookingValue.toString())
                .build();
    }

    /**
     * Calculate revenue for tour guide service provider
     */
    private UnifiedServiceProviderResponse.RevenueData calculateTourGuideRevenue(Long serviceProviderId) {
        List<TourBooking> allBookings = tourBookingRepository.findByServiceProviderId(serviceProviderId);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal completedRevenue = BigDecimal.ZERO;
        BigDecimal pendingRevenue = BigDecimal.ZERO;
        int totalBookings = allBookings.size();
        int completedBookings = 0;
        int pendingBookings = 0;
        int cancelledBookings = 0;

        for (TourBooking booking : allBookings) {
            BigDecimal amount = booking.getTotalAmount() != null ? booking.getTotalAmount() : BigDecimal.ZERO;

            switch (booking.getStatus()) {
                case COMPLETED:
                    completedRevenue = completedRevenue.add(amount);
                    completedBookings++;
                    break;
                case APPROVED:
                    pendingRevenue = pendingRevenue.add(amount);
                    pendingBookings++;
                    break;
                case CANCELLED:
                case REJECTED:
                    cancelledBookings++;
                    break;
                case PENDING_APPROVAL:
                    pendingBookings++;
                    break;
            }
        }

        totalRevenue = completedRevenue.add(pendingRevenue);
        BigDecimal averageBookingValue = totalBookings > 0
            ? totalRevenue.divide(BigDecimal.valueOf(totalBookings), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        return UnifiedServiceProviderResponse.RevenueData.builder()
                .totalRevenue(totalRevenue.toString())
                .completedRevenue(completedRevenue.toString())
                .pendingRevenue(pendingRevenue.toString())
                .totalBookings(totalBookings)
                .completedBookings(completedBookings)
                .pendingBookings(pendingBookings)
                .cancelledBookings(cancelledBookings)
                .averageBookingValue(averageBookingValue.toString())
                .build();
    }

    /**
     * Calculate revenue for travel agency service provider
     */
    private UnifiedServiceProviderResponse.RevenueData calculateAgencyRevenue(Long serviceProviderId) {
        List<VehicleBooking> allBookings = vehicleBookingRepository.findByServiceProviderId(serviceProviderId);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal completedRevenue = BigDecimal.ZERO;
        BigDecimal pendingRevenue = BigDecimal.ZERO;
        int totalBookings = allBookings.size();
        int completedBookings = 0;
        int pendingBookings = 0;
        int cancelledBookings = 0;

        for (VehicleBooking booking : allBookings) {
            BigDecimal amount = booking.getEstimatedTotalAmount() != null ? booking.getEstimatedTotalAmount() : BigDecimal.ZERO;

            switch (booking.getStatus()) {
                case COMPLETED:
                    completedRevenue = completedRevenue.add(amount);
                    completedBookings++;
                    break;
                case APPROVED:
                    pendingRevenue = pendingRevenue.add(amount);
                    pendingBookings++;
                    break;
                case CANCELLED:
                case REJECTED:
                    cancelledBookings++;
                    break;
                case PENDING_APPROVAL:
                    pendingBookings++;
                    break;
            }
        }

        totalRevenue = completedRevenue.add(pendingRevenue);
        BigDecimal averageBookingValue = totalBookings > 0
            ? totalRevenue.divide(BigDecimal.valueOf(totalBookings), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        return UnifiedServiceProviderResponse.RevenueData.builder()
                .totalRevenue(totalRevenue.toString())
                .completedRevenue(completedRevenue.toString())
                .pendingRevenue(pendingRevenue.toString())
                .totalBookings(totalBookings)
                .completedBookings(completedBookings)
                .pendingBookings(pendingBookings)
                .cancelledBookings(cancelledBookings)
                .averageBookingValue(averageBookingValue.toString())
                .build();
    }
}