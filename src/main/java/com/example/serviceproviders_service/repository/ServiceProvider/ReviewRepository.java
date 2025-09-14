package com.example.serviceproviders_service.repository.ServiceProvider;

import com.example.serviceproviders_service.entity.serviceProvider.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // User-based queries
    List<Review> findByUserId(Long userId);

    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT r FROM Review r WHERE r.userId = :userId AND r.status = 'ACTIVE' ORDER BY r.createdAt DESC")
    List<Review> findActiveReviewsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.userId = :userId AND r.status = 'ACTIVE'")
    long countActiveReviewsByUserId(@Param("userId") Long userId);

    @Query("SELECT AVG(CAST(r.rating AS double)) FROM Review r WHERE r.userId = :userId AND r.status = 'ACTIVE'")
    BigDecimal getAverageRatingByUserId(@Param("userId") Long userId);

    // Service provider-based queries
    List<Review> findByServiceProviderId(Long serviceProviderId);

    List<Review> findByServiceProviderIdAndStatus(Long serviceProviderId, Review.ReviewStatus status);

    List<Review> findByServiceProviderIdOrderByCreatedAtDesc(Long serviceProviderId);

    List<Review> findByServiceProviderIdAndRating(Long serviceProviderId, Integer rating);

    Optional<Review> findByBookingId(String bookingId);

    boolean existsByBookingId(String bookingId);

    @Query("SELECT r FROM Review r WHERE r.serviceProviderId = :serviceProviderId AND r.status = 'ACTIVE' ORDER BY r.createdAt DESC")
    List<Review> findActiveReviewsByServiceProviderId(@Param("serviceProviderId") Long serviceProviderId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.serviceProviderId = :serviceProviderId AND r.status = 'ACTIVE'")
    long countActiveReviewsByServiceProviderId(@Param("serviceProviderId") Long serviceProviderId);

    @Query("SELECT AVG(CAST(r.rating AS double)) FROM Review r WHERE r.serviceProviderId = :serviceProviderId AND r.status = 'ACTIVE'")
    BigDecimal getAverageRatingByServiceProviderId(@Param("serviceProviderId") Long serviceProviderId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.serviceProviderId = :serviceProviderId AND r.rating = :rating AND r.status = 'ACTIVE'")
    long countByServiceProviderIdAndRatingAndStatusActive(@Param("serviceProviderId") Long serviceProviderId, @Param("rating") Integer rating);

    @Query("SELECT r FROM Review r WHERE r.serviceProviderId = :serviceProviderId AND r.rating >= :minRating AND r.status = 'ACTIVE' ORDER BY r.createdAt DESC")
    List<Review> findByServiceProviderIdAndMinRating(@Param("serviceProviderId") Long serviceProviderId, @Param("minRating") Integer minRating);

    List<Review> findByStatus(Review.ReviewStatus status);

    @Query("SELECT r FROM Review r WHERE r.serviceProviderId = :serviceProviderId AND r.isVerified = :isVerified ORDER BY r.createdAt DESC")
    List<Review> findByServiceProviderIdAndIsVerified(@Param("serviceProviderId") Long serviceProviderId, @Param("isVerified") Boolean isVerified);

    long countByServiceProviderIdAndStatus(Long serviceProviderId, Review.ReviewStatus status);

    // Combined user and service provider queries
    @Query("SELECT r FROM Review r WHERE r.userId = :userId AND r.serviceProviderId = :serviceProviderId ORDER BY r.createdAt DESC")
    List<Review> findByUserIdAndServiceProviderId(@Param("userId") Long userId, @Param("serviceProviderId") Long serviceProviderId);
}