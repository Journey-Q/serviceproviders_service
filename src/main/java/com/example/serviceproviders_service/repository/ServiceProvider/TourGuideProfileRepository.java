package com.example.serviceproviders_service.repository.ServiceProvider;

import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourGuideProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TourGuideProfileRepository extends JpaRepository<TourGuideProfile, Long> {

    boolean existsByServiceProviderId(Long serviceProviderId);

    boolean existsByCompanyNameAndContactInfoAddress(String companyName, String address);

    boolean existsByCompanyNameAndContactInfoAddressAndServiceProviderIdNot(
            String companyName,
            String address,
            Long idToExclude);

    Optional<TourGuideProfile> findByServiceProviderId(Long serviceProviderId);
}