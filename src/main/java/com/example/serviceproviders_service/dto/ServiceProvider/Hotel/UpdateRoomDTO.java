package com.example.serviceproviders_service.dto.ServiceProvider.Hotel;

import com.example.serviceproviders_service.entity.serviceProvider.Hotel.Room;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class UpdateRoomDTO {
    private String name;
    private BigDecimal price;
    private Integer maxOccupancy;
    private Integer area;
    private String bedType;
    private Integer numberOfBeds;
    private Integer bathrooms;
    private List<String> amenities;
    private Room.RoomStatus status;
    private List<String> images;

    public UpdateRoomDTO() {}
}