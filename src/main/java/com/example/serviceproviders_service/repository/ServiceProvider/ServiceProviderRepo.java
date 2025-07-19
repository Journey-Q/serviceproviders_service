// repository/ServiceProviderRepo.java
package com.example.serviceproviders_service.repository.ServiceProvider;

import com.example.serviceproviders_service.entity.serviceProvider.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceProviderRepo extends JpaRepository<ServiceProvider, Long> {

    Optional<ServiceProvider> findByUsername(String username);

    Optional<ServiceProvider> findByEmail(String email);

    Optional<ServiceProvider> findByBusinessRegistrationNumber(String businessRegistrationNumber);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByBusinessRegistrationNumber(String businessRegistrationNumber);

    List<ServiceProvider> findByServiceType(ServiceProvider serviceType);

    List<ServiceProvider> findByIsApproved(Boolean isApproved);

    List<ServiceProvider> findByIsActive(Boolean isActive);

    List<ServiceProvider> findByServiceTypeAndIsApproved(ServiceProvider serviceType, Boolean isApproved);
}