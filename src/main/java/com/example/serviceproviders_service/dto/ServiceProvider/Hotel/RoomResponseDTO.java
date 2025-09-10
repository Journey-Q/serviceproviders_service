package com.example.serviceproviders_service.dto.ServiceProvider.Hotel;

import com.example.serviceproviders_service.entity.serviceProvider.Hotel.Room;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RoomResponseDTO {
    private Long id;
    private Long serviceProviderId;
    private String name;
    private BigDecimal price;
    private Integer maxOccupancy;
    private Integer area;
    private String beds;
    private Integer bathrooms;
    private List<String> amenities;
    private Room.RoomStatus status;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RoomResponseDTO() {}

    public static RoomResponseDTO fromEntity(Room room) {
        RoomResponseDTO dto = new RoomResponseDTO();
        dto.setId(room.getId());
        dto.setServiceProviderId(room.getServiceProviderId());
        dto.setName(room.getName());
        dto.setPrice(room.getPrice());
        dto.setMaxOccupancy(room.getMaxOccupancy());
        dto.setArea(room.getArea());
        dto.setBeds(room.getBeds());
        dto.setBathrooms(room.getBathrooms());
        dto.setAmenities(room.getAmenities());
        dto.setStatus(room.getStatus());
        dto.setImage(room.getImage());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setUpdatedAt(room.getUpdatedAt());
        return dto;
    }
}
