package com.example.serviceproviders_service.controller.ServiceProvider;

import com.example.serviceproviders_service.dto.ServiceProvider.AllProvidersResponse;
import com.example.serviceproviders_service.dto.ServiceProvider.ProfileOnlyResponse;
import com.example.serviceproviders_service.dto.ServiceProvider.UnifiedServiceProviderResponse;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProviderType;
import com.example.serviceproviders_service.services.ServiceProvider.UnifiedServiceProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Unified controller to access all service providers and their details in one place
 */
@RestController
@RequestMapping("/service/providers")
@RequiredArgsConstructor
public class UnifiedServiceProviderController {

    private final UnifiedServiceProviderService unifiedService;

    /**
     * Get all service providers grouped by type with complete details
     * Endpoint: GET /service/providers/all
     */
    @GetMapping("/all")
    public ResponseEntity<AllProvidersResponse> getAllServiceProviders() {
        AllProvidersResponse response = unifiedService.getAllServiceProviders();
        return ResponseEntity.ok(response);
    }

    /**
     * Get all approved service providers grouped by type with complete details
     * Endpoint: GET /service/providers/approved
     */
    @GetMapping("/approved")
    public ResponseEntity<AllProvidersResponse> getAllApprovedServiceProviders() {
        AllProvidersResponse response = unifiedService.getAllApprovedServiceProviders();
        return ResponseEntity.ok(response);
    }

    /**
     * Get all hotels with their profiles and rooms
     * Endpoint: GET /service/providers/hotels
     */
    @GetMapping("/hotels")
    public ResponseEntity<List<UnifiedServiceProviderResponse>> getAllHotels(
            @RequestParam(required = false, defaultValue = "false") Boolean approvedOnly) {
        List<UnifiedServiceProviderResponse> hotels;
        if (approvedOnly) {
            hotels = unifiedService.getApprovedServiceProvidersByType(ServiceProviderType.HOTEL);
        } else {
            hotels = unifiedService.getServiceProvidersByType(ServiceProviderType.HOTEL);
        }
        return ResponseEntity.ok(hotels);
    }

    /**
     * Get all tour guides with their profiles and tours
     * Endpoint: GET /service/providers/tour-guides
     */
    @GetMapping("/tour-guides")
    public ResponseEntity<List<UnifiedServiceProviderResponse>> getAllTourGuides(
            @RequestParam(required = false, defaultValue = "false") Boolean approvedOnly) {
        List<UnifiedServiceProviderResponse> tourGuides;
        if (approvedOnly) {
            tourGuides = unifiedService.getApprovedServiceProvidersByType(ServiceProviderType.TOUR_GUIDE);
        } else {
            tourGuides = unifiedService.getServiceProvidersByType(ServiceProviderType.TOUR_GUIDE);
        }
        return ResponseEntity.ok(tourGuides);
    }

    /**
     * Get all travel agencies with their profiles and vehicles
     * Endpoint: GET /service/providers/agencies
     */
    @GetMapping("/agencies")
    public ResponseEntity<List<UnifiedServiceProviderResponse>> getAllAgencies(
            @RequestParam(required = false, defaultValue = "false") Boolean approvedOnly) {
        List<UnifiedServiceProviderResponse> agencies;
        if (approvedOnly) {
            agencies = unifiedService.getApprovedServiceProvidersByType(ServiceProviderType.TRAVEL_AGENT);
        } else {
            agencies = unifiedService.getServiceProvidersByType(ServiceProviderType.TRAVEL_AGENT);
        }
        return ResponseEntity.ok(agencies);
    }

    /**
     * Get service providers by type (generic endpoint)
     * Endpoint: GET /service/providers/by-type/{type}
     * @param type - HOTEL, TOUR_GUIDE, or TRAVEL_AGENT
     */
    @GetMapping("/by-type/{type}")
    public ResponseEntity<List<UnifiedServiceProviderResponse>> getServiceProvidersByType(
            @PathVariable ServiceProviderType type,
            @RequestParam(required = false, defaultValue = "false") Boolean approvedOnly) {
        List<UnifiedServiceProviderResponse> providers;
        if (approvedOnly) {
            providers = unifiedService.getApprovedServiceProvidersByType(type);
        } else {
            providers = unifiedService.getServiceProvidersByType(type);
        }
        return ResponseEntity.ok(providers);
    }

    /**
     * Get a specific service provider by ID with complete details
     * Endpoint: GET /service/providers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UnifiedServiceProviderResponse> getServiceProviderById(@PathVariable Long id) {
        UnifiedServiceProviderResponse response = unifiedService.getServiceProviderById(id);
        return ResponseEntity.ok(response);
    }

    // ==================== PROFILE ONLY ENDPOINTS ====================

    /**
     * Get all service providers with profile details only (no resources or revenue)
     * Endpoint: GET /service/providers/profiles/all
     */
    @GetMapping("/profiles/all")
    public ResponseEntity<List<ProfileOnlyResponse>> getAllServiceProvidersProfileOnly() {
        List<ProfileOnlyResponse> response = unifiedService.getAllServiceProvidersProfileOnly();
        return ResponseEntity.ok(response);
    }

    /**
     * Get all approved service providers with profile details only
     * Endpoint: GET /service/providers/profiles/approved
     */
    @GetMapping("/profiles/approved")
    public ResponseEntity<List<ProfileOnlyResponse>> getAllApprovedServiceProvidersProfileOnly() {
        List<ProfileOnlyResponse> response = unifiedService.getAllApprovedServiceProvidersProfileOnly();
        return ResponseEntity.ok(response);
    }

    /**
     * Get all hotels with profile details only (no rooms or revenue)
     * Endpoint: GET /service/providers/profiles/hotels
     */
    @GetMapping("/profiles/hotels")
    public ResponseEntity<List<ProfileOnlyResponse>> getAllHotelsProfileOnly(
            @RequestParam(required = false, defaultValue = "false") Boolean approvedOnly) {
        List<ProfileOnlyResponse> hotels;
        if (approvedOnly) {
            hotels = unifiedService.getApprovedServiceProvidersByTypeProfileOnly(ServiceProviderType.HOTEL);
        } else {
            hotels = unifiedService.getServiceProvidersByTypeProfileOnly(ServiceProviderType.HOTEL);
        }
        return ResponseEntity.ok(hotels);
    }

    /**
     * Get all tour guides with profile details only (no tours or revenue)
     * Endpoint: GET /service/providers/profiles/tour-guides
     */
    @GetMapping("/profiles/tour-guides")
    public ResponseEntity<List<ProfileOnlyResponse>> getAllTourGuidesProfileOnly(
            @RequestParam(required = false, defaultValue = "false") Boolean approvedOnly) {
        List<ProfileOnlyResponse> tourGuides;
        if (approvedOnly) {
            tourGuides = unifiedService.getApprovedServiceProvidersByTypeProfileOnly(ServiceProviderType.TOUR_GUIDE);
        } else {
            tourGuides = unifiedService.getServiceProvidersByTypeProfileOnly(ServiceProviderType.TOUR_GUIDE);
        }
        return ResponseEntity.ok(tourGuides);
    }

    /**
     * Get all travel agencies with profile details only (no vehicles or revenue)
     * Endpoint: GET /service/providers/profiles/agencies
     */
    @GetMapping("/profiles/agencies")
    public ResponseEntity<List<ProfileOnlyResponse>> getAllAgenciesProfileOnly(
            @RequestParam(required = false, defaultValue = "false") Boolean approvedOnly) {
        List<ProfileOnlyResponse> agencies;
        if (approvedOnly) {
            agencies = unifiedService.getApprovedServiceProvidersByTypeProfileOnly(ServiceProviderType.TRAVEL_AGENT);
        } else {
            agencies = unifiedService.getServiceProvidersByTypeProfileOnly(ServiceProviderType.TRAVEL_AGENT);
        }
        return ResponseEntity.ok(agencies);
    }

    /**
     * Get service providers by type with profile details only
     * Endpoint: GET /service/providers/profiles/by-type/{type}
     * @param type - HOTEL, TOUR_GUIDE, or TRAVEL_AGENT
     */
    @GetMapping("/profiles/by-type/{type}")
    public ResponseEntity<List<ProfileOnlyResponse>> getServiceProvidersByTypeProfileOnly(
            @PathVariable ServiceProviderType type,
            @RequestParam(required = false, defaultValue = "false") Boolean approvedOnly) {
        List<ProfileOnlyResponse> providers;
        if (approvedOnly) {
            providers = unifiedService.getApprovedServiceProvidersByTypeProfileOnly(type);
        } else {
            providers = unifiedService.getServiceProvidersByTypeProfileOnly(type);
        }
        return ResponseEntity.ok(providers);
    }

    /**
     * Get a specific service provider by ID with profile details only
     * Endpoint: GET /service/providers/profiles/{id}
     */
    @GetMapping("/profiles/{id}")
    public ResponseEntity<ProfileOnlyResponse> getServiceProviderByIdProfileOnly(@PathVariable Long id) {
        ProfileOnlyResponse response = unifiedService.getServiceProviderByIdProfileOnly(id);
        return ResponseEntity.ok(response);
    }
}