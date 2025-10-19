package com.example.serviceproviders_service.repository.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TourBookingRepository extends JpaRepository<TourBooking, Long> {

    // Find all bookings for a specific tour
    List<TourBooking> findByTourId(Long tourId);

    // Find all bookings for a tour guide (service provider)
    List<TourBooking> findByServiceProviderId(Long serviceProviderId);

    // Find bookings by customer email
    List<TourBooking> findByCustomerEmail(String customerEmail);

    // Find all bookings for a specific user
    List<TourBooking> findByUserId(Long userId);

    // Find bookings by status
    List<TourBooking> findByStatus(TourBooking.BookingStatus status);

    // Find bookings for a specific tour and status
    List<TourBooking> findByTourIdAndStatus(Long tourId, TourBooking.BookingStatus status);

    // Find pending approval bookings for a tour guide
    @Query("SELECT b FROM TourBooking b WHERE b.serviceProviderId = :serviceProviderId " +
           "AND b.status = 'PENDING_APPROVAL' " +
           "ORDER BY b.createdAt ASC")
    List<TourBooking> findPendingApprovalBookings(@Param("serviceProviderId") Long serviceProviderId);

    // Find approved bookings for a tour guide
    @Query("SELECT b FROM TourBooking b WHERE b.serviceProviderId = :serviceProviderId " +
           "AND b.status = 'APPROVED' " +
           "ORDER BY b.tourDate ASC")
    List<TourBooking> findApprovedBookings(@Param("serviceProviderId") Long serviceProviderId);

    // Find upcoming tours for a tour guide (approved bookings with future dates)
    @Query("SELECT b FROM TourBooking b WHERE b.serviceProviderId = :serviceProviderId " +
           "AND b.status = 'APPROVED' " +
           "AND b.tourDate >= :today " +
           "ORDER BY b.tourDate ASC")
    List<TourBooking> findUpcomingTours(@Param("serviceProviderId") Long serviceProviderId,
                                        @Param("today") LocalDate today);

    // Get total number of people booked for a specific tour on a specific date
    @Query("SELECT COALESCE(SUM(b.numberOfPeople), 0) FROM TourBooking b " +
           "WHERE b.tourId = :tourId " +
           "AND b.tourDate = :tourDate " +
           "AND b.status IN ('PENDING_APPROVAL', 'APPROVED')")
    Integer getTotalPeopleBookedForTourDate(@Param("tourId") Long tourId,
                                            @Param("tourDate") LocalDate tourDate);

    // Find all bookings for a specific tour on a specific date
    @Query("SELECT b FROM TourBooking b WHERE b.tourId = :tourId " +
           "AND b.tourDate = :tourDate " +
           "AND b.status IN ('PENDING_APPROVAL', 'APPROVED') " +
           "ORDER BY b.createdAt ASC")
    List<TourBooking> findBookingsForTourDate(@Param("tourId") Long tourId,
                                              @Param("tourDate") LocalDate tourDate);

    // Count bookings by service provider and status
    @Query("SELECT COUNT(b) FROM TourBooking b WHERE b.serviceProviderId = :serviceProviderId " +
           "AND b.status = :status")
    Long countByServiceProviderIdAndStatus(@Param("serviceProviderId") Long serviceProviderId,
                                           @Param("status") TourBooking.BookingStatus status);
}
