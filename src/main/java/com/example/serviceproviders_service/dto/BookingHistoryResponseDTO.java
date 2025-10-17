package com.example.serviceproviders_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingHistoryResponseDTO {

    // Booking Type - to distinguish between Hotel, Tour, and Travel Agency bookings
    private String bookingType; // HOTEL, TOUR, TRAVEL_AGENCY

    // Common Booking Information
    private Long bookingId;
    private String bookingReference;
    private String status;
    private LocalDateTime bookedDate;

    // Service Provider Information
    private Long serviceProviderId;
    private String serviceName;
    private String serviceImage;

    // Customer Information
    private Long userId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // Common Date Information
    private LocalDate startDate; // checkInDate for hotels, tourDate for tours, pickupDate for vehicles
    private LocalDate endDate;   // checkOutDate for hotels, null for tours, returnDate for vehicles

    // Booking Details
    private Integer numberOfPeople; // numberOfGuests for hotels, numberOfPeople for tours, null for vehicles
    private Integer numberOfNights; // for hotels only
    private Integer numberOfDays;   // for vehicles only
    private String specialRequests;

    // Hotel Specific
    private Long roomId;
    private String roomType;
    private String roomNumber;

    // Tour Specific
    private Long tourId;
    private String tourDuration;
    private String tourDescription;

    // Vehicle Specific
    private Long vehicleId;
    private String vehicleType;
    private String vehicleModel;
    private String pickupLocation;
    private String dropoffLocation;
    private BigDecimal estimatedDistance;
    private Boolean withAC;

    // Pricing Information
    private BigDecimal subtotal;
    private BigDecimal serviceCharge;
    private BigDecimal taxes;
    private BigDecimal totalAmount;
    private String currency;

    // Payment Information
    private String paymentStatus;
    private String paymentMethod;
    private String paymentFailureReason;
    private LocalDateTime paidAt;

    // Payment Integration
    private String stripeSessionId;
    private String stripePaymentIntentId;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
