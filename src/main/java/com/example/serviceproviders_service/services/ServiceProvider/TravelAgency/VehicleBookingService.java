package com.example.serviceproviders_service.services.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.VehicleBookingResponseDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.CreateVehicleBookingDTO;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.VehicleBooking;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.Vehicle;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.repository.ServiceProvider.TravelAgency.VehicleBookingRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TravelAgency.VehicleRepository;
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
public class VehicleBookingService {

    @Autowired
    private VehicleBookingRepository vehicleBookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private static final BigDecimal SERVICE_CHARGE_RATE = new BigDecimal("0.10"); // 10%
    private static final BigDecimal TAX_RATE = new BigDecimal("0.12"); // 12%

    @Transactional
    public VehicleBookingResponseDTO createVehicleBooking(CreateVehicleBookingDTO dto) {
        validateCreateVehicleBookingDTO(dto);

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new BadRequestException("Vehicle not found with id: " + dto.getVehicleId()));

        if (!vehicle.getStatus().equals(Vehicle.VehicleStatus.AVAILABLE)) {
            throw new BadRequestException("Vehicle is not available for booking");
        }

        // Check for conflicting bookings
        List<VehicleBooking> conflictingBookings = vehicleBookingRepository.findConflictingVehicleBookings(
                dto.getVehicleId(), dto.getPickupDate(), dto.getReturnDate());

        if (!conflictingBookings.isEmpty()) {
            throw new BadRequestException("Vehicle is not available for the selected dates");
        }

        int numberOfDays = (int) ChronoUnit.DAYS.between(dto.getPickupDate(), dto.getReturnDate());
        if (numberOfDays < 1) {
            numberOfDays = 1;
        }

