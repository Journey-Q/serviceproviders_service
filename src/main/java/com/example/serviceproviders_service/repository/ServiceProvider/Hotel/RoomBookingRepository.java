package com.example.serviceproviders_service.repository.ServiceProvider.Hotel;

import com.example.serviceproviders_service.entity.serviceProvider.Hotel.RoomBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {

    // Find all bookings for a specific room
    List<RoomBooking> findByRoomId(Long roomId);

    // Find all bookings for a service provider
    List<RoomBooking> findByServiceProviderId(Long serviceProviderId);

    // Find bookings by customer email
    List<RoomBooking> findByCustomerEmail(String customerEmail);

    // Find all bookings for a specific user
    List<RoomBooking> findByUserId(Long userId);

    // Find bookings by status
    List<RoomBooking> findByStatus(RoomBooking.BookingStatus status);

    // Find bookings for a specific room and status
    List<RoomBooking> findByRoomIdAndStatus(Long roomId, RoomBooking.BookingStatus status);

    // Check for overlapping bookings for a room (critical for validation)
    @Query("SELECT COUNT(b) > 0 FROM RoomBooking b WHERE b.roomId = :roomId " +
           "AND b.status NOT IN ('CANCELLED', 'COMPLETED') " +
           "AND ((b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate))")
    boolean existsOverlappingBooking(@Param("roomId") Long roomId,
                                     @Param("checkInDate") LocalDate checkInDate,
                                     @Param("checkOutDate") LocalDate checkOutDate);

    // Get all active bookings for a room within a date range
    @Query("SELECT b FROM RoomBooking b WHERE b.roomId = :roomId " +
           "AND b.status NOT IN ('CANCELLED', 'COMPLETED') " +
           "AND ((b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate))")
    List<RoomBooking> findOverlappingBookings(@Param("roomId") Long roomId,
                                              @Param("checkInDate") LocalDate checkInDate,
                                              @Param("checkOutDate") LocalDate checkOutDate);

    // Find upcoming bookings for a service provider
    @Query("SELECT b FROM RoomBooking b WHERE b.serviceProviderId = :serviceProviderId " +
           "AND b.checkInDate >= :today " +
           "AND b.status IN ('CONFIRMED', 'PENDING') " +
           "ORDER BY b.checkInDate ASC")
    List<RoomBooking> findUpcomingBookings(@Param("serviceProviderId") Long serviceProviderId,
                                           @Param("today") LocalDate today);
}
