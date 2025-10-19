package com.example.serviceproviders_service.repository.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourPackageReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TourPackageReviewRepository extends JpaRepository<TourPackageReview, Long> {

    // Find by booking ID
    Optional<TourPackageReview> findByBookingId(String bookingId);

    boolean existsByBookingId(String bookingId);

    // Find by tour ID
    List<TourPackageReview> findByTourIdOrderByCreatedAtDesc(Long tourId);

    @Query("SELECT r FROM TourPackageReview r WHERE r.tourId = :tourId AND r.status = 'ACTIVE' ORDER BY r.createdAt DESC")
    List<TourPackageReview> findActiveReviewsByTourId(@Param("tourId") Long tourId);

    List<TourPackageReview> findByTourIdAndStatus(Long tourId, TourPackageReview.ReviewStatus status);

    List<TourPackageReview> findByTourIdAndRating(Long tourId, Integer rating);

    // Find by user ID
    List<TourPackageReview> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT r FROM TourPackageReview r WHERE r.userId = :userId AND r.status = 'ACTIVE' ORDER BY r.createdAt DESC")
    List<TourPackageReview> findActiveReviewsByUserId(@Param("userId") Long userId);

    // Combined filters
    List<TourPackageReview> findByUserIdAndTourId(Long userId, Long tourId);

    // Statistics queries
    @Query("SELECT COUNT(r) FROM TourPackageReview r WHERE r.tourId = :tourId AND r.status = 'ACTIVE'")
    long countActiveReviewsByTourId(@Param("tourId") Long tourId);

    @Query("SELECT AVG(r.rating) FROM TourPackageReview r WHERE r.tourId = :tourId AND r.status = 'ACTIVE'")
    BigDecimal getAverageRatingByTourId(@Param("tourId") Long tourId);

    @Query("SELECT COUNT(r) FROM TourPackageReview r WHERE r.tourId = :tourId AND r.rating = :rating AND r.status = 'ACTIVE'")
    long countByTourIdAndRatingAndStatusActive(@Param("tourId") Long tourId, @Param("rating") Integer rating);
}