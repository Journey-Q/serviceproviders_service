package com.example.serviceproviders_service.services.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.CreateVehicleBookingDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.VehicleBookingResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.Vehicle;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.VehicleBooking;
import com.example.serviceproviders_service.repository.ServiceProvider.TravelAgency.VehicleBookingRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TravelAgency.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleBookingService {

    @Autowired
    private VehicleBookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     * Create a new vehicle booking (requires travel agency approval)
     * No payment required at booking time
     */
    @Transactional
    public VehicleBookingResponseDTO createBooking(CreateVehicleBookingDTO dto) {
        // Step 1: Validate vehicle exists
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + dto.getVehicleId()));

        // Step 2: Validate vehicle is available
        if (vehicle.getStatus() != Vehicle.VehicleStatus.AVAILABLE) {
            throw new IllegalStateException("Vehicle is not available for booking. Current status: " + vehicle.getStatus());
        }

        // Step 3: Validate booking dates
        validateBookingDates(dto.getStartDate(), dto.getEndDate());

        // Step 4: Check if vehicle is available for the selected date range
        List<VehicleBooking> conflictingBookings = bookingRepository.findConflictingBookings(
                dto.getVehicleId(), dto.getStartDate(), dto.getEndDate());

        if (!conflictingBookings.isEmpty()) {
            throw new IllegalStateException(
                    String.format("Vehicle is not available for the selected dates (%s to %s). " +
                                    "There are %d conflicting bookings.",
                            dto.getStartDate(), dto.getEndDate(), conflictingBookings.size())
            );
        }

        // Step 5: Calculate estimated total amount
        BigDecimal pricePerKm = dto.getWithAC() ? vehicle.getPricePerKmWithAC() : vehicle.getPricePerKmWithoutAC();
        BigDecimal estimatedTotal = pricePerKm.multiply(BigDecimal.valueOf(dto.getEstimatedKilometers()));

        // Step 6: Create booking entity
        VehicleBooking booking = new VehicleBooking();
        booking.setVehicleId(vehicle.getId());
        booking.setServiceProviderId(vehicle.getServiceProviderId());
        booking.setUserId(dto.getUserId());

        // Customer information
        booking.setCustomerName(dto.getCustomerName());
        booking.setCustomerEmail(dto.getCustomerEmail());
        booking.setCustomerPhone(dto.getCustomerPhone());

        // Booking details
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setPickupLocation(dto.getPickupLocation());
        booking.setDropoffLocation(dto.getDropoffLocation());
        booking.setEstimatedKilometers(dto.getEstimatedKilometers());
        booking.setWithAC(dto.getWithAC());
        booking.setPricePerKm(pricePerKm);
        booking.setEstimatedTotalAmount(estimatedTotal);

        // Special requests
        booking.setSpecialRequests(dto.getSpecialRequests());

        // Set status as PENDING_APPROVAL (waiting for travel agency to accept)
        booking.setStatus(VehicleBooking.BookingStatus.PENDING_APPROVAL);

        // Step 7: Save booking
        VehicleBooking savedBooking = bookingRepository.save(booking);

        // Step 8: Return response DTO
        return new VehicleBookingResponseDTO(savedBooking);
    }

    /**
     * Validate booking dates
     */
    private void validateBookingDates(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();

        // Start date must be in the future
        if (startDate.isBefore(today)) {
            throw new IllegalArgumentException("Start date must be in the future");
        }

        // End date must be after start date
        if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        // Booking should not be too far in the future (e.g., 1 year)
        LocalDate maxDate = today.plusYears(1);
        if (startDate.isAfter(maxDate)) {
            throw new IllegalArgumentException("Start date cannot be more than 1 year in the future");
        }

        // Booking duration should not exceed 90 days
        long bookingDuration = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        if (bookingDuration > 90) {
            throw new IllegalArgumentException("Booking duration cannot exceed 90 days");
        }
    }

    /**
     * Travel Agency approves a booking
     */
    @Transactional
    public VehicleBookingResponseDTO approveBooking(Long bookingId, Long travelAgencyId) {
        VehicleBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        // Verify the travel agency owns this booking
        if (!booking.getServiceProviderId().equals(travelAgencyId)) {
            throw new IllegalStateException("You are not authorized to approve this booking");
        }

        // Only PENDING_APPROVAL bookings can be approved
        if (booking.getStatus() != VehicleBooking.BookingStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Only pending bookings can be approved. Current status: " + booking.getStatus());
        }

        // Verify start date hasn't passed
        if (booking.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot approve booking for past start date");
        }

        booking.setStatus(VehicleBooking.BookingStatus.APPROVED);
        booking.setApprovedAt(LocalDateTime.now());

        VehicleBooking updatedBooking = bookingRepository.save(booking);
        return new VehicleBookingResponseDTO(updatedBooking);
    }

    /**
     * Travel Agency rejects a booking
     */
    @Transactional
    public VehicleBookingResponseDTO rejectBooking(Long bookingId, Long travelAgencyId, String rejectionReason) {
        VehicleBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        // Verify the travel agency owns this booking
        if (!booking.getServiceProviderId().equals(travelAgencyId)) {
            throw new IllegalStateException("You are not authorized to reject this booking");
        }

        // Only PENDING_APPROVAL bookings can be rejected
        if (booking.getStatus() != VehicleBooking.BookingStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Only pending bookings can be rejected. Current status: " + booking.getStatus());
        }

        booking.setStatus(VehicleBooking.BookingStatus.REJECTED);
        booking.setRejectionReason(rejectionReason != null ? rejectionReason : "Travel agency rejected the booking");
        booking.setRejectedAt(LocalDateTime.now());

        VehicleBooking updatedBooking = bookingRepository.save(booking);
        return new VehicleBookingResponseDTO(updatedBooking);
    }

    /**
     * Get booking by ID
     */
    public VehicleBookingResponseDTO getBookingById(Long bookingId) {
        VehicleBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));
        return new VehicleBookingResponseDTO(booking);
    }

    /**
     * Get all bookings for a travel agency
     */
    public List<VehicleBookingResponseDTO> getBookingsByTravelAgency(Long travelAgencyId) {
        List<VehicleBooking> bookings = bookingRepository.findByServiceProviderId(travelAgencyId);
        return bookings.stream()
                .map(VehicleBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all bookings for a specific vehicle
     */
    public List<VehicleBookingResponseDTO> getBookingsByVehicle(Long vehicleId) {
        List<VehicleBooking> bookings = bookingRepository.findByVehicleId(vehicleId);
        return bookings.stream()
                .map(VehicleBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get bookings by customer email
     */
    public List<VehicleBookingResponseDTO> getBookingsByCustomerEmail(String email) {
        List<VehicleBooking> bookings = bookingRepository.findByCustomerEmail(email);
        return bookings.stream()
                .map(VehicleBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all bookings for a specific user
     */
    public List<VehicleBookingResponseDTO> getBookingsByUserId(Long userId) {
        List<VehicleBooking> bookings = bookingRepository.findByUserId(userId);
        return bookings.stream()
                .map(VehicleBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get pending approval bookings for a travel agency
     */
    public List<VehicleBookingResponseDTO> getPendingApprovalBookings(Long travelAgencyId) {
        List<VehicleBooking> bookings = bookingRepository.findPendingApprovalBookings(travelAgencyId);
        return bookings.stream()
                .map(VehicleBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get approved bookings for a travel agency
     */
    public List<VehicleBookingResponseDTO> getApprovedBookings(Long travelAgencyId) {
        List<VehicleBooking> bookings = bookingRepository.findApprovedBookings(travelAgencyId);
        return bookings.stream()
                .map(VehicleBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get upcoming bookings for a travel agency
     */
    public List<VehicleBookingResponseDTO> getUpcomingBookings(Long travelAgencyId) {
        List<VehicleBooking> bookings = bookingRepository.findUpcomingBookings(travelAgencyId, LocalDate.now());
        return bookings.stream()
                .map(VehicleBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Cancel a booking (by customer)
     */
    @Transactional
    public VehicleBookingResponseDTO cancelBooking(Long bookingId, String cancellationReason) {
        VehicleBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        // Only PENDING_APPROVAL and APPROVED bookings can be cancelled by customer
        if (booking.getStatus() == VehicleBooking.BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        if (booking.getStatus() == VehicleBooking.BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed booking");
        }
        if (booking.getStatus() == VehicleBooking.BookingStatus.REJECTED) {
            throw new IllegalStateException("Cannot cancel a rejected booking");
        }

        // Validate cancellation is made before start date
        if (LocalDate.now().isAfter(booking.getStartDate())) {
            throw new IllegalStateException("Cannot cancel booking after start date");
        }

        booking.setStatus(VehicleBooking.BookingStatus.CANCELLED);
        booking.setCancellationReason(cancellationReason != null ? cancellationReason : "Customer cancelled the booking");
        booking.setCancelledAt(LocalDateTime.now());

        VehicleBooking updatedBooking = bookingRepository.save(booking);
        return new VehicleBookingResponseDTO(updatedBooking);
    }

    /**
     * Mark booking as completed (by travel agency after trip)
     */
    @Transactional
    public VehicleBookingResponseDTO completeBooking(Long bookingId, Long travelAgencyId) {
        VehicleBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        // Verify the travel agency owns this booking
        if (!booking.getServiceProviderId().equals(travelAgencyId)) {
            throw new IllegalStateException("You are not authorized to complete this booking");
        }

        // Only APPROVED bookings can be completed
        if (booking.getStatus() != VehicleBooking.BookingStatus.APPROVED) {
            throw new IllegalStateException("Only approved bookings can be completed");
        }

        // End date should be today or in the past
        if (booking.getEndDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("Cannot complete booking before end date");
        }

        booking.setStatus(VehicleBooking.BookingStatus.COMPLETED);
        booking.setCompletedAt(LocalDateTime.now());

        VehicleBooking updatedBooking = bookingRepository.save(booking);
        return new VehicleBookingResponseDTO(updatedBooking);
    }

    /**
     * Check if vehicle is available for a date range
     */
    public boolean isVehicleAvailable(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        List<VehicleBooking> conflictingBookings = bookingRepository.findConflictingBookings(
                vehicleId, startDate, endDate);
        return conflictingBookings.isEmpty();
    }
}
