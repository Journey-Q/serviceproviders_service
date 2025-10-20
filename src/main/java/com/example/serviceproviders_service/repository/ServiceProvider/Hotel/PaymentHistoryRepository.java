package com.example.serviceproviders_service.repository.ServiceProvider.Hotel;

import com.example.serviceproviders_service.entity.serviceProvider.Hotel.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    // Find payment by paymentId
    Optional<PaymentHistory> findByPaymentId(String paymentId);

    // Find payment by bookingId
    Optional<PaymentHistory> findByBookingId(Long bookingId);

    // Find all payments for a service provider
    List<PaymentHistory> findByServiceProviderId(Long serviceProviderId);

    // Find all completed payments for a service provider
    List<PaymentHistory> findByServiceProviderIdAndStatus(Long serviceProviderId, PaymentHistory.PaymentStatus status);

    // Find all payments for a service provider filtered by date range
    @Query("SELECT p FROM PaymentHistory p WHERE p.serviceProviderId = :serviceProviderId " +
           "AND p.paymentDate >= :startDate AND p.paymentDate <= :endDate " +
           "ORDER BY p.paymentDate DESC")
    List<PaymentHistory> findByServiceProviderIdAndDateRange(
            @Param("serviceProviderId") Long serviceProviderId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Find all completed payments for a service provider filtered by date range
    @Query("SELECT p FROM PaymentHistory p WHERE p.serviceProviderId = :serviceProviderId " +
           "AND p.status = 'COMPLETED' " +
           "AND p.paymentDate >= :startDate AND p.paymentDate <= :endDate " +
           "ORDER BY p.paymentDate DESC")
    List<PaymentHistory> findCompletedPaymentsByServiceProviderIdAndDateRange(
            @Param("serviceProviderId") Long serviceProviderId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Find all payments for a service provider filtered by month and year
    @Query("SELECT p FROM PaymentHistory p WHERE p.serviceProviderId = :serviceProviderId " +
           "AND YEAR(p.paymentDate) = :year " +
           "AND MONTH(p.paymentDate) = :month " +
           "ORDER BY p.paymentDate DESC")
    List<PaymentHistory> findByServiceProviderIdAndYearAndMonth(
            @Param("serviceProviderId") Long serviceProviderId,
            @Param("year") int year,
            @Param("month") int month
    );

    // Find all completed payments for a service provider filtered by month and year
    @Query("SELECT p FROM PaymentHistory p WHERE p.serviceProviderId = :serviceProviderId " +
           "AND p.status = 'COMPLETED' " +
           "AND YEAR(p.paymentDate) = :year " +
           "AND MONTH(p.paymentDate) = :month " +
           "ORDER BY p.paymentDate DESC")
    List<PaymentHistory> findCompletedPaymentsByServiceProviderIdAndYearAndMonth(
            @Param("serviceProviderId") Long serviceProviderId,
            @Param("year") int year,
            @Param("month") int month
    );

    // Find all completed payments for a service provider filtered by year
    @Query("SELECT p FROM PaymentHistory p WHERE p.serviceProviderId = :serviceProviderId " +
           "AND p.status = 'COMPLETED' " +
           "AND YEAR(p.paymentDate) = :year " +
           "ORDER BY p.paymentDate DESC")
    List<PaymentHistory> findCompletedPaymentsByServiceProviderIdAndYear(
            @Param("serviceProviderId") Long serviceProviderId,
            @Param("year") int year
    );

    // Find all payments by guest email
    List<PaymentHistory> findByGuestEmail(String guestEmail);

    // Find all payments by room
    List<PaymentHistory> findByRoomId(Long roomId);
}