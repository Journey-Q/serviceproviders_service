package com.example.serviceproviders_service.dto.ServiceProvider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Wrapper DTO for returning all service providers grouped by type
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllProvidersResponse {
    private Integer totalProviders;
    private Integer totalHotels;
    private Integer totalTourGuides;
    private Integer totalAgencies;
    private List<UnifiedServiceProviderResponse> hotels;
    private List<UnifiedServiceProviderResponse> tourGuides;
    private List<UnifiedServiceProviderResponse> agencies;
}