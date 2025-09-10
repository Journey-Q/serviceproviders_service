package com.example.serviceproviders_service.repository.ServiceProvider.Hotel;

import com.example.serviceproviders_service.entity.serviceProvider.Hotel.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findByServiceProviderId(Long serviceProviderId);

    List<Promotion> findByServiceProviderIdAndStatus(Long serviceProviderId, Promotion.PromotionStatus status);

    List<Promotion> findByStatus(Promotion.PromotionStatus status);

    boolean existsByServiceProviderIdAndTitle(Long serviceProviderId, String title);

    boolean existsByServiceProviderIdAndTitleAndIdNot(Long serviceProviderId, String title, Long id);

    @Query("SELECT p FROM Promotion p WHERE p.serviceProviderId = :serviceProviderId AND p.isActive = true AND p.status = 'ADVERTISED'")
    List<Promotion> findActiveAdvertisedPromotionsByServiceProviderId(@Param("serviceProviderId") Long serviceProviderId);

    @Query("SELECT p FROM Promotion p WHERE p.status = 'ADVERTISED' AND p.isActive = true AND p.validFrom <= :currentDate AND p.validTo >= :currentDate")
    List<Promotion> findCurrentActivePromotions(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT p FROM Promotion p WHERE p.serviceProviderId = :serviceProviderId AND p.validFrom <= :currentDate AND p.validTo >= :currentDate")
    List<Promotion> findCurrentPromotionsByServiceProviderId(@Param("serviceProviderId") Long serviceProviderId, @Param("currentDate") LocalDate currentDate);

    long countByServiceProviderId(Long serviceProviderId);

    long countByServiceProviderIdAndStatus(Long serviceProviderId, Promotion.PromotionStatus status);

    @Query("SELECT p FROM Promotion p WHERE p.serviceProviderId = :serviceProviderId AND p.discount >= :minDiscount")
    List<Promotion> findByServiceProviderIdAndMinDiscount(@Param("serviceProviderId") Long serviceProviderId, @Param("minDiscount") Integer minDiscount);
}