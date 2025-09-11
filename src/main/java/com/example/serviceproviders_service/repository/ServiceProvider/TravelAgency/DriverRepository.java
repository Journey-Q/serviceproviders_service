package com.example.serviceproviders_service.repository.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    List<Driver> findByServiceProviderId(Long serviceProviderId);

    List<Driver> findByServiceProviderIdAndStatus(Long serviceProviderId, Driver.DriverStatus status);

    boolean existsByServiceProviderIdAndName(Long serviceProviderId, String name);

    boolean existsByServiceProviderIdAndNameAndIdNot(Long serviceProviderId, String name, Long id);

    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumberAndIdNot(String licenseNumber, Long id);

    boolean existsByContactNumber(String contactNumber);

    boolean existsByContactNumberAndIdNot(String contactNumber, Long id);

    @Query("SELECT d FROM Driver d WHERE d.serviceProviderId = :serviceProviderId AND d.status = 'AVAILABLE'")
    List<Driver> findAvailableDriversByServiceProviderId(@Param("serviceProviderId") Long serviceProviderId);

    @Query("SELECT d FROM Driver d WHERE d.serviceProviderId = :serviceProviderId AND d.experience >= :minExperience")
    List<Driver> findByServiceProviderIdAndMinExperience(@Param("serviceProviderId") Long serviceProviderId, @Param("minExperience") Integer minExperience);

    @Query("SELECT d FROM Driver d WHERE d.serviceProviderId = :serviceProviderId AND d.rating >= :minRating")
    List<Driver> findByServiceProviderIdAndMinRating(@Param("serviceProviderId") Long serviceProviderId, @Param("minRating") BigDecimal minRating);

    @Query("SELECT d FROM Driver d WHERE d.serviceProviderId = :serviceProviderId AND :language MEMBER OF d.languages")
    List<Driver> findByServiceProviderIdAndLanguage(@Param("serviceProviderId") Long serviceProviderId, @Param("language") String language);

    long countByServiceProviderId(Long serviceProviderId);

    long countByServiceProviderIdAndStatus(Long serviceProviderId, Driver.DriverStatus status);

    @Query("SELECT d FROM Driver d WHERE d.serviceProviderId = :serviceProviderId AND (LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(d.licenseNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Driver> findByServiceProviderIdAndNameOrLicenseContaining(@Param("serviceProviderId") Long serviceProviderId, @Param("searchTerm") String searchTerm);
}
