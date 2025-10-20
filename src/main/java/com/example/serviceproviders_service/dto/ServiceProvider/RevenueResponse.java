package com.example.serviceproviders_service.dto.ServiceProvider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Revenue response DTO for service providers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueResponse {
    private Long serviceProviderId;
    private String serviceProviderName;
    private String serviceType;

    // Overall revenue summary
    private BigDecimal totalRevenue;
    private BigDecimal pendingRevenue;
    private BigDecimal completedRevenue;
    private Integer totalBookings;
    private Integer completedBookings;
    private Integer pendingBookings;
    private Integer cancelledBookings;

    // Breakdown by booking type (only populated for respective service types)
    private RoomRevenueData roomRevenue;
    private TourRevenueData tourRevenue;
    private VehicleRevenueData vehicleRevenue;

    // Recent bookings
    private List<BookingSummary> recentBookings;

    // Report metadata
    private LocalDateTime generatedAt;
    private String reportPeriod;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomRevenueData {
        private BigDecimal totalRoomRevenue;
        private BigDecimal averageBookingValue;
        private Integer totalRoomBookings;
        private Integer confirmedBookings;
        private Integer completedBookings;
        private Integer cancelledBookings;
        private BigDecimal cancellationLoss;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TourRevenueData {
        private BigDecimal totalTourRevenue;
        private BigDecimal averageBookingValue;
        private Integer totalTourBookings;
        private Integer approvedBookings;
        private Integer completedBookings;
        private Integer rejectedBookings;
        private Integer totalParticipants;
        private BigDecimal averageRevenuePerPerson;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleRevenueData {
        private BigDecimal totalVehicleRevenue;
        private BigDecimal averageBookingValue;
        private Integer totalVehicleBookings;
        private Integer approvedBookings;
        private Integer completedBookings;
        private Integer rejectedBookings;
        private Integer totalEstimatedKilometers;
        private BigDecimal averageRevenuePerKm;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingSummary {
        private Long bookingId;
        private String bookingType; // ROOM, TOUR, VEHICLE
        private String customerName;
        private String customerEmail;
        private BigDecimal amount;
        private String status;
        private LocalDateTime bookingDate;
        private LocalDateTime completedDate;
        private String resourceName; // Room name, Tour name, or Vehicle name
    }
}