package com.example.serviceproviders_service.services.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.CreateTourBookingDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.TourBookingResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.Tour;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourBooking;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.repository.ServiceProvider.TourGuide.TourBookingRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TourGuide.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourBookingService {

    @Autowired
    private TourBookingRepository tourBookingRepository;

    @Autowired
    private TourRepository tourRepository;

    private static final BigDecimal SERVICE_CHARGE_RATE = new BigDecimal("0.10"); // 10%
    private static final BigDecimal TAX_RATE = new BigDecimal("0.12"); // 12%

    @Transactional
    public TourBookingResponseDTO createTourBooking(CreateTourBookingDTO dto) {
        validateCreateTourBookingDTO(dto);

        Tour tour = tourRepository.findById(dto.getTourId())
                .orElseThrow(() -> new BadRequestException("Tour not found with id: " + dto.getTourId()));

        if (!tour.getStatus().equals(Tour.TourStatus.AVAILABLE)) {
            throw new BadRequestException("Tour is not available for booking");
        }

        // Check if tour has capacity for the requested number of people
        Integer currentPeopleBooked = tourBookingRepository.countPeopleForTourOnDate(
                dto.getTourId(), dto.getTourDate());

        if (currentPeopleBooked == null) {
            currentPeopleBooked = 0;
        }

        int remainingCapacity = tour.getMaxPeople() - currentPeopleBooked;
        if (dto.getNumberOfPeople() > remainingCapacity) {
            throw new BadRequestException("Tour does not have enough capacity. Available spots: " + remainingCapacity);
        }

        // Check minimum people requirement
        if (dto.getNumberOfPeople() < tour.getMinPeople() && currentPeopleBooked == 0) {
            throw new BadRequestException("Minimum " + tour.getMinPeople() + " people required for this tour");
        }

        BigDecimal subtotal = dto.getPricePerPerson().multiply(new BigDecimal(dto.getNumberOfPeople()));
        BigDecimal serviceCharge = subtotal.multiply(SERVICE_CHARGE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxes = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(serviceCharge).add(taxes);

        TourBooking tourBooking = new TourBooking();
        tourBooking.setTourId(dto.getTourId());
        tourBooking.setServiceProviderId(tour.getServiceProviderId());
        tourBooking.setUserId(dto.getUserId());
        tourBooking.setCustomerName(dto.getCustomerName());
        tourBooking.setCustomerEmail(dto.getCustomerEmail());
        tourBooking.setCustomerPhone(dto.getCustomerPhone());
        tourBooking.setSpecialRequests(dto.getSpecialRequests());
        tourBooking.setTourDate(dto.getTourDate());
        tourBooking.setNumberOfPeople(dto.getNumberOfPeople());
        tourBooking.setPricePerPerson(dto.getPricePerPerson());
        tourBooking.setSubtotal(subtotal);
        tourBooking.setServiceCharge(serviceCharge);
        tourBooking.setTaxes(taxes);
        tourBooking.setTotalAmount(totalAmount);
        tourBooking.setStatus(TourBooking.TourBookingStatus.PENDING);
        tourBooking.setCurrency(dto.getCurrency() != null ? dto.getCurrency().toUpperCase() : "USD");
        tourBooking.setPaymentStatus(TourBooking.PaymentStatus.PENDING);
        tourBooking.setPaymentMethod(TourBooking.PaymentMethod.STRIPE_CARD);

        TourBooking savedTourBooking = tourBookingRepository.save(tourBooking);
        return TourBookingResponseDTO.fromEntity(savedTourBooking);
    }

    @Transactional
    public TourBookingResponseDTO updatePaymentDetails(Long bookingId, String stripeSessionId,
                                                       String stripePaymentIntentId,
                                                       TourBooking.PaymentStatus paymentStatus) {
        TourBooking tourBooking = tourBookingRepository.findById(bookingId)
                .orElseThrow(() -> new BadRequestException("Tour booking not found with id: " + bookingId));

        tourBooking.setStripeSessionId(stripeSessionId);
        tourBooking.setStripePaymentIntentId(stripePaymentIntentId);
        tourBooking.setPaymentStatus(paymentStatus);

        if (paymentStatus == TourBooking.PaymentStatus.SUCCEEDED) {
            tourBooking.setPaidAt(LocalDateTime.now());
            tourBooking.setStatus(TourBooking.TourBookingStatus.CONFIRMED);
        }

        TourBooking updatedBooking = tourBookingRepository.save(tourBooking);
        return TourBookingResponseDTO.fromEntity(updatedBooking);
    }

    @Transactional
    public TourBookingResponseDTO updatePaymentFailure(String stripeSessionId, String failureReason) {
        TourBooking tourBooking = tourBookingRepository.findByStripeSessionId(stripeSessionId)
                .orElseThrow(() -> new BadRequestException("Tour booking not found with session ID: " + stripeSessionId));

        tourBooking.setPaymentFailureReason(failureReason);
        tourBooking.setPaymentStatus(TourBooking.PaymentStatus.FAILED);

        TourBooking updatedBooking = tourBookingRepository.save(tourBooking);
        return TourBookingResponseDTO.fromEntity(updatedBooking);
    }

    @Transactional(readOnly = true)
    public TourBookingResponseDTO getTourBookingById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid tour booking ID");
        }
        TourBooking tourBooking = tourBookingRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tour booking not found with id: " + id));
        return TourBookingResponseDTO.fromEntity(tourBooking);
    }

    @Transactional(readOnly = true)
    public TourBookingResponseDTO getTourBookingByReference(String bookingReference) {
        if (bookingReference == null || bookingReference.trim().isEmpty()) {
            throw new BadRequestException("Booking reference is required");
        }
        TourBooking tourBooking = tourBookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new BadRequestException("Tour booking not found with reference: " + bookingReference));
        return TourBookingResponseDTO.fromEntity(tourBooking);
    }

    @Transactional(readOnly = true)
    public TourBookingResponseDTO getTourBookingByStripeSessionId(String stripeSessionId) {
        if (stripeSessionId == null || stripeSessionId.trim().isEmpty()) {
            throw new BadRequestException("Stripe session ID is required");
        }
        TourBooking tourBooking = tourBookingRepository.findByStripeSessionId(stripeSessionId)
                .orElseThrow(() -> new BadRequestException("Tour booking not found with session ID: " + stripeSessionId));
        return TourBookingResponseDTO.fromEntity(tourBooking);
    }

    @Transactional(readOnly = true)
    public List<TourBookingResponseDTO> getAllTourBookings() {
        return tourBookingRepository.findAll()
                .stream()
                .map(TourBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TourBookingResponseDTO> getTourBookingsByServiceProviderId(Long serviceProviderId) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }
        return tourBookingRepository.findByServiceProviderId(serviceProviderId)
                .stream()
                .map(TourBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TourBookingResponseDTO> getTourBookingsByCustomerEmail(String customerEmail) {
        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            throw new BadRequestException("Customer email is required");
        }
        return tourBookingRepository.findByCustomerEmail(customerEmail)
                .stream()
                .map(TourBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TourBookingResponseDTO> getTourBookingsByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }
        return tourBookingRepository.findByUserId(userId)
                .stream()
                .map(TourBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TourBookingResponseDTO> getTourBookingsByPaymentStatus(TourBooking.PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            throw new BadRequestException("Payment status is required");
        }
        return tourBookingRepository.findByPaymentStatus(paymentStatus)
                .stream()
                .map(TourBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TourBookingResponseDTO> getSuccessfulPayments() {
        return tourBookingRepository.findByPaymentStatus(TourBooking.PaymentStatus.SUCCEEDED)
                .stream()
                .map(TourBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TourBookingResponseDTO> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Start date and end date are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        return tourBookingRepository.findPaymentsByDateRange(startDate, endDate)
                .stream()
                .map(TourBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TourBookingResponseDTO updateTourBookingStatus(Long id, TourBooking.TourBookingStatus status) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid tour booking ID");
        }
        if (status == null) {
            throw new BadRequestException("Tour booking status is required");
        }

        TourBooking tourBooking = tourBookingRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tour booking not found with id: " + id));

        tourBooking.setStatus(status);
        TourBooking updatedTourBooking = tourBookingRepository.save(tourBooking);
        return TourBookingResponseDTO.fromEntity(updatedTourBooking);
    }

    @Transactional
    public boolean cancelTourBooking(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid tour booking ID");
        }

        TourBooking tourBooking = tourBookingRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tour booking not found with id: " + id));

        if (tourBooking.getStatus().equals(TourBooking.TourBookingStatus.IN_PROGRESS) ||
                tourBooking.getStatus().equals(TourBooking.TourBookingStatus.COMPLETED)) {
            throw new BadRequestException("Cannot cancel tour booking that is already in progress or completed");
        }

        tourBooking.setStatus(TourBooking.TourBookingStatus.CANCELLED);
        tourBooking.setPaymentStatus(TourBooking.PaymentStatus.CANCELLED);
        tourBookingRepository.save(tourBooking);
        return true;
    }

    @Transactional
    public TourBookingResponseDTO refundPayment(Long bookingId, String refundReason) {
        if (bookingId == null || bookingId <= 0) {
            throw new BadRequestException("Invalid booking ID");
        }

        TourBooking tourBooking = tourBookingRepository.findById(bookingId)
                .orElseThrow(() -> new BadRequestException("Tour booking not found with id: " + bookingId));

        if (!tourBooking.getPaymentStatus().equals(TourBooking.PaymentStatus.SUCCEEDED)) {
            throw new BadRequestException("Only successful payments can be refunded");
        }

        tourBooking.setPaymentStatus(TourBooking.PaymentStatus.REFUNDED);
        tourBooking.setPaymentFailureReason(refundReason);

        TourBooking updatedBooking = tourBookingRepository.save(tourBooking);
        return TourBookingResponseDTO.fromEntity(updatedBooking);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        return tourBookingRepository.findByPaymentStatus(TourBooking.PaymentStatus.SUCCEEDED)
                .stream()
                .map(TourBooking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public long countBookingsByPaymentStatus(TourBooking.PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            throw new BadRequestException("Payment status is required");
        }
        return tourBookingRepository.findByPaymentStatus(paymentStatus).size();
    }

    private void validateCreateTourBookingDTO(CreateTourBookingDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Tour booking data cannot be null");
        }
        if (dto.getTourId() == null || dto.getTourId() <= 0) {
            throw new BadRequestException("Tour ID is required and must be positive");
        }
        if (dto.getUserId() == null || dto.getUserId() <= 0) {
            throw new BadRequestException("User ID is required and must be positive");
        }
        if (dto.getCustomerName() == null || dto.getCustomerName().trim().isEmpty()) {
            throw new BadRequestException("Customer name is required");
        }
        if (dto.getCustomerName().length() > 100) {
            throw new BadRequestException("Customer name cannot exceed 100 characters");
        }
        if (dto.getCustomerEmail() == null || dto.getCustomerEmail().trim().isEmpty()) {
            throw new BadRequestException("Customer email is required");
        }
        if (!dto.getCustomerEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new BadRequestException("Invalid email format");
        }
        if (dto.getCustomerPhone() == null || dto.getCustomerPhone().trim().isEmpty()) {
            throw new BadRequestException("Customer phone is required");
        }
        if (dto.getTourDate() == null) {
            throw new BadRequestException("Tour date is required");
        }
        if (dto.getTourDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Tour date cannot be in the past");
        }
        if (dto.getNumberOfPeople() == null || dto.getNumberOfPeople() <= 0) {
            throw new BadRequestException("Number of people must be positive");
        }
        if (dto.getNumberOfPeople() > 50) {
            throw new BadRequestException("Number of people cannot exceed 50");
        }
        if (dto.getPricePerPerson() == null || dto.getPricePerPerson().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Price per person must be positive");
        }
    }
}