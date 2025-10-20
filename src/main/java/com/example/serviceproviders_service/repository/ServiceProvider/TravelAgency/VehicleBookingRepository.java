package com.example.serviceproviders_service.repository.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.VehicleBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VehicleBookingRepository extends JpaRepository<VehicleBooking, Long> {

    // Find all bookings for a specific vehicle
    List<VehicleBooking> findByVehicleId(Long vehicleId);

    // Find all bookings for a travel agency (service provider)
    List<VehicleBooking> findByServiceProviderId(Long serviceProviderId);

    // Find bookings by customer email
    List<VehicleBooking> findByCustomerEmail(String customerEmail);

    // Find all bookings for a specific user
    List<VehicleBooking> findByUserId(Long userId);

    // Find approved and completed bookings for a specific user (for booking history)
    @Query("SELECT b FROM VehicleBooking b WHERE b.userId = :userId " +
           "AND b.status IN ('APPROVED', 'COMPLETED') " +
           "ORDER BY b.createdAt DESC")
    List<VehicleBooking> findApprovedBookingsByUserId(@Param("userId") Long userId);

    // Find approved and completed bookings for a service provider (for booking history)
    @Query("SELECT b FROM VehicleBooking b WHERE b.serviceProviderId = :serviceProviderId " +
           "AND b.status IN ('APPROVED', 'COMPLETED') " +
           "ORDER BY b.createdAt DESC")
    List<VehicleBooking> findApprovedBookingsByServiceProviderId(@Param("serviceProviderId") Long serviceProviderId);

    // Find bookings by status
    List<VehicleBooking> findByStatus(VehicleBooking.BookingStatus status);

    // Find bookings for a specific vehicle and status
    List<VehicleBooking> findByVehicleIdAndStatus(Long vehicleId, VehicleBooking.BookingStatus status);

    // Find pending approval bookings for a travel agency
    @Query("SELECT b FROM VehicleBooking b WHERE b.serviceProviderId = :serviceProviderId " +
           "AND b.status = 'PENDING_APPROVAL' " +
           "ORDER BY b.createdAt ASC")
    List<VehicleBooking> findPendingApprovalBookings(@Param("serviceProviderId") Long serviceProviderId);

    // Find approved bookings for a travel agency
    @Query("SELECT b FROM VehicleBooking b WHERE b.serviceProviderId = :serviceProviderId " +
           "AND b.status = 'APPROVED' " +
           "ORDER BY b.startDate ASC")
    List<VehicleBooking> findApprovedBookings(@Param("serviceProviderId") Long serviceProviderId);

    // Find upcoming bookings for a travel agency (approved bookings with future dates)
    @Query("SELECT b FROM VehicleBooking b WHERE b.serviceProviderId = :serviceProviderId " +
           "AND b.status = 'APPROVED' " +
           "AND b.startDate >= :today " +
           "ORDER BY b.startDate ASC")
    List<VehicleBooking> findUpcomingBookings(@Param("serviceProviderId") Long serviceProviderId,
                                              @Param("today") LocalDate today);

    // Check if vehicle is available for a specific date range
    @Query("SELECT b FROM VehicleBooking b WHERE b.vehicleId = :vehicleId " +
           "AND b.status IN ('PENDING_APPROVAL', 'APPROVED') " +
           "AND ((b.startDate <= :endDate AND b.endDate >= :startDate))")
    List<VehicleBooking> findConflictingBookings(@Param("vehicleId") Long vehicleId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    // Count bookings by service provider and status
    @Query("SELECT COUNT(b) FROM VehicleBooking b WHERE b.serviceProviderId = :serviceProviderId " +
           "AND b.status = :status")
    Long countByServiceProviderIdAndStatus(@Param("serviceProviderId") Long serviceProviderId,
                                           @Param("status") VehicleBooking.BookingStatus status);
}
