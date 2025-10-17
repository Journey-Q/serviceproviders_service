package com.example.serviceproviders_service.controller;

import com.example.serviceproviders_service.dto.BookingHistoryResponseDTO;
import com.example.serviceproviders_service.services.BookingHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking-history")
@CrossOrigin(origins = "*")
public class BookingHistoryController {

    private final BookingHistoryService bookingHistoryService;

    public BookingHistoryController(BookingHistoryService bookingHistoryService) {
        this.bookingHistoryService = bookingHistoryService;
    }

    /**
     * Get all bookings (Hotel, Tour, Travel Agency) for a specific user
     * GET /api/booking-history/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingHistoryResponseDTO>> getAllBookingsByUserId(@PathVariable Long userId) {
        List<BookingHistoryResponseDTO> bookings = bookingHistoryService.getAllBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get hotel bookings only for a specific user
     * GET /api/booking-history/user/{userId}/hotels
     */
    @GetMapping("/user/{userId}/hotels")
    public ResponseEntity<List<BookingHistoryResponseDTO>> getHotelBookingsByUserId(@PathVariable Long userId) {
        List<BookingHistoryResponseDTO> bookings = bookingHistoryService.getHotelBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get tour bookings only for a specific user
     * GET /api/booking-history/user/{userId}/tours
     */
    @GetMapping("/user/{userId}/tours")
    public ResponseEntity<List<BookingHistoryResponseDTO>> getTourBookingsByUserId(@PathVariable Long userId) {
        List<BookingHistoryResponseDTO> bookings = bookingHistoryService.getTourBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get travel agency (vehicle) bookings only for a specific user
     * GET /api/booking-history/user/{userId}/vehicles
     */
    @GetMapping("/user/{userId}/vehicles")
    public ResponseEntity<List<BookingHistoryResponseDTO>> getVehicleBookingsByUserId(@PathVariable Long userId) {
        List<BookingHistoryResponseDTO> bookings = bookingHistoryService.getVehicleBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get booking count by type for a specific user
     * GET /api/booking-history/user/{userId}/count
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<BookingCountResponse> getBookingCountByUserId(@PathVariable Long userId) {
        List<BookingHistoryResponseDTO> hotelBookings = bookingHistoryService.getHotelBookingsByUserId(userId);
        List<BookingHistoryResponseDTO> tourBookings = bookingHistoryService.getTourBookingsByUserId(userId);
        List<BookingHistoryResponseDTO> vehicleBookings = bookingHistoryService.getVehicleBookingsByUserId(userId);

        BookingCountResponse response = new BookingCountResponse();
        response.setHotelCount(hotelBookings.size());
        response.setTourCount(tourBookings.size());
        response.setVehicleCount(vehicleBookings.size());
        response.setTotalCount(hotelBookings.size() + tourBookings.size() + vehicleBookings.size());

        return ResponseEntity.ok(response);
    }

    /**
     * Inner class for booking count response
     */
    public static class BookingCountResponse {
        private int hotelCount;
        private int tourCount;
        private int vehicleCount;
        private int totalCount;

        public int getHotelCount() {
            return hotelCount;
        }

        public void setHotelCount(int hotelCount) {
            this.hotelCount = hotelCount;
        }

        public int getTourCount() {
            return tourCount;
        }

        public void setTourCount(int tourCount) {
            this.tourCount = tourCount;
        }

        public int getVehicleCount() {
            return vehicleCount;
        }

        public void setVehicleCount(int vehicleCount) {
            this.vehicleCount = vehicleCount;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
    }
}
