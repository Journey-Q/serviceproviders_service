// Repository
package com.example.serviceproviders_service.repository.ServiceProvider;

import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.AgencyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgencyProfileRepository extends JpaRepository<AgencyProfile, Long> {

    boolean existsByAgencyName(String agencyName);

    boolean existsByAgencyNameAndServiceProviderIdNot(String agencyName, Long serviceProviderId);

    boolean existsByServiceProviderId(Long serviceProviderId);
}