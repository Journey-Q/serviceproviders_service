package com.example.serviceproviders_service.services.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.CreateTourBookingDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.TourBookingResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.Tour;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourBooking;
import com.example.serviceproviders_service.repository.ServiceProvider.TourGuide.TourBookingRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TourGuide.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourBookingService {

    @Autowired
    private TourBookingRepository bookingRepository;

    @Autowired
    private TourRepository tourRepository;

    /**
     * Create a new tour booking (requires tour guide approval)
     */
    @Transactional
    public TourBookingResponseDTO createBooking(CreateTourBookingDTO dto) {
        // Step 1: Validate tour exists
        Tour tour = tourRepository.findById(dto.getTourId())
                .orElseThrow(() -> new IllegalArgumentException("Tour not found with ID: " + dto.getTourId()));

        // Step 2: Validate tour is available
        if (tour.getStatus() != Tour.TourStatus.AVAILABLE) {
            throw new IllegalStateException("Tour is not available for booking. Current status: " + tour.getStatus());
        }

        // Step 3: Validate tour date is in the future
        validateTourDate(dto.getTourDate());

        // Step 4: Validate number of people
        validateNumberOfPeople(dto.getNumberOfPeople(), tour);

        // Step 5: Check if tour capacity is not exceeded for the selected date
        Integer totalBookedPeople = bookingRepository.getTotalPeopleBookedForTourDate(
                dto.getTourId(), dto.getTourDate());
        int availableCapacity = tour.getMaxPeople() - totalBookedPeople;

        if (dto.getNumberOfPeople() > availableCapacity) {
            throw new IllegalStateException(
                    String.format("Not enough capacity available. Requested: %d, Available: %d",
                            dto.getNumberOfPeople(), availableCapacity)
            );
        }

        // Step 6: Calculate total amount
        BigDecimal totalAmount = tour.getPricePerPerson().multiply(BigDecimal.valueOf(dto.getNumberOfPeople()));

        // Step 7: Create booking entity
        TourBooking booking = new TourBooking();
        booking.setTourId(tour.getId());
        booking.setServiceProviderId(tour.getServiceProviderId());
        booking.setUserId(dto.getUserId());

        // Customer information
        booking.setCustomerName(dto.getCustomerName());
        booking.setCustomerEmail(dto.getCustomerEmail());
        booking.setCustomerPhone(dto.getCustomerPhone());

        // Booking details
        booking.setTourDate(dto.getTourDate());
        booking.setNumberOfPeople(dto.getNumberOfPeople());
        booking.setPricePerPerson(tour.getPricePerPerson());
        booking.setTotalAmount(totalAmount);

        // Special requests
        booking.setSpecialRequests(dto.getSpecialRequests());

        // Set status as PENDING_APPROVAL (waiting for tour guide to accept)
        booking.setStatus(TourBooking.BookingStatus.PENDING_APPROVAL);

        // Step 9: Save booking
        TourBooking savedBooking = bookingRepository.save(booking);

        // Step 10: Return response DTO
        return new TourBookingResponseDTO(savedBooking);
    }

    /**
     * Validate tour date
     */
    private void validateTourDate(LocalDate tourDate) {
        LocalDate today = LocalDate.now();

        // Tour date must be in the future
        if (tourDate.isBefore(today)) {
            throw new IllegalArgumentException("Tour date must be in the future");
        }

        // Tour date should not be too far in the future (e.g., 1 year)
        LocalDate maxDate = today.plusYears(1);
        if (tourDate.isAfter(maxDate)) {
            throw new IllegalArgumentException("Tour date cannot be more than 1 year in the future");
        }
    }

    /**
     * Validate number of people against tour capacity
     */
    private void validateNumberOfPeople(Integer numberOfPeople, Tour tour) {
        if (numberOfPeople < tour.getMinPeople()) {
            throw new IllegalArgumentException(
                    String.format("Minimum %d people required for this tour", tour.getMinPeople())
            );
        }

        if (numberOfPeople > tour.getMaxPeople()) {
            throw new IllegalArgumentException(
                    String.format("Maximum %d people allowed for this tour", tour.getMaxPeople())
            );
        }
    }

    /**
     * Tour Guide approves a booking
     */
    @Transactional
    public TourBookingResponseDTO approveBooking(Long bookingId, Long tourGuideId) {
        TourBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        // Verify the tour guide owns this booking
        if (!booking.getServiceProviderId().equals(tourGuideId)) {
            throw new IllegalStateException("You are not authorized to approve this booking");
        }

        // Only PENDING_APPROVAL bookings can be approved
        if (booking.getStatus() != TourBooking.BookingStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Only pending bookings can be approved. Current status: " + booking.getStatus());
        }

        // Verify tour date hasn't passed
        if (booking.getTourDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot approve booking for past tour date");
        }

        booking.setStatus(TourBooking.BookingStatus.APPROVED);
        booking.setApprovedAt(LocalDateTime.now());

        TourBooking updatedBooking = bookingRepository.save(booking);
        return new TourBookingResponseDTO(updatedBooking);
    }

    /**
     * Tour Guide rejects a booking
     */
    @Transactional
    public TourBookingResponseDTO rejectBooking(Long bookingId, Long tourGuideId, String rejectionReason) {
        TourBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        // Verify the tour guide owns this booking
        if (!booking.getServiceProviderId().equals(tourGuideId)) {
            throw new IllegalStateException("You are not authorized to reject this booking");
        }

        // Only PENDING_APPROVAL bookings can be rejected
        if (booking.getStatus() != TourBooking.BookingStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Only pending bookings can be rejected. Current status: " + booking.getStatus());
        }

        booking.setStatus(TourBooking.BookingStatus.REJECTED);
        booking.setRejectionReason(rejectionReason != null ? rejectionReason : "Tour guide rejected the booking");
        booking.setRejectedAt(LocalDateTime.now());

        TourBooking updatedBooking = bookingRepository.save(booking);
        return new TourBookingResponseDTO(updatedBooking);
    }

    /**
     * Get booking by ID
     */
    public TourBookingResponseDTO getBookingById(Long bookingId) {
        TourBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));
        return new TourBookingResponseDTO(booking);
    }

    /**
     * Get all bookings for a tour guide
     */
    public List<TourBookingResponseDTO> getBookingsByTourGuide(Long tourGuideId) {
        List<TourBooking> bookings = bookingRepository.findByServiceProviderId(tourGuideId);
        return bookings.stream()
                .map(TourBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all bookings for a specific tour
     */
    public List<TourBookingResponseDTO> getBookingsByTour(Long tourId) {
        List<TourBooking> bookings = bookingRepository.findByTourId(tourId);
        return bookings.stream()
                .map(TourBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get bookings by customer email
     */
    public List<TourBookingResponseDTO> getBookingsByCustomerEmail(String email) {
        List<TourBooking> bookings = bookingRepository.findByCustomerEmail(email);
        return bookings.stream()
                .map(TourBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all bookings for a specific user
     */
    public List<TourBookingResponseDTO> getBookingsByUserId(Long userId) {
        List<TourBooking> bookings = bookingRepository.findByUserId(userId);
        return bookings.stream()
                .map(TourBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get pending approval bookings for a tour guide
     */
    public List<TourBookingResponseDTO> getPendingApprovalBookings(Long tourGuideId) {
        List<TourBooking> bookings = bookingRepository.findPendingApprovalBookings(tourGuideId);
        return bookings.stream()
                .map(TourBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get approved bookings for a tour guide
     */
    public List<TourBookingResponseDTO> getApprovedBookings(Long tourGuideId) {
        List<TourBooking> bookings = bookingRepository.findApprovedBookings(tourGuideId);
        return bookings.stream()
                .map(TourBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get upcoming tours for a tour guide
     */
    public List<TourBookingResponseDTO> getUpcomingTours(Long tourGuideId) {
        List<TourBooking> bookings = bookingRepository.findUpcomingTours(tourGuideId, LocalDate.now());
        return bookings.stream()
                .map(TourBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Cancel a booking (by customer)
     */
    @Transactional
    public TourBookingResponseDTO cancelBooking(Long bookingId, String cancellationReason) {
        TourBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        // Only PENDING_APPROVAL and APPROVED bookings can be cancelled by customer
        if (booking.getStatus() == TourBooking.BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        if (booking.getStatus() == TourBooking.BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed booking");
        }
        if (booking.getStatus() == TourBooking.BookingStatus.REJECTED) {
            throw new IllegalStateException("Cannot cancel a rejected booking");
        }

        // Validate cancellation is made before tour date
        if (LocalDate.now().isAfter(booking.getTourDate())) {
            throw new IllegalStateException("Cannot cancel booking after tour date");
        }

        booking.setStatus(TourBooking.BookingStatus.CANCELLED);
        booking.setCancellationReason(cancellationReason != null ? cancellationReason : "Customer cancelled the booking");
        booking.setCancelledAt(LocalDateTime.now());

        TourBooking updatedBooking = bookingRepository.save(booking);
        return new TourBookingResponseDTO(updatedBooking);
    }

    /**
     * Mark booking as completed (by tour guide after tour)
     */
    @Transactional
    public TourBookingResponseDTO completeBooking(Long bookingId, Long tourGuideId) {
        TourBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        // Verify the tour guide owns this booking
        if (!booking.getServiceProviderId().equals(tourGuideId)) {
            throw new IllegalStateException("You are not authorized to complete this booking");
        }

        // Only APPROVED bookings can be completed
        if (booking.getStatus() != TourBooking.BookingStatus.APPROVED) {
            throw new IllegalStateException("Only approved bookings can be completed");
        }

        // Tour date should be today or in the past
        if (booking.getTourDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("Cannot complete booking before tour date");
        }

        booking.setStatus(TourBooking.BookingStatus.COMPLETED);
        booking.setCompletedAt(LocalDateTime.now());

        TourBooking updatedBooking = bookingRepository.save(booking);
        return new TourBookingResponseDTO(updatedBooking);
    }

    /**
     * Get available capacity for a tour on a specific date
     */
    public int getAvailableCapacity(Long tourId, LocalDate tourDate) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new IllegalArgumentException("Tour not found with ID: " + tourId));

        Integer totalBookedPeople = bookingRepository.getTotalPeopleBookedForTourDate(tourId, tourDate);
        return tour.getMaxPeople() - totalBookedPeople;
    }
}
