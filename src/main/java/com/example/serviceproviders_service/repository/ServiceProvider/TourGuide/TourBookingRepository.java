package com.example.serviceproviders_service.repository.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TourBookingRepository extends JpaRepository<TourBooking, Long> {

    List<TourBooking> findByServiceProviderId(Long serviceProviderId);

    List<TourBooking> findByServiceProviderIdAndStatus(Long serviceProviderId, TourBooking.TourBookingStatus status);

    List<TourBooking> findByTourId(Long tourId);

    List<TourBooking> findByUserId(Long userId);

    List<TourBooking> findByCustomerEmail(String customerEmail);

    Optional<TourBooking> findByBookingReference(String bookingReference);

    Optional<TourBooking> findByStripeSessionId(String stripeSessionId);

    List<TourBooking> findByPaymentStatus(TourBooking.PaymentStatus paymentStatus);

    @Query("SELECT tb FROM TourBooking tb WHERE tb.tourId = :tourId AND tb.status != 'CANCELLED' " +
            "AND tb.tourDate = :tourDate")
    List<TourBooking> findBookingsForTourOnDate(@Param("tourId") Long tourId,
                                                 @Param("tourDate") LocalDate tourDate);

    @Query("SELECT COALESCE(SUM(tb.numberOfPeople), 0) FROM TourBooking tb WHERE tb.tourId = :tourId " +
            "AND tb.tourDate = :tourDate AND tb.status != 'CANCELLED'")
    Integer countPeopleForTourOnDate(@Param("tourId") Long tourId,
                                      @Param("tourDate") LocalDate tourDate);

    @Query("SELECT tb FROM TourBooking tb WHERE tb.serviceProviderId = :serviceProviderId " +
            "AND tb.tourDate BETWEEN :startDate AND :endDate")
    List<TourBooking> findByServiceProviderIdAndDateRange(@Param("serviceProviderId") Long serviceProviderId,
                                                           @Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate);

    @Query("SELECT tb FROM TourBooking tb WHERE tb.createdAt BETWEEN :startDate AND :endDate")
    List<TourBooking> findPaymentsByDateRange(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT tb FROM TourBooking tb WHERE tb.paymentStatus = 'SUCCEEDED' AND tb.paidAt BETWEEN :startDate AND :endDate")
    List<TourBooking> findSuccessfulPaymentsByDateRange(@Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);

    long countByServiceProviderId(Long serviceProviderId);

    long countByServiceProviderIdAndStatus(Long serviceProviderId, TourBooking.TourBookingStatus status);

    long countByPaymentStatus(TourBooking.PaymentStatus paymentStatus);
}