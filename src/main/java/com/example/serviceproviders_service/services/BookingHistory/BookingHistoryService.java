package com.example.serviceproviders_service.services.BookingHistory;

import com.example.serviceproviders_service.dto.BookingHistory.BookingHistoryResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.HotelProfile;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.Room;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.RoomBooking;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.Tour;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourBooking;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourGuideProfile;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.AgencyProfile;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.Vehicle;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.VehicleBooking;
import com.example.serviceproviders_service.repository.ServiceProvider.AgencyProfileRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.Hotel.RoomBookingRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.Hotel.RoomRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.HotelProfileRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TourGuide.TourBookingRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TourGuide.TourRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TourGuideProfileRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TravelAgency.VehicleBookingRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TravelAgency.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingHistoryService {

    private final RoomBookingRepository roomBookingRepository;
    private final TourBookingRepository tourBookingRepository;
    private final VehicleBookingRepository vehicleBookingRepository;
    private final RoomRepository roomRepository;
    private final TourRepository tourRepository;
    private final VehicleRepository vehicleRepository;
    private final HotelProfileRepository hotelProfileRepository;
    private final TourGuideProfileRepository tourGuideProfileRepository;
    private final AgencyProfileRepository agencyProfileRepository;

    /**
     * Get all bookings for a specific user across all booking types
     */
    public List<BookingHistoryResponseDTO> getAllBookingsByUserId(Long userId) {
        List<BookingHistoryResponseDTO> allBookings = new ArrayList<>();

        // Fetch room bookings
        List<RoomBooking> roomBookings = roomBookingRepository.findByUserId(userId);
        allBookings.addAll(roomBookings.stream()
                .map(this::convertRoomBookingToDTO)
                .collect(Collectors.toList()));

        // Fetch tour bookings
        List<TourBooking> tourBookings = tourBookingRepository.findByUserId(userId);
        allBookings.addAll(tourBookings.stream()
                .map(this::convertTourBookingToDTO)
                .collect(Collectors.toList()));

        // Fetch vehicle bookings
        List<VehicleBooking> vehicleBookings = vehicleBookingRepository.findByUserId(userId);
        allBookings.addAll(vehicleBookings.stream()
                .map(this::convertVehicleBookingToDTO)
                .collect(Collectors.toList()));

        // Sort by creation date (newest first)
        allBookings.sort(Comparator.comparing(BookingHistoryResponseDTO::getCreatedAt).reversed());

        return allBookings;
    }

    /**
     * Get all room bookings for a user
     */
    public List<BookingHistoryResponseDTO> getRoomBookingsByUserId(Long userId) {
        List<RoomBooking> roomBookings = roomBookingRepository.findByUserId(userId);
        return roomBookings.stream()
                .map(this::convertRoomBookingToDTO)
                .sorted(Comparator.comparing(BookingHistoryResponseDTO::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get all tour bookings for a user
     */
    public List<BookingHistoryResponseDTO> getTourBookingsByUserId(Long userId) {
        List<TourBooking> tourBookings = tourBookingRepository.findByUserId(userId);
        return tourBookings.stream()
                .map(this::convertTourBookingToDTO)
                .sorted(Comparator.comparing(BookingHistoryResponseDTO::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get all vehicle bookings for a user
     */
    public List<BookingHistoryResponseDTO> getVehicleBookingsByUserId(Long userId) {
        List<VehicleBooking> vehicleBookings = vehicleBookingRepository.findByUserId(userId);
        return vehicleBookings.stream()
                .map(this::convertVehicleBookingToDTO)
                .sorted(Comparator.comparing(BookingHistoryResponseDTO::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get booking details by ID and type
     */
    public BookingHistoryResponseDTO getBookingById(Long bookingId, BookingHistoryResponseDTO.BookingType bookingType) {
        return switch (bookingType) {
            case ROOM_BOOKING -> roomBookingRepository.findById(bookingId)
                    .map(this::convertRoomBookingToDTO)
                    .orElseThrow(() -> new RuntimeException("Room booking not found with id: " + bookingId));
            case TOUR_BOOKING -> tourBookingRepository.findById(bookingId)
                    .map(this::convertTourBookingToDTO)
                    .orElseThrow(() -> new RuntimeException("Tour booking not found with id: " + bookingId));
            case VEHICLE_BOOKING -> vehicleBookingRepository.findById(bookingId)
                    .map(this::convertVehicleBookingToDTO)
                    .orElseThrow(() -> new RuntimeException("Vehicle booking not found with id: " + bookingId));
        };
    }

    // Conversion methods
    private BookingHistoryResponseDTO convertRoomBookingToDTO(RoomBooking booking) {
        BookingHistoryResponseDTO dto = new BookingHistoryResponseDTO();
        dto.setBookingId(booking.getId());
        dto.setBookingType(BookingHistoryResponseDTO.BookingType.ROOM_BOOKING);
        dto.setServiceProviderId(booking.getServiceProviderId());
        dto.setUserId(booking.getUserId());
        dto.setCustomerName(booking.getCustomerName());
        dto.setCustomerEmail(booking.getCustomerEmail());
        dto.setCustomerPhone(booking.getCustomerPhone());
        dto.setStartDate(booking.getCheckInDate());
        dto.setEndDate(booking.getCheckOutDate());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setStatus(booking.getStatus().name());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        dto.setSpecialRequests(booking.getSpecialRequests());
        dto.setCancellationReason(booking.getCancellationReason());

        // Get hotel name from HotelProfile
        hotelProfileRepository.findById(booking.getServiceProviderId())
                .ifPresent(profile -> dto.setServiceProviderName(profile.getHotelName()));

        // Room-specific details
        BookingHistoryResponseDTO.RoomBookingDetails roomDetails = new BookingHistoryResponseDTO.RoomBookingDetails();
        roomDetails.setRoomId(booking.getRoomId());
        roomDetails.setNumberOfGuests(booking.getNumberOfGuests());
        roomDetails.setNumberOfNights(booking.getNumberOfNights());
        roomDetails.setPricePerNight(booking.getPricePerNight());

        // Get room name
        roomRepository.findById(booking.getRoomId())
                .ifPresent(room -> roomDetails.setRoomType(room.getName()));

        dto.setRoomBookingDetails(roomDetails);

        return dto;
    }

    private BookingHistoryResponseDTO convertTourBookingToDTO(TourBooking booking) {
        BookingHistoryResponseDTO dto = new BookingHistoryResponseDTO();
        dto.setBookingId(booking.getId());
        dto.setBookingType(BookingHistoryResponseDTO.BookingType.TOUR_BOOKING);
        dto.setServiceProviderId(booking.getServiceProviderId());
        dto.setUserId(booking.getUserId());
        dto.setCustomerName(booking.getCustomerName());
        dto.setCustomerEmail(booking.getCustomerEmail());
        dto.setCustomerPhone(booking.getCustomerPhone());
        dto.setStartDate(booking.getTourDate());
        dto.setEndDate(null); // Tours typically are single-day
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setStatus(booking.getStatus().name());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        dto.setSpecialRequests(booking.getSpecialRequests());
        dto.setCancellationReason(booking.getCancellationReason());

        // Get company name from TourGuideProfile
        tourGuideProfileRepository.findById(booking.getServiceProviderId())
                .ifPresent(profile -> dto.setServiceProviderName(profile.getCompanyName()));

        // Tour-specific details
        BookingHistoryResponseDTO.TourBookingDetails tourDetails = new BookingHistoryResponseDTO.TourBookingDetails();
        tourDetails.setTourId(booking.getTourId());
        tourDetails.setNumberOfPeople(booking.getNumberOfPeople());
        tourDetails.setPricePerPerson(booking.getPricePerPerson());

        // Get tour name
        tourRepository.findById(booking.getTourId())
                .ifPresent(tour -> tourDetails.setTourTitle(tour.getName()));

        dto.setTourBookingDetails(tourDetails);

        return dto;
    }

    private BookingHistoryResponseDTO convertVehicleBookingToDTO(VehicleBooking booking) {
        BookingHistoryResponseDTO dto = new BookingHistoryResponseDTO();
        dto.setBookingId(booking.getId());
        dto.setBookingType(BookingHistoryResponseDTO.BookingType.VEHICLE_BOOKING);
        dto.setServiceProviderId(booking.getServiceProviderId());
        dto.setUserId(booking.getUserId());
        dto.setCustomerName(booking.getCustomerName());
        dto.setCustomerEmail(booking.getCustomerEmail());
        dto.setCustomerPhone(booking.getCustomerPhone());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setTotalAmount(booking.getEstimatedTotalAmount());
        dto.setStatus(booking.getStatus().name());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        dto.setSpecialRequests(booking.getSpecialRequests());
        dto.setCancellationReason(booking.getCancellationReason());

        // Get agency name from AgencyProfile
        agencyProfileRepository.findById(booking.getServiceProviderId())
                .ifPresent(profile -> dto.setServiceProviderName(profile.getAgencyName()));

        // Vehicle-specific details
        BookingHistoryResponseDTO.VehicleBookingDetails vehicleDetails = new BookingHistoryResponseDTO.VehicleBookingDetails();
        vehicleDetails.setVehicleId(booking.getVehicleId());
        vehicleDetails.setPickupLocation(booking.getPickupLocation());
        vehicleDetails.setDropoffLocation(booking.getDropoffLocation());
        vehicleDetails.setEstimatedKilometers(booking.getEstimatedKilometers());
        vehicleDetails.setWithAC(booking.getWithAC());
        vehicleDetails.setPricePerKm(booking.getPricePerKm());

        // Get vehicle details
        vehicleRepository.findById(booking.getVehicleId())
                .ifPresent(vehicle -> {
                    vehicleDetails.setVehicleType(vehicle.getType().name());
                    vehicleDetails.setVehicleBrand(vehicle.getBrand());
                    vehicleDetails.setVehicleModel(vehicle.getModel());
                });

        dto.setVehicleBookingDetails(vehicleDetails);

        return dto;
    }
}
