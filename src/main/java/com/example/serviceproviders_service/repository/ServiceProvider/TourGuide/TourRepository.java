package com.example.serviceproviders_service.repository.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {

    List<Tour> findByServiceProviderId(Long serviceProviderId);

    List<Tour> findByServiceProviderIdAndStatus(Long serviceProviderId, Tour.TourStatus status);

    List<Tour> findByStatus(Tour.TourStatus status);

    boolean existsByServiceProviderIdAndName(Long serviceProviderId, String name);

    boolean existsByServiceProviderIdAndNameAndIdNot(Long serviceProviderId, String name, Long id);

    @Query("SELECT t FROM Tour t WHERE t.serviceProviderId = :serviceProviderId AND t.status = 'AVAILABLE'")
    List<Tour> findAvailableToursByServiceProviderId(@Param("serviceProviderId") Long serviceProviderId);

    @Query("SELECT t FROM Tour t WHERE t.status = 'AVAILABLE' AND t.finalPrice BETWEEN :minPrice AND :maxPrice")
    List<Tour> findAvailableToursInPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT t FROM Tour t WHERE t.serviceProviderId = :serviceProviderId AND t.finalPrice BETWEEN :minPrice AND :maxPrice")
    List<Tour> findByServiceProviderIdAndPriceRange(@Param("serviceProviderId") Long serviceProviderId,
                                                    @Param("minPrice") BigDecimal minPrice,
                                                    @Param("maxPrice") BigDecimal maxPrice);

    List<Tour> findByServiceProviderIdAndDuration(Long serviceProviderId, String duration);

    @Query("SELECT t FROM Tour t WHERE t.serviceProviderId = :serviceProviderId AND t.rating >= :minRating")
    List<Tour> findByServiceProviderIdAndMinRating(@Param("serviceProviderId") Long serviceProviderId, @Param("minRating") BigDecimal minRating);

    long countByServiceProviderId(Long serviceProviderId);

    long countByServiceProviderIdAndStatus(Long serviceProviderId, Tour.TourStatus status);
}