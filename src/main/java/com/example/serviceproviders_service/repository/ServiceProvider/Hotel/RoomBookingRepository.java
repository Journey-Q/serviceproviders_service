package com.example.serviceproviders_service.repository.ServiceProvider.Hotel;

import com.example.serviceproviders_service.entity.serviceProvider.Hotel.RoomBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {

    List<RoomBooking> findByServiceProviderId(Long serviceProviderId);

    List<RoomBooking> findByServiceProviderIdAndStatus(Long serviceProviderId, RoomBooking.RoomBookingStatus status);

    List<RoomBooking> findByRoomId(Long roomId);

    List<RoomBooking> findByUserId(Long userId);

    List<RoomBooking> findByGuestEmail(String guestEmail);

    Optional<RoomBooking> findByBookingReference(String bookingReference);

    Optional<RoomBooking> findByStripeSessionId(String stripeSessionId);

    List<RoomBooking> findByPaymentStatus(RoomBooking.PaymentStatus paymentStatus);

    @Query("SELECT rb FROM RoomBooking rb WHERE rb.roomId = :roomId AND rb.status != 'CANCELLED' " +
            "AND ((rb.checkInDate <= :checkOutDate AND rb.checkOutDate >= :checkInDate))")
    List<RoomBooking> findConflictingRoomBookings(@Param("roomId") Long roomId,
                                                  @Param("checkInDate") LocalDate checkInDate,
                                                  @Param("checkOutDate") LocalDate checkOutDate);

    @Query("SELECT rb FROM RoomBooking rb WHERE rb.serviceProviderId = :serviceProviderId " +
            "AND rb.checkInDate BETWEEN :startDate AND :endDate")
    List<RoomBooking> findByServiceProviderIdAndDateRange(@Param("serviceProviderId") Long serviceProviderId,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);

    @Query("SELECT rb FROM RoomBooking rb WHERE rb.createdAt BETWEEN :startDate AND :endDate")
    List<RoomBooking> findPaymentsByDateRange(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT rb FROM RoomBooking rb WHERE rb.paymentStatus = 'SUCCEEDED' AND rb.paidAt BETWEEN :startDate AND :endDate")
    List<RoomBooking> findSuccessfulPaymentsByDateRange(@Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);

    long countByServiceProviderId(Long serviceProviderId);

    long countByServiceProviderIdAndStatus(Long serviceProviderId, RoomBooking.RoomBookingStatus status);

    long countByPaymentStatus(RoomBooking.PaymentStatus paymentStatus);
}