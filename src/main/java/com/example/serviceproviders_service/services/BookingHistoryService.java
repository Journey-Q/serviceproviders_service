package com.example.serviceproviders_service.services;

import com.example.serviceproviders_service.dto.BookingHistoryResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.Room;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.RoomBooking;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.Tour;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourBooking;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.Vehicle;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.VehicleBooking;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.repository.ServiceProvider.Hotel.RoomBookingRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.Hotel.RoomRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TourGuide.TourBookingRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TourGuide.TourRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TravelAgency.VehicleBookingRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TravelAgency.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingHistoryService {

    @Autowired
    private RoomBookingRepository roomBookingRepository;

    @Autowired
    private TourBookingRepository tourBookingRepository;

    @Autowired
    private VehicleBookingRepository vehicleBookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     * Get all bookings (Hotel, Tour, Travel Agency) for a specific user
     * @param userId User ID
     * @return List of all bookings sorted by booking date (newest first)
     */
    @Transactional(readOnly = true)
    public List<BookingHistoryResponseDTO> getAllBookingsByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        List<BookingHistoryResponseDTO> allBookings = new ArrayList<>();

        // Get hotel bookings
        List<RoomBooking> roomBookings = roomBookingRepository.findByUserId(userId);
        for (RoomBooking booking : roomBookings) {
            allBookings.add(mapRoomBookingToDTO(booking));
        }

        // Get tour bookings
        List<TourBooking> tourBookings = tourBookingRepository.findByUserId(userId);
        for (TourBooking booking : tourBookings) {
            allBookings.add(mapTourBookingToDTO(booking));
        }

        // Get vehicle bookings
        List<VehicleBooking> vehicleBookings = vehicleBookingRepository.findByUserId(userId);
        for (VehicleBooking booking : vehicleBookings) {
            allBookings.add(mapVehicleBookingToDTO(booking));
        }

        // Sort by booking date (newest first)
        return allBookings.stream()
                .sorted(Comparator.comparing(BookingHistoryResponseDTO::getBookedDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get hotel bookings only for a specific user
     */
    @Transactional(readOnly = true)
    public List<BookingHistoryResponseDTO> getHotelBookingsByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        return roomBookingRepository.findByUserId(userId).stream()
                .map(this::mapRoomBookingToDTO)
                .sorted(Comparator.comparing(BookingHistoryResponseDTO::getBookedDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get tour bookings only for a specific user
     */
    @Transactional(readOnly = true)
    public List<BookingHistoryResponseDTO> getTourBookingsByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        return tourBookingRepository.findByUserId(userId).stream()
                .map(this::mapTourBookingToDTO)
                .sorted(Comparator.comparing(BookingHistoryResponseDTO::getBookedDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get travel agency (vehicle) bookings only for a specific user
     */
    @Transactional(readOnly = true)
    public List<BookingHistoryResponseDTO> getVehicleBookingsByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        return vehicleBookingRepository.findByUserId(userId).stream()
                .map(this::mapVehicleBookingToDTO)
                .sorted(Comparator.comparing(BookingHistoryResponseDTO::getBookedDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Map RoomBooking entity to BookingHistoryResponseDTO
     */
    private BookingHistoryResponseDTO mapRoomBookingToDTO(RoomBooking booking) {
        BookingHistoryResponseDTO dto = new BookingHistoryResponseDTO();

        // Booking Type
        dto.setBookingType("HOTEL");

        // Common Booking Information
        dto.setBookingId(booking.getId());
        dto.setBookingReference(booking.getBookingReference());
        dto.setStatus(booking.getStatus() != null ? booking.getStatus().name() : null);
        dto.setBookedDate(booking.getCreatedAt());

        // Service Provider Information
        dto.setServiceProviderId(booking.getServiceProviderId());

        // Get room details
        Optional<Room> roomOptional = roomRepository.findById(booking.getRoomId());
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            dto.setServiceName(room.getName());
            dto.setServiceImage(room.getImages() != null && !room.getImages().isEmpty()
                ? room.getImages().get(0) : null);
            dto.setRoomType(room.getName());
        }

        // Customer Information
        dto.setUserId(booking.getUserId());
        dto.setCustomerName(booking.getGuestName());
        dto.setCustomerEmail(booking.getGuestEmail());
        dto.setCustomerPhone(booking.getGuestPhone());

        // Date Information
        dto.setStartDate(booking.getCheckInDate());
        dto.setEndDate(booking.getCheckOutDate());

        // Booking Details
        dto.setNumberOfPeople(booking.getNumberOfGuests());
        dto.setNumberOfNights(booking.getNumberOfNights());
        dto.setSpecialRequests(booking.getSpecialRequests());

        // Hotel Specific
        dto.setRoomId(booking.getRoomId());
        dto.setRoomNumber("Room #" + booking.getRoomId()); // You might want to add actual room number to entity

        // Pricing Information
        dto.setSubtotal(booking.getSubtotal());
        dto.setServiceCharge(booking.getServiceCharge());
        dto.setTaxes(booking.getTaxes());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setCurrency(booking.getCurrency());

        // Payment Information
        dto.setPaymentStatus(booking.getPaymentStatus() != null ? booking.getPaymentStatus().name() : null);
        dto.setPaymentMethod(booking.getPaymentMethod() != null ? booking.getPaymentMethod().name() : null);
        dto.setPaymentFailureReason(booking.getPaymentFailureReason());
        dto.setPaidAt(booking.getPaidAt());

        // Payment Integration
        dto.setStripeSessionId(booking.getStripeSessionId());
        dto.setStripePaymentIntentId(booking.getStripePaymentIntentId());

        // Timestamps
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());

        return dto;
    }

    /**
     * Map TourBooking entity to BookingHistoryResponseDTO
     */
    private BookingHistoryResponseDTO mapTourBookingToDTO(TourBooking booking) {
        BookingHistoryResponseDTO dto = new BookingHistoryResponseDTO();

        // Booking Type
        dto.setBookingType("TOUR");

        // Common Booking Information
        dto.setBookingId(booking.getId());
        dto.setBookingReference(booking.getBookingReference());
        dto.setStatus(booking.getStatus() != null ? booking.getStatus().name() : null);
        dto.setBookedDate(booking.getCreatedAt());

        // Service Provider Information
        dto.setServiceProviderId(booking.getServiceProviderId());

        // Get tour details
        Optional<Tour> tourOptional = tourRepository.findById(booking.getTourId());
        if (tourOptional.isPresent()) {
            Tour tour = tourOptional.get();
            dto.setServiceName(tour.getName());
            dto.setServiceImage(tour.getImage());
            dto.setTourDuration(tour.getDuration());

            // Create tour description from places
            if (tour.getPlaces() != null && !tour.getPlaces().isEmpty()) {
                dto.setTourDescription(String.join(", ", tour.getPlaces()));
            }
        }

        // Customer Information
        dto.setUserId(booking.getUserId());
        dto.setCustomerName(booking.getCustomerName());
        dto.setCustomerEmail(booking.getCustomerEmail());
        dto.setCustomerPhone(booking.getCustomerPhone());

        // Date Information
        dto.setStartDate(booking.getTourDate());
        dto.setEndDate(null); // Tours typically don't have end dates

        // Booking Details
        dto.setNumberOfPeople(booking.getNumberOfPeople());
        dto.setSpecialRequests(booking.getSpecialRequests());

        // Tour Specific
        dto.setTourId(booking.getTourId());

        // Pricing Information
        dto.setSubtotal(booking.getSubtotal());
        dto.setServiceCharge(booking.getServiceCharge());
        dto.setTaxes(booking.getTaxes());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setCurrency(booking.getCurrency());

        // Payment Information
        dto.setPaymentStatus(booking.getPaymentStatus() != null ? booking.getPaymentStatus().name() : null);
        dto.setPaymentMethod(booking.getPaymentMethod() != null ? booking.getPaymentMethod().name() : null);
        dto.setPaymentFailureReason(booking.getPaymentFailureReason());
        dto.setPaidAt(booking.getPaidAt());

        // Payment Integration
        dto.setStripeSessionId(booking.getStripeSessionId());
        dto.setStripePaymentIntentId(booking.getStripePaymentIntentId());

        // Timestamps
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());

        return dto;
    }

    /**
     * Map VehicleBooking entity to BookingHistoryResponseDTO
     */
    private BookingHistoryResponseDTO mapVehicleBookingToDTO(VehicleBooking booking) {
        BookingHistoryResponseDTO dto = new BookingHistoryResponseDTO();

        // Booking Type
        dto.setBookingType("TRAVEL_AGENCY");

        // Common Booking Information
        dto.setBookingId(booking.getId());
        dto.setBookingReference(booking.getBookingReference());
        dto.setStatus(booking.getStatus() != null ? booking.getStatus().name() : null);
        dto.setBookedDate(booking.getCreatedAt());

        // Service Provider Information
        dto.setServiceProviderId(booking.getServiceProviderId());

        // Get vehicle details
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(booking.getVehicleId());
        if (vehicleOptional.isPresent()) {
            Vehicle vehicle = vehicleOptional.get();
            dto.setServiceName(vehicle.getName());
            dto.setServiceImage(vehicle.getImage());
            dto.setVehicleType(vehicle.getType() != null ? vehicle.getType().name() : null);
            dto.setVehicleModel(vehicle.getBrand() + " " + vehicle.getModel());
        }

        // Customer Information
        dto.setUserId(booking.getUserId());
        dto.setCustomerName(booking.getCustomerName());
        dto.setCustomerEmail(booking.getCustomerEmail());
        dto.setCustomerPhone(booking.getCustomerPhone());

        // Date Information
        dto.setStartDate(booking.getPickupDate());
        dto.setEndDate(booking.getReturnDate());

        // Booking Details
        dto.setNumberOfDays(booking.getNumberOfDays());
        dto.setSpecialRequests(booking.getSpecialRequests());

        // Vehicle Specific
        dto.setVehicleId(booking.getVehicleId());
        dto.setPickupLocation(booking.getPickupLocation());
        dto.setDropoffLocation(booking.getDropoffLocation());
        dto.setEstimatedDistance(booking.getEstimatedDistance());
        dto.setWithAC(booking.getWithAC());

        // Pricing Information
        dto.setSubtotal(booking.getSubtotal());
        dto.setServiceCharge(booking.getServiceCharge());
        dto.setTaxes(booking.getTaxes());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setCurrency(booking.getCurrency());

        // Payment Information
        dto.setPaymentStatus(booking.getPaymentStatus() != null ? booking.getPaymentStatus().name() : null);
        dto.setPaymentMethod(booking.getPaymentMethod() != null ? booking.getPaymentMethod().name() : null);
        dto.setPaymentFailureReason(booking.getPaymentFailureReason());
        dto.setPaidAt(booking.getPaidAt());

        // Payment Integration
        dto.setStripeSessionId(booking.getStripeSessionId());
        dto.setStripePaymentIntentId(booking.getStripePaymentIntentId());

        // Timestamps
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());

        return dto;
    }
}