        // Calculate total based on distance
        BigDecimal subtotal = dto.getPricePerKm().multiply(dto.getEstimatedDistance()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal serviceCharge = subtotal.multiply(SERVICE_CHARGE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxes = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(serviceCharge).add(taxes);

        VehicleBooking vehicleBooking = new VehicleBooking();
        vehicleBooking.setVehicleId(dto.getVehicleId());
        vehicleBooking.setServiceProviderId(vehicle.getServiceProviderId());
        vehicleBooking.setUserId(dto.getUserId());
        vehicleBooking.setCustomerName(dto.getCustomerName());
        vehicleBooking.setCustomerEmail(dto.getCustomerEmail());
        vehicleBooking.setCustomerPhone(dto.getCustomerPhone());
        vehicleBooking.setSpecialRequests(dto.getSpecialRequests());
        vehicleBooking.setPickupDate(dto.getPickupDate());
        vehicleBooking.setReturnDate(dto.getReturnDate());
        vehicleBooking.setPickupLocation(dto.getPickupLocation());
        vehicleBooking.setDropoffLocation(dto.getDropoffLocation());
        vehicleBooking.setNumberOfDays(numberOfDays);
        vehicleBooking.setEstimatedDistance(dto.getEstimatedDistance());
        vehicleBooking.setWithAC(dto.getWithAC() != null ? dto.getWithAC() : true);
        vehicleBooking.setPricePerKm(dto.getPricePerKm());
        vehicleBooking.setSubtotal(subtotal);
        vehicleBooking.setServiceCharge(serviceCharge);
        vehicleBooking.setTaxes(taxes);
        vehicleBooking.setTotalAmount(totalAmount);
        vehicleBooking.setStatus(VehicleBooking.VehicleBookingStatus.PENDING);
        vehicleBooking.setCurrency(dto.getCurrency() != null ? dto.getCurrency().toUpperCase() : "USD");
        vehicleBooking.setPaymentStatus(VehicleBooking.PaymentStatus.PENDING);
        vehicleBooking.setPaymentMethod(VehicleBooking.PaymentMethod.STRIPE_CARD);

        VehicleBooking savedVehicleBooking = vehicleBookingRepository.save(vehicleBooking);
        return VehicleBookingResponseDTO.fromEntity(savedVehicleBooking);
    }

    @Transactional
    public VehicleBookingResponseDTO updatePaymentDetails(Long bookingId, String stripeSessionId,
                                                          String stripePaymentIntentId,
                                                          VehicleBooking.PaymentStatus paymentStatus) {
        VehicleBooking vehicleBooking = vehicleBookingRepository.findById(bookingId)
                .orElseThrow(() -> new BadRequestException("Vehicle booking not found with id: " + bookingId));

        vehicleBooking.setStripeSessionId(stripeSessionId);
        vehicleBooking.setStripePaymentIntentId(stripePaymentIntentId);
        vehicleBooking.setPaymentStatus(paymentStatus);

        if (paymentStatus == VehicleBooking.PaymentStatus.SUCCEEDED) {
            vehicleBooking.setPaidAt(LocalDateTime.now());
            vehicleBooking.setStatus(VehicleBooking.VehicleBookingStatus.CONFIRMED);
        }

        VehicleBooking updatedBooking = vehicleBookingRepository.save(vehicleBooking);
        return VehicleBookingResponseDTO.fromEntity(updatedBooking);
    }

    @Transactional
    public VehicleBookingResponseDTO updatePaymentFailure(String stripeSessionId, String failureReason) {
        VehicleBooking vehicleBooking = vehicleBookingRepository.findByStripeSessionId(stripeSessionId)
                .orElseThrow(() -> new BadRequestException("Vehicle booking not found with session ID: " + stripeSessionId));

        vehicleBooking.setPaymentFailureReason(failureReason);
        vehicleBooking.setPaymentStatus(VehicleBooking.PaymentStatus.FAILED);

        VehicleBooking updatedBooking = vehicleBookingRepository.save(vehicleBooking);
        return VehicleBookingResponseDTO.fromEntity(updatedBooking);
    }

    @Transactional(readOnly = true)
    public VehicleBookingResponseDTO getVehicleBookingById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid vehicle booking ID");
        }
        VehicleBooking vehicleBooking = vehicleBookingRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Vehicle booking not found with id: " + id));
        return VehicleBookingResponseDTO.fromEntity(vehicleBooking);
    }

    @Transactional(readOnly = true)
    public VehicleBookingResponseDTO getVehicleBookingByReference(String bookingReference) {
        if (bookingReference == null || bookingReference.trim().isEmpty()) {
            throw new BadRequestException("Booking reference is required");
        }
        VehicleBooking vehicleBooking = vehicleBookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new BadRequestException("Vehicle booking not found with reference: " + bookingReference));
        return VehicleBookingResponseDTO.fromEntity(vehicleBooking);
    }

    @Transactional(readOnly = true)
    public VehicleBookingResponseDTO getVehicleBookingByStripeSessionId(String stripeSessionId) {
        if (stripeSessionId == null || stripeSessionId.trim().isEmpty()) {
            throw new BadRequestException("Stripe session ID is required");
        }
        VehicleBooking vehicleBooking = vehicleBookingRepository.findByStripeSessionId(stripeSessionId)
                .orElseThrow(() -> new BadRequestException("Vehicle booking not found with session ID: " + stripeSessionId));
        return VehicleBookingResponseDTO.fromEntity(vehicleBooking);
    }

    @Transactional(readOnly = true)
    public List<VehicleBookingResponseDTO> getAllVehicleBookings() {
        return vehicleBookingRepository.findAll()
                .stream()
                .map(VehicleBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleBookingResponseDTO> getVehicleBookingsByServiceProviderId(Long serviceProviderId) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }
        return vehicleBookingRepository.findByServiceProviderId(serviceProviderId)
                .stream()
                .map(VehicleBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleBookingResponseDTO> getVehicleBookingsByCustomerEmail(String customerEmail) {
        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            throw new BadRequestException("Customer email is required");
        }
        return vehicleBookingRepository.findByCustomerEmail(customerEmail)
                .stream()
                .map(VehicleBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleBookingResponseDTO> getVehicleBookingsByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }
        return vehicleBookingRepository.findByUserId(userId)
                .stream()
                .map(VehicleBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleBookingResponseDTO> getVehicleBookingsByPaymentStatus(VehicleBooking.PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            throw new BadRequestException("Payment status is required");
        }
        return vehicleBookingRepository.findByPaymentStatus(paymentStatus)
                .stream()
                .map(VehicleBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleBookingResponseDTO> getSuccessfulPayments() {
        return vehicleBookingRepository.findByPaymentStatus(VehicleBooking.PaymentStatus.SUCCEEDED)
                .stream()
                .map(VehicleBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleBookingResponseDTO> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Start date and end date are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        return vehicleBookingRepository.findPaymentsByDateRange(startDate, endDate)
                .stream()
                .map(VehicleBookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public VehicleBookingResponseDTO updateVehicleBookingStatus(Long id, VehicleBooking.VehicleBookingStatus status) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid vehicle booking ID");
        }
        if (status == null) {
            throw new BadRequestException("Vehicle booking status is required");
        }

        VehicleBooking vehicleBooking = vehicleBookingRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Vehicle booking not found with id: " + id));

        vehicleBooking.setStatus(status);
        VehicleBooking updatedVehicleBooking = vehicleBookingRepository.save(vehicleBooking);
        return VehicleBookingResponseDTO.fromEntity(updatedVehicleBooking);
    }

    @Transactional
    public boolean cancelVehicleBooking(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid vehicle booking ID");
        }

        VehicleBooking vehicleBooking = vehicleBookingRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Vehicle booking not found with id: " + id));

        if (vehicleBooking.getStatus().equals(VehicleBooking.VehicleBookingStatus.IN_PROGRESS) ||
                vehicleBooking.getStatus().equals(VehicleBooking.VehicleBookingStatus.COMPLETED)) {
            throw new BadRequestException("Cannot cancel vehicle booking that is already in progress or completed");
        }

        vehicleBooking.setStatus(VehicleBooking.VehicleBookingStatus.CANCELLED);
        vehicleBooking.setPaymentStatus(VehicleBooking.PaymentStatus.CANCELLED);
        vehicleBookingRepository.save(vehicleBooking);
        return true;
    }

    @Transactional
    public VehicleBookingResponseDTO refundPayment(Long bookingId, String refundReason) {
        if (bookingId == null || bookingId <= 0) {
            throw new BadRequestException("Invalid booking ID");
        }

        VehicleBooking vehicleBooking = vehicleBookingRepository.findById(bookingId)
                .orElseThrow(() -> new BadRequestException("Vehicle booking not found with id: " + bookingId));

        if (!vehicleBooking.getPaymentStatus().equals(VehicleBooking.PaymentStatus.SUCCEEDED)) {
            throw new BadRequestException("Only successful payments can be refunded");
        }

        vehicleBooking.setPaymentStatus(VehicleBooking.PaymentStatus.REFUNDED);
        vehicleBooking.setPaymentFailureReason(refundReason);

        VehicleBooking updatedBooking = vehicleBookingRepository.save(vehicleBooking);
        return VehicleBookingResponseDTO.fromEntity(updatedBooking);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        return vehicleBookingRepository.findByPaymentStatus(VehicleBooking.PaymentStatus.SUCCEEDED)
                .stream()
                .map(VehicleBooking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public long countBookingsByPaymentStatus(VehicleBooking.PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            throw new BadRequestException("Payment status is required");
        }
        return vehicleBookingRepository.findByPaymentStatus(paymentStatus).size();
    }

    private void validateCreateVehicleBookingDTO(CreateVehicleBookingDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Vehicle booking data cannot be null");
        }
        if (dto.getVehicleId() == null || dto.getVehicleId() <= 0) {
            throw new BadRequestException("Vehicle ID is required and must be positive");
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
        if (dto.getPickupDate() == null) {
            throw new BadRequestException("Pickup date is required");
        }
        if (dto.getReturnDate() == null) {
            throw new BadRequestException("Return date is required");
        }
        if (dto.getPickupDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Pickup date cannot be in the past");
        }
        if (dto.getReturnDate().isBefore(dto.getPickupDate().plusDays(1))) {
            throw new BadRequestException("Return date must be at least one day after pickup date");
        }
        if (dto.getPickupLocation() == null || dto.getPickupLocation().trim().isEmpty()) {
            throw new BadRequestException("Pickup location is required");
        }
        if (dto.getDropoffLocation() == null || dto.getDropoffLocation().trim().isEmpty()) {
            throw new BadRequestException("Dropoff location is required");
        }
        if (dto.getEstimatedDistance() == null || dto.getEstimatedDistance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Estimated distance must be positive");
        }
        if (dto.getPricePerKm() == null || dto.getPricePerKm().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Price per km must be positive");
        }
    }
}
