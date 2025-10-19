package com.example.serviceproviders_service.dto.BookingHistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingHistoryResponseDTO {

    private Long bookingId;
    private BookingType bookingType;
    private Long serviceProviderId;
    private String serviceProviderName;
    private Long userId;

    // Customer Information
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // Common Booking Details
    private LocalDate startDate; // checkInDate for hotels, tourDate for tours, startDate for vehicles
    private LocalDate endDate;   // checkOutDate for hotels, null for tours, endDate for vehicles
    private BigDecimal totalAmount;
    private String status;

    // Type-specific details (as JSON-like string or structured)
    private RoomBookingDetails roomBookingDetails;
    private TourBookingDetails tourBookingDetails;
    private VehicleBookingDetails vehicleBookingDetails;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String specialRequests;
    private String cancellationReason;

    public enum BookingType {
        ROOM_BOOKING,
        TOUR_BOOKING,
        VEHICLE_BOOKING
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomBookingDetails {
        private Long roomId;
        private String roomType;
        private Integer numberOfGuests;
        private Integer numberOfNights;
        private BigDecimal pricePerNight;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TourBookingDetails {
        private Long tourId;
        private String tourTitle;
        private Integer numberOfPeople;
        private BigDecimal pricePerPerson;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleBookingDetails {
        private Long vehicleId;
        private String vehicleType;
        private String vehicleBrand;
        private String vehicleModel;
        private String pickupLocation;
        private String dropoffLocation;
        private Integer estimatedKilometers;
        private Boolean withAC;
        private BigDecimal pricePerKm;
    }
}