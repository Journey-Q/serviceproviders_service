package com.example.serviceproviders_service.controller.ServiceProvider.Hotel;

import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.CreateRoomDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.RoomResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.Room;
import com.example.serviceproviders_service.services.ServiceProvider.Hotel.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/create")
    public ResponseEntity<RoomResponseDTO> createRoom(@RequestBody CreateRoomDTO dto) {
        RoomResponseDTO createdRoom = roomService.createRoom(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> getRoomById(@PathVariable Long id) {
        RoomResponseDTO room = roomService.getRoomById(id);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RoomResponseDTO>> getAllRooms() {
        List<RoomResponseDTO> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/service-provider/{serviceProviderId}")
    public ResponseEntity<List<RoomResponseDTO>> getRoomsByServiceProviderId(@PathVariable Long serviceProviderId) {
        List<RoomResponseDTO> rooms = roomService.getRoomsByServiceProviderId(serviceProviderId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/service-provider/{serviceProviderId}/status/{status}")
    public ResponseEntity<List<RoomResponseDTO>> getRoomsByServiceProviderIdAndStatus(
            @PathVariable Long serviceProviderId,
            @PathVariable Room.RoomStatus status) {
        List<RoomResponseDTO> rooms = roomService.getRoomsByServiceProviderIdAndStatus(serviceProviderId, status);
        return ResponseEntity.ok(rooms);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> updateRoom(
            @PathVariable Long id,
            @RequestBody CreateRoomDTO dto) {
        RoomResponseDTO updatedRoom = roomService.updateRoom(id, dto);
        return ResponseEntity.ok(updatedRoom);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RoomResponseDTO> updateRoomStatus(
            @PathVariable Long id,
            @RequestParam Room.RoomStatus status) {
        RoomResponseDTO updatedRoom = roomService.updateRoomStatus(id, status);
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long id) {
        boolean response = roomService.deleteRoom(id);
        if (response) {
            return ResponseEntity.ok("Room deleted successfully");
        }
        return ResponseEntity.noContent().build();
    }
}