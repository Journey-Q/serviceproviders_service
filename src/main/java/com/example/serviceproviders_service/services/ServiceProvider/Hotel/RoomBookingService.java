package com.example.serviceproviders_service.services.ServiceProvider.Hotel;


import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.RoomBookingResponseDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.CreateRoomBookingDTO;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.RoomBooking;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.Room;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.repository.ServiceProvider.Hotel.RoomBookingRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.Hotel.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomBookingService {

    @Autowired
    private RoomBookingRepository roomBookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    private static final BigDecimal SERVICE_CHARGE_RATE = new BigDecimal("0.10"); // 10%
    private static final BigDecimal TAX_RATE = new BigDecimal("0.12"); // 12%

    @Transactional
    public RoomBookingResponseDTO createRoomBooking(CreateRoomBookingDTO dto) {
        validateCreateRoomBookingDTO(dto);

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new BadRequestException("Room not found with id: " + dto.getRoomId()));

        if (!room.getStatus().equals(Room.RoomStatus.AVAILABLE)) {
            throw new BadRequestException("Room is not available for booking");
        }

        // Check for conflicting bookings
        List<RoomBooking> conflictingBookings = roomBookingRepository.findConflictingRoomBookings(
                dto.getRoomId(), dto.getCheckInDate(), dto.getCheckOutDate());

        if (!conflictingBookings.isEmpty()) {
            throw new BadRequestException("Room is not available for the selected dates");
        }

        int numberOfNights = (int) ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
        if (numberOfNights < 1) {
            numberOfNights = 1;
        }

        BigDecimal subtotal = dto.getRoomPricePerNight().multiply(new BigDecimal(numberOfNights));
        BigDecimal serviceCharge = subtotal.multiply(SERVICE_CHARGE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxes = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(serviceCharge).add(taxes);

        RoomBooking roomBooking = new RoomBooking();
        roomBooking.setRoomId(dto.getRoomId());
        roomBooking.setServiceProviderId(room.getServiceProviderId());
        roomBooking.setUserId(dto.getUserId());
        roomBooking.setGuestName(dto.getGuestName());
        roomBooking.setGuestEmail(dto.getGuestEmail());
        roomBooking.setGuestPhone(dto.getGuestPhone());
        roomBooking.setSpecialRequests(dto.getSpecialRequests());
        roomBooking.setCheckInDate(dto.getCheckInDate());
        roomBooking.setCheckOutDate(dto.getCheckOutDate());
        roomBooking.setNumberOfGuests(dto.getNumberOfGuests());
        roomBooking.setNumberOfNights(numberOfNights);
        roomBooking.setSubtotal(subtotal);
        roomBooking.setServiceCharge(serviceCharge);
        roomBooking.setTaxes(taxes);
        roomBooking.setTotalAmount(totalAmount);
        roomBooking.setStatus(RoomBooking.RoomBookingStatus.PENDING);
        roomBooking.setCurrency(dto.getCurrency() != null ? dto.getCurrency().toUpperCase() : "USD");
        roomBooking.setPaymentStatus(RoomBooking.PaymentStatus.PENDING);
        roomBooking.setPaymentMethod(RoomBooking.PaymentMethod.STRIPE_CARD);

        RoomBooking savedRoomBooking = roomBookingRepository.save(roomBooking);
        return RoomBookingResponseDTO.fromEntity(savedRoomBooking);
    }

    @Transactional
    public RoomBookingResponseDTO updatePaymentDetails(Long bookingId, String stripeSessionId,
                                                       String stripePaymentIntentId,
                                                       RoomBooking.PaymentStatus paymentStatus) {
        RoomBooking roomBooking = roomBookingRepository.findById(bookingId)
                .orElseThrow(() -> new BadRequestException("Room booking not found with id: " + bookingId));

        roomBooking.setStripeSessionId(stripeSessionId);
        roomBooking.setStripePaymentIntentId(stripePaymentIntentId);
        roomBooking.setPaymentStatus(paymentStatus);

        if (paymentStatus == RoomBooking.PaymentStatus.SUCCEEDED) {
            roomBooking.setPaidAt(LocalDateTime.now());
            roomBooking.setStatus(RoomBooking.RoomBookingStatus.CONFIRMED);
        }

        RoomBooking updatedBooking = roomBookingRepository.save(roomBooking);
        return RoomBookingResponseDTO.fromEntity(updatedBooking);
    }

    @Transactional
    public RoomBookingResponseDTO updatePaymentFailure(String stripeSessionId, String failureReason) {
        RoomBooking roomBooking = roomBookingRepository.findByStripeSessionId(stripeSessionId)
                .orElseThrow(() -> new BadRequestException("Room booking not found with session ID: " + stripeSessionId));

        roomBooking.setPaymentFailureReason(failureReason);
        roomBooking.setPaymentStatus(RoomBooking.PaymentStatus.FAILED);

        RoomBooking updatedBooking = roomBookingRepository.save(roomBooking);
        return RoomBookingResponseDTO.fromEntity(updatedBooking);
    }

    @Transactional(readOnly = true)
    public RoomBookingResponseDTO getRoomBookingById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid room booking ID");
        }
        RoomBooking roomBooking = roomBookingRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Room booking not found with id: " + id));
        return RoomBookingResponseDTO.fromEntity(roomBooking);
    }

    @Transactional(readOnly = true)
    public RoomBookingResponseDTO getRoomBookingByReference(String bookingReference) {
        if (bookingReference == null || bookingReference.trim().isEmpty()) {
            throw new BadRequestException("Booking reference is required");
        }
        RoomBooking roomBooking = roomBookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new BadRequestException("Room booking not found with reference: " + bookingReference));
        return RoomBookingResponseDTO.fromEntity(roomBooking);
    }

    @Transactional(readOnly = true)
    public RoomBookingResponseDTO getRoomBookingByStripeSessionId(String stripeSessionId) {
        if (stripeSessionId == null || stripeSessionId.trim().isEmpty()) {
            throw new BadRequestException("Stripe session ID is required");
        }
        RoomBooking roomBooking = roomBookingRepository.findByStripeSessionId(stripeSessionId)
                .orElseThrow(() -> new BadRequestException("Room booking not found with session ID: " + stripeSessionId));
        return RoomBookingResponseDTO.fromEntity(roomBooking);
    }

    @Transactional(readOnly = true)
    public List<RoomBookingResponseDTO> getAllRoomBookings() {
        return roomBookingRepository.findAll()
                .stream()
                .map(RoomBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomBookingResponseDTO> getRoomBookingsByServiceProviderId(Long serviceProviderId) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }
        return roomBookingRepository.findByServiceProviderId(serviceProviderId)
                .stream()
                .map(RoomBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomBookingResponseDTO> getRoomBookingsByGuestEmail(String guestEmail) {
        if (guestEmail == null || guestEmail.trim().isEmpty()) {
            throw new BadRequestException("Guest email is required");
        }
        return roomBookingRepository.findByGuestEmail(guestEmail)
                .stream()
                .map(RoomBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomBookingResponseDTO> getRoomBookingsByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }
        return roomBookingRepository.findByUserId(userId)
                .stream()
                .map(RoomBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomBookingResponseDTO> getRoomBookingsByPaymentStatus(RoomBooking.PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            throw new BadRequestException("Payment status is required");
        }
        return roomBookingRepository.findByPaymentStatus(paymentStatus)
                .stream()
                .map(RoomBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomBookingResponseDTO> getSuccessfulPayments() {
        return roomBookingRepository.findByPaymentStatus(RoomBooking.PaymentStatus.SUCCEEDED)
                .stream()
                .map(RoomBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomBookingResponseDTO> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Start date and end date are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        return roomBookingRepository.findPaymentsByDateRange(startDate, endDate)
                .stream()
                .map(RoomBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomBookingResponseDTO updateRoomBookingStatus(Long id, RoomBooking.RoomBookingStatus status) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid room booking ID");
        }
        if (status == null) {
            throw new BadRequestException("Room booking status is required");
        }

        RoomBooking roomBooking = roomBookingRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Room booking not found with id: " + id));

        roomBooking.setStatus(status);
        RoomBooking updatedRoomBooking = roomBookingRepository.save(roomBooking);
        return RoomBookingResponseDTO.fromEntity(updatedRoomBooking);
    }

    @Transactional
    public boolean cancelRoomBooking(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid room booking ID");
        }

        RoomBooking roomBooking = roomBookingRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Room booking not found with id: " + id));

        if (roomBooking.getStatus().equals(RoomBooking.RoomBookingStatus.CHECKED_IN) ||
                roomBooking.getStatus().equals(RoomBooking.RoomBookingStatus.CHECKED_OUT)) {
            throw new BadRequestException("Cannot cancel room booking that is already checked in or completed");
        }

        roomBooking.setStatus(RoomBooking.RoomBookingStatus.CANCELLED);
        roomBooking.setPaymentStatus(RoomBooking.PaymentStatus.CANCELLED);
        roomBookingRepository.save(roomBooking);
        return true;
    }

    @Transactional
    public RoomBookingResponseDTO refundPayment(Long bookingId, String refundReason) {
        if (bookingId == null || bookingId <= 0) {
            throw new BadRequestException("Invalid booking ID");
        }

        RoomBooking roomBooking = roomBookingRepository.findById(bookingId)
                .orElseThrow(() -> new BadRequestException("Room booking not found with id: " + bookingId));

        if (!roomBooking.getPaymentStatus().equals(RoomBooking.PaymentStatus.SUCCEEDED)) {
            throw new BadRequestException("Only successful payments can be refunded");
        }

        roomBooking.setPaymentStatus(RoomBooking.PaymentStatus.REFUNDED);
        roomBooking.setPaymentFailureReason(refundReason);

        RoomBooking updatedBooking = roomBookingRepository.save(roomBooking);
        return RoomBookingResponseDTO.fromEntity(updatedBooking);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        return roomBookingRepository.findByPaymentStatus(RoomBooking.PaymentStatus.SUCCEEDED)
                .stream()
                .map(RoomBooking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public long countBookingsByPaymentStatus(RoomBooking.PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            throw new BadRequestException("Payment status is required");
        }
        return roomBookingRepository.findByPaymentStatus(paymentStatus).size();
    }

    private void validateCreateRoomBookingDTO(CreateRoomBookingDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Room booking data cannot be null");
        }
        if (dto.getRoomId() == null || dto.getRoomId() <= 0) {
            throw new BadRequestException("Room ID is required and must be positive");
        }
        if (dto.getUserId() == null || dto.getUserId() <= 0) {
            throw new BadRequestException("User ID is required and must be positive");
        }
        if (dto.getGuestName() == null || dto.getGuestName().trim().isEmpty()) {
            throw new BadRequestException("Guest name is required");
        }
        if (dto.getGuestName().length() > 100) {
            throw new BadRequestException("Guest name cannot exceed 100 characters");
        }
        if (dto.getGuestEmail() == null || dto.getGuestEmail().trim().isEmpty()) {
            throw new BadRequestException("Guest email is required");
        }
        if (!dto.getGuestEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new BadRequestException("Invalid email format");
        }
        if (dto.getGuestPhone() == null || dto.getGuestPhone().trim().isEmpty()) {
            throw new BadRequestException("Guest phone is required");
        }
        if (dto.getCheckInDate() == null) {
            throw new BadRequestException("Check-in date is required");
        }
        if (dto.getCheckOutDate() == null) {
            throw new BadRequestException("Check-out date is required");
        }
        if (dto.getCheckInDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Check-in date cannot be in the past");
        }
        if (dto.getCheckOutDate().isBefore(dto.getCheckInDate().plusDays(1))) {
            throw new BadRequestException("Check-out date must be at least one day after check-in date");
        }
        if (dto.getNumberOfGuests() == null || dto.getNumberOfGuests() <= 0) {
            throw new BadRequestException("Number of guests must be positive");
        }
        if (dto.getNumberOfGuests() > 10) {
            throw new BadRequestException("Number of guests cannot exceed 10");
        }
        if (dto.getRoomPricePerNight() == null || dto.getRoomPricePerNight().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Room price per night must be positive");
        }
    }
}