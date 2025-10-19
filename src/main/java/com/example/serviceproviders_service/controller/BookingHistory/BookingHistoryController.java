package com.example.serviceproviders_service.controller.BookingHistory;

import com.example.serviceproviders_service.dto.BookingHistory.BookingHistoryResponseDTO;
import com.example.serviceproviders_service.services.BookingHistory.BookingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking-history")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookingHistoryController {

    private final BookingHistoryService bookingHistoryService;

    /**
     * Get all bookings (rooms, tours, vehicles) for a specific user
     * GET /api/booking-history/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingHistoryResponseDTO>> getAllBookingsByUserId(@PathVariable Long userId) {
        List<BookingHistoryResponseDTO> bookings = bookingHistoryService.getAllBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get all room bookings for a specific user
     * GET /api/booking-history/user/{userId}/rooms
     */
    @GetMapping("/user/{userId}/rooms")
    public ResponseEntity<List<BookingHistoryResponseDTO>> getRoomBookingsByUserId(@PathVariable Long userId) {
        List<BookingHistoryResponseDTO> bookings = bookingHistoryService.getRoomBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get all tour bookings for a specific user
     * GET /api/booking-history/user/{userId}/tours
     */
    @GetMapping("/user/{userId}/tours")
    public ResponseEntity<List<BookingHistoryResponseDTO>> getTourBookingsByUserId(@PathVariable Long userId) {
        List<BookingHistoryResponseDTO> bookings = bookingHistoryService.getTourBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get all vehicle bookings for a specific user
     * GET /api/booking-history/user/{userId}/vehicles
     */
    @GetMapping("/user/{userId}/vehicles")
    public ResponseEntity<List<BookingHistoryResponseDTO>> getVehicleBookingsByUserId(@PathVariable Long userId) {
        List<BookingHistoryResponseDTO> bookings = bookingHistoryService.getVehicleBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get specific booking details by ID and type
     * GET /api/booking-history/{bookingId}?type=ROOM_BOOKING
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingHistoryResponseDTO> getBookingById(
            @PathVariable Long bookingId,
            @RequestParam BookingHistoryResponseDTO.BookingType type) {
        BookingHistoryResponseDTO booking = bookingHistoryService.getBookingById(bookingId, type);
        return ResponseEntity.ok(booking);
    }
}
