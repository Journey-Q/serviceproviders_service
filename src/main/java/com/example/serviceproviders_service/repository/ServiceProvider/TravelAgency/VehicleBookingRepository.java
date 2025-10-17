package com.example.serviceproviders_service.repository.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.VehicleBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleBookingRepository extends JpaRepository<VehicleBooking, Long> {

    List<VehicleBooking> findByServiceProviderId(Long serviceProviderId);

    List<VehicleBooking> findByServiceProviderIdAndStatus(Long serviceProviderId, VehicleBooking.VehicleBookingStatus status);

    List<VehicleBooking> findByVehicleId(Long vehicleId);

    List<VehicleBooking> findByUserId(Long userId);

    List<VehicleBooking> findByCustomerEmail(String customerEmail);

    Optional<VehicleBooking> findByBookingReference(String bookingReference);

    Optional<VehicleBooking> findByStripeSessionId(String stripeSessionId);

    List<VehicleBooking> findByPaymentStatus(VehicleBooking.PaymentStatus paymentStatus);

    @Query("SELECT vb FROM VehicleBooking vb WHERE vb.vehicleId = :vehicleId AND vb.status != 'CANCELLED' " +
            "AND ((vb.pickupDate <= :returnDate AND vb.returnDate >= :pickupDate))")
    List<VehicleBooking> findConflictingVehicleBookings(@Param("vehicleId") Long vehicleId,
                                                         @Param("pickupDate") LocalDate pickupDate,
                                                         @Param("returnDate") LocalDate returnDate);

    @Query("SELECT vb FROM VehicleBooking vb WHERE vb.serviceProviderId = :serviceProviderId " +
            "AND vb.pickupDate BETWEEN :startDate AND :endDate")
    List<VehicleBooking> findByServiceProviderIdAndDateRange(@Param("serviceProviderId") Long serviceProviderId,
                                                              @Param("startDate") LocalDate startDate,
                                                              @Param("endDate") LocalDate endDate);

    @Query("SELECT vb FROM VehicleBooking vb WHERE vb.createdAt BETWEEN :startDate AND :endDate")
    List<VehicleBooking> findPaymentsByDateRange(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT vb FROM VehicleBooking vb WHERE vb.paymentStatus = 'SUCCEEDED' AND vb.paidAt BETWEEN :startDate AND :endDate")
    List<VehicleBooking> findSuccessfulPaymentsByDateRange(@Param("startDate") LocalDateTime startDate,
                                                            @Param("endDate") LocalDateTime endDate);

    long countByServiceProviderId(Long serviceProviderId);

    long countByServiceProviderIdAndStatus(Long serviceProviderId, VehicleBooking.VehicleBookingStatus status);

    long countByPaymentStatus(VehicleBooking.PaymentStatus paymentStatus);
}
