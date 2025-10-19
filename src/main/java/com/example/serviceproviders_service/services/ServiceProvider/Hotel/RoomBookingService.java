package com.example.serviceproviders_service.services.ServiceProvider.Hotel;

import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.CreateRoomBookingDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.RoomBookingResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.Room;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.RoomBooking;
import com.example.serviceproviders_service.repository.ServiceProvider.Hotel.RoomBookingRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.Hotel.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomBookingService {

    @Autowired
    private RoomBookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    /**
     * Create a new room booking with comprehensive validations
     */
    @Transactional
    public RoomBookingResponseDTO createBooking(CreateRoomBookingDTO dto) {
        // Step 1: Validate room exists
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + dto.getRoomId()));

        // Step 2: Validate room is available
        if (room.getStatus() != Room.RoomStatus.AVAILABLE) {
            throw new IllegalStateException("Room is not available for booking. Current status: " + room.getStatus());
        }

        // Step 3: Validate dates
        validateBookingDates(dto.getCheckInDate(), dto.getCheckOutDate());

        // Step 4: Validate number of guests against room capacity
        if (dto.getNumberOfGuests() > room.getMaxOccupancy()) {
            throw new IllegalArgumentException(
                    String.format("Number of guests (%d) exceeds room maximum occupancy (%d)",
                            dto.getNumberOfGuests(), room.getMaxOccupancy())
            );
        }

        // Step 5: Check for overlapping bookings
        if (bookingRepository.existsOverlappingBooking(dto.getRoomId(), dto.getCheckInDate(), dto.getCheckOutDate())) {
            throw new IllegalStateException(
                    "Room is already booked for the selected dates. Please choose different dates."
            );
        }

        // Step 6: Validate card details
        validateCardDetails(dto.getCardDetails().getExpiryDate());

        // Step 7: Calculate booking details
        long numberOfNights = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
        BigDecimal totalAmount = room.getPrice().multiply(BigDecimal.valueOf(numberOfNights));

        // Step 8: Create booking entity
        RoomBooking booking = new RoomBooking();
        booking.setRoomId(room.getId());
        booking.setServiceProviderId(room.getServiceProviderId());
        booking.setUserId(dto.getUserId());

        // Customer information
        booking.setCustomerName(dto.getCustomerName());
        booking.setCustomerEmail(dto.getCustomerEmail());
        booking.setCustomerPhone(dto.getCustomerPhone());

        // Booking details
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setNumberOfGuests(dto.getNumberOfGuests());
        booking.setNumberOfNights((int) numberOfNights);
        booking.setPricePerNight(room.getPrice());
        booking.setTotalAmount(totalAmount);

        // Card details
        booking.setCardHolderName(dto.getCardDetails().getCardHolderName());
        booking.setCardNumber(dto.getCardDetails().getCardNumber());
        booking.setExpiryDate(dto.getCardDetails().getExpiryDate());
        booking.setCvv(dto.getCardDetails().getCvv());
        booking.setBillingAddress(dto.getCardDetails().getBillingAddress());

        // Special requests
        booking.setSpecialRequests(dto.getSpecialRequests());

        // Set status and timestamp
        booking.setStatus(RoomBooking.BookingStatus.CONFIRMED);
        booking.setConfirmedAt(LocalDateTime.now());

        // Step 9: Save booking
        RoomBooking savedBooking = bookingRepository.save(booking);

        // Step 10: Return response DTO
        return new RoomBookingResponseDTO(savedBooking);
    }

    /**
     * Validate booking dates
     */
    private void validateBookingDates(LocalDate checkInDate, LocalDate checkOutDate) {
        LocalDate today = LocalDate.now();

        // Check-in date must be in the future
        if (checkInDate.isBefore(today)) {
            throw new IllegalArgumentException("Check-in date must be today or in the future");
        }

        // Check-out date must be after check-in date
        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // Maximum booking duration (e.g., 30 days)
        long numberOfNights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (numberOfNights > 30) {
            throw new IllegalArgumentException("Maximum booking duration is 30 nights");
        }

        // Minimum booking duration (at least 1 night)
        if (numberOfNights < 1) {
            throw new IllegalArgumentException("Minimum booking duration is 1 night");
        }
    }

    /**
     * Validate card expiry date
     */
    private void validateCardDetails(String expiryDate) {
        try {
            String[] parts = expiryDate.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);

            YearMonth cardExpiry = YearMonth.of(year, month);
            YearMonth currentMonth = YearMonth.now();

            if (cardExpiry.isBefore(currentMonth)) {
                throw new IllegalArgumentException("Card has expired");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid card expiry date format");
        }
    }

    /**
     * Get booking by ID
     */
    public RoomBookingResponseDTO getBookingById(Long bookingId) {
        RoomBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));
        return new RoomBookingResponseDTO(booking);
    }

    /**
     * Get all bookings for a service provider
     */
    public List<RoomBookingResponseDTO> getBookingsByServiceProvider(Long serviceProviderId) {
        List<RoomBooking> bookings = bookingRepository.findByServiceProviderId(serviceProviderId);
        return bookings.stream()
                .map(RoomBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all bookings for a specific room
     */
    public List<RoomBookingResponseDTO> getBookingsByRoom(Long roomId) {
        List<RoomBooking> bookings = bookingRepository.findByRoomId(roomId);
        return bookings.stream()
                .map(RoomBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get bookings by customer email
     */
    public List<RoomBookingResponseDTO> getBookingsByCustomerEmail(String email) {
        List<RoomBooking> bookings = bookingRepository.findByCustomerEmail(email);
        return bookings.stream()
                .map(RoomBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all bookings for a specific user
     */
    public List<RoomBookingResponseDTO> getBookingsByUserId(Long userId) {
        List<RoomBooking> bookings = bookingRepository.findByUserId(userId);
        return bookings.stream()
                .map(RoomBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get upcoming bookings for a service provider
     */
    public List<RoomBookingResponseDTO> getUpcomingBookings(Long serviceProviderId) {
        List<RoomBooking> bookings = bookingRepository.findUpcomingBookings(serviceProviderId, LocalDate.now());
        return bookings.stream()
                .map(RoomBookingResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Cancel a booking
     */
    @Transactional
    public RoomBookingResponseDTO cancelBooking(Long bookingId, String cancellationReason) {
        RoomBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        // Only allow cancellation if booking is not already cancelled or completed
        if (booking.getStatus() == RoomBooking.BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        if (booking.getStatus() == RoomBooking.BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed booking");
        }

        // Validate cancellation is made before check-in date
        if (LocalDate.now().isAfter(booking.getCheckInDate())) {
            throw new IllegalStateException("Cannot cancel booking after check-in date");
        }

        booking.setStatus(RoomBooking.BookingStatus.CANCELLED);
        booking.setCancellationReason(cancellationReason);
        booking.setCancelledAt(LocalDateTime.now());

        RoomBooking updatedBooking = bookingRepository.save(booking);
        return new RoomBookingResponseDTO(updatedBooking);
    }

    /**
     * Update booking status (for check-in/check-out)
     */
    @Transactional
    public RoomBookingResponseDTO updateBookingStatus(Long bookingId, RoomBooking.BookingStatus newStatus) {
        RoomBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        // Validate status transition
        validateStatusTransition(booking.getStatus(), newStatus);

        booking.setStatus(newStatus);
        RoomBooking updatedBooking = bookingRepository.save(booking);
        return new RoomBookingResponseDTO(updatedBooking);
    }

    /**
     * Validate status transitions
     */
    private void validateStatusTransition(RoomBooking.BookingStatus currentStatus, RoomBooking.BookingStatus newStatus) {
        // Define valid transitions
        switch (currentStatus) {
            case PENDING:
                if (newStatus != RoomBooking.BookingStatus.CONFIRMED && newStatus != RoomBooking.BookingStatus.CANCELLED) {
                    throw new IllegalStateException("PENDING bookings can only be CONFIRMED or CANCELLED");
                }
                break;
            case CONFIRMED:
                if (newStatus != RoomBooking.BookingStatus.CHECKED_IN && newStatus != RoomBooking.BookingStatus.CANCELLED) {
                    throw new IllegalStateException("CONFIRMED bookings can only be CHECKED_IN or CANCELLED");
                }
                break;
            case CHECKED_IN:
                if (newStatus != RoomBooking.BookingStatus.CHECKED_OUT) {
                    throw new IllegalStateException("CHECKED_IN bookings can only be CHECKED_OUT");
                }
                break;
            case CHECKED_OUT:
                if (newStatus != RoomBooking.BookingStatus.COMPLETED) {
                    throw new IllegalStateException("CHECKED_OUT bookings can only be COMPLETED");
                }
                break;
            case CANCELLED:
            case COMPLETED:
                throw new IllegalStateException("Cannot change status of " + currentStatus + " bookings");
        }
    }

    /**
     * Check room availability for specific dates
     */
    public boolean isRoomAvailable(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        return !bookingRepository.existsOverlappingBooking(roomId, checkInDate, checkOutDate);
    }
}
