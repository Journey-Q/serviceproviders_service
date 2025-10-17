package com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.VehicleBooking;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class VehicleBookingResponseDTO {
    private Long id;
    private Long vehicleId;
    private Long serviceProviderId;
    private Long userId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String specialRequests;
    private LocalDate pickupDate;
    private LocalDate returnDate;
    private String pickupLocation;
    private String dropoffLocation;
    private Integer numberOfDays;
    private BigDecimal estimatedDistance;
    private Boolean withAC;
    private BigDecimal pricePerKm;
    private BigDecimal subtotal;
    private BigDecimal serviceCharge;
    private BigDecimal taxes;
    private BigDecimal totalAmount;
    private VehicleBooking.VehicleBookingStatus status;
    private String bookingReference;

    // Payment related fields
    private String stripeSessionId;
    private String stripePaymentIntentId;
    private String currency;
    private VehicleBooking.PaymentStatus paymentStatus;
    private VehicleBooking.PaymentMethod paymentMethod;
    private String paymentFailureReason;
    private LocalDateTime paidAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VehicleBookingResponseDTO() {}

    public static VehicleBookingResponseDTO fromEntity(VehicleBooking vehicleBooking) {
        VehicleBookingResponseDTO dto = new VehicleBookingResponseDTO();
        dto.setId(vehicleBooking.getId());
        dto.setVehicleId(vehicleBooking.getVehicleId());
        dto.setServiceProviderId(vehicleBooking.getServiceProviderId());
        dto.setUserId(vehicleBooking.getUserId());
        dto.setCustomerName(vehicleBooking.getCustomerName());
        dto.setCustomerEmail(vehicleBooking.getCustomerEmail());
        dto.setCustomerPhone(vehicleBooking.getCustomerPhone());
        dto.setSpecialRequests(vehicleBooking.getSpecialRequests());
        dto.setPickupDate(vehicleBooking.getPickupDate());
        dto.setReturnDate(vehicleBooking.getReturnDate());
        dto.setPickupLocation(vehicleBooking.getPickupLocation());
        dto.setDropoffLocation(vehicleBooking.getDropoffLocation());
        dto.setNumberOfDays(vehicleBooking.getNumberOfDays());
        dto.setEstimatedDistance(vehicleBooking.getEstimatedDistance());
        dto.setWithAC(vehicleBooking.getWithAC());
        dto.setPricePerKm(vehicleBooking.getPricePerKm());
        dto.setSubtotal(vehicleBooking.getSubtotal());
        dto.setServiceCharge(vehicleBooking.getServiceCharge());
        dto.setTaxes(vehicleBooking.getTaxes());
        dto.setTotalAmount(vehicleBooking.getTotalAmount());
        dto.setStatus(vehicleBooking.getStatus());
        dto.setBookingReference(vehicleBooking.getBookingReference());

        // Payment fields
        dto.setStripeSessionId(vehicleBooking.getStripeSessionId());
        dto.setStripePaymentIntentId(vehicleBooking.getStripePaymentIntentId());
        dto.setCurrency(vehicleBooking.getCurrency());
        dto.setPaymentStatus(vehicleBooking.getPaymentStatus());
        dto.setPaymentMethod(vehicleBooking.getPaymentMethod());
        dto.setPaymentFailureReason(vehicleBooking.getPaymentFailureReason());
        dto.setPaidAt(vehicleBooking.getPaidAt());

        dto.setCreatedAt(vehicleBooking.getCreatedAt());
        dto.setUpdatedAt(vehicleBooking.getUpdatedAt());
        return dto;
    }
}
