package com.example.serviceproviders_service.services.ServiceProvider.Hotel;

import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.CreateRoomDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.RoomResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.Room;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.repository.ServiceProvider.HotelProfileRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.Hotel.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelProfileRepository hotelProfileRepository;

    @Transactional
    public RoomResponseDTO createRoom(CreateRoomDTO dto) {
        validateCreateRoomDTO(dto);

        if (!hotelProfileRepository.existsById(dto.getServiceProviderId())) {
            throw new BadRequestException("Service provider profile not found with id: " + dto.getServiceProviderId());
        }

        if (roomRepository.existsByServiceProviderIdAndName(dto.getServiceProviderId(), dto.getName())) {
            throw new BadRequestException("Room with this name already exists for this service provider");
        }

        String bedDescription = createBedDescription(dto.getNumberOfBeds(), dto.getBedType());

        Room room = new Room();
        room.setServiceProviderId(dto.getServiceProviderId());
        room.setName(dto.getName());
        room.setPrice(dto.getPrice());
        room.setMaxOccupancy(dto.getMaxOccupancy());
        room.setArea(dto.getArea());
        room.setBeds(bedDescription);
        room.setBathrooms(dto.getBathrooms());
        room.setAmenities(dto.getAmenities());
        room.setStatus(dto.getStatus() != null ? dto.getStatus() : Room.RoomStatus.AVAILABLE);
        room.setImages(dto.getImages());

        // Validate that at least one image is provided
        if (dto.getImages() == null || dto.getImages().isEmpty()) {
            throw new BadRequestException("At least one room image is required");
        }

        Room savedRoom = roomRepository.save(room);
        return RoomResponseDTO.fromEntity(savedRoom);
    }

    @Transactional(readOnly = true)
    public RoomResponseDTO getRoomById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid room ID");
        }
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Room not found with id: " + id));
        return RoomResponseDTO.fromEntity(room);
    }

    @Transactional(readOnly = true)
    public List<RoomResponseDTO> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(RoomResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomResponseDTO> getRoomsByServiceProviderId(Long serviceProviderId) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }
        return roomRepository.findByServiceProviderId(serviceProviderId)
                .stream()
                .map(RoomResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomResponseDTO> getRoomsByServiceProviderIdAndStatus(Long serviceProviderId, Room.RoomStatus status) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }
        if (status == null) {
            throw new BadRequestException("Room status is required");
        }
        return roomRepository.findByServiceProviderIdAndStatus(serviceProviderId, status)
                .stream()
                .map(RoomResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomResponseDTO updateRoom(Long id, CreateRoomDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid room ID");
        }

        validateCreateRoomDTO(dto);

        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Room not found with id: " + id));

        if (!hotelProfileRepository.existsById(dto.getServiceProviderId())) {
            throw new BadRequestException("Service provider profile not found with id: " + dto.getServiceProviderId());
        }

        if (roomRepository.existsByServiceProviderIdAndNameAndIdNot(dto.getServiceProviderId(), dto.getName(), id)) {
            throw new BadRequestException("Room with this name already exists for this service provider");
        }

        String bedDescription = createBedDescription(dto.getNumberOfBeds(), dto.getBedType());

        existingRoom.setServiceProviderId(dto.getServiceProviderId());
        existingRoom.setName(dto.getName());
        existingRoom.setPrice(dto.getPrice());
        existingRoom.setMaxOccupancy(dto.getMaxOccupancy());
        existingRoom.setArea(dto.getArea());
        existingRoom.setBeds(bedDescription);
        existingRoom.setBathrooms(dto.getBathrooms());
        existingRoom.setAmenities(dto.getAmenities());
        existingRoom.setStatus(dto.getStatus() != null ? dto.getStatus() : existingRoom.getStatus());
        existingRoom.setImages(dto.getImages());

        Room updatedRoom = roomRepository.save(existingRoom);
        return RoomResponseDTO.fromEntity(updatedRoom);
    }

    @Transactional
    public boolean deleteRoom(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid room ID");
        }
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Room not found with id: " + id));

        roomRepository.delete(room);
        return true;
    }

    @Transactional
    public RoomResponseDTO updateRoomStatus(Long id, Room.RoomStatus status) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid room ID");
        }
        if (status == null) {
            throw new BadRequestException("Room status is required");
        }

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Room not found with id: " + id));

        room.setStatus(status);
        Room updatedRoom = roomRepository.save(room);
        return RoomResponseDTO.fromEntity(updatedRoom);
    }

    private void validateCreateRoomDTO(CreateRoomDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Room data cannot be null");
        }
        if (dto.getServiceProviderId() == null || dto.getServiceProviderId() <= 0) {
            throw new BadRequestException("Service Provider ID is required and must be positive");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BadRequestException("Room name is required");
        }
        if (dto.getName().length() > 100) {
            throw new BadRequestException("Room name cannot exceed 100 characters");
        }
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Price must be positive");
        }
        if (dto.getMaxOccupancy() == null || dto.getMaxOccupancy() <= 0) {
            throw new BadRequestException("Maximum occupancy must be positive");
        }
        if (dto.getMaxOccupancy() > 10) {
            throw new BadRequestException("Maximum occupancy cannot exceed 10");
        }
        if (dto.getArea() == null || dto.getArea() <= 0) {
            throw new BadRequestException("Room area must be positive");
        }
        if (dto.getBedType() == null || dto.getBedType().trim().isEmpty()) {
            throw new BadRequestException("Bed type is required");
        }
        if (dto.getNumberOfBeds() == null || dto.getNumberOfBeds() <= 0) {
            throw new BadRequestException("Number of beds must be positive");
        }
        if (dto.getNumberOfBeds() > 4) {
            throw new BadRequestException("Number of beds cannot exceed 4");
        }
        if (dto.getBathrooms() == null || dto.getBathrooms() <= 0) {
            throw new BadRequestException("Number of bathrooms must be positive");
        }
        if (dto.getBathrooms() > 5) {
            throw new BadRequestException("Number of bathrooms cannot exceed 5");
        }
    }

    private String createBedDescription(Integer numberOfBeds, String bedType) {
        if (numberOfBeds == null || bedType == null) {
            return "Unknown bed configuration";
        }
        return numberOfBeds + " " + bedType + (numberOfBeds > 1 ? " beds" : " bed");
    }
}

