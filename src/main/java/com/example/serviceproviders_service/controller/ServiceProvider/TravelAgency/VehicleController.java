package com.example.serviceproviders_service.controller.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.CreateVehicleDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.VehicleResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.Vehicle;
import com.example.serviceproviders_service.services.ServiceProvider.TravelAgency.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping("/create")
    public ResponseEntity<VehicleResponseDTO> createVehicle(@RequestBody CreateVehicleDTO dto) {
        VehicleResponseDTO createdVehicle = vehicleService.createVehicle(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicle);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getVehicleById(@PathVariable Long id) {
        VehicleResponseDTO vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping("/all")
    public ResponseEntity<List<VehicleResponseDTO>> getAllVehicles() {
        List<VehicleResponseDTO> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/service-provider/{serviceProviderId}")
    public ResponseEntity<List<VehicleResponseDTO>> getVehiclesByServiceProviderId(@PathVariable Long serviceProviderId) {
        List<VehicleResponseDTO> vehicles = vehicleService.getVehiclesByServiceProviderId(serviceProviderId);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/service-provider/{serviceProviderId}/status/{status}")
    public ResponseEntity<List<VehicleResponseDTO>> getVehiclesByServiceProviderIdAndStatus(
            @PathVariable Long serviceProviderId,
            @PathVariable Vehicle.VehicleStatus status) {
        List<VehicleResponseDTO> vehicles = vehicleService.getVehiclesByServiceProviderIdAndStatus(serviceProviderId, status);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/service-provider/{serviceProviderId}/type/{type}")
    public ResponseEntity<List<VehicleResponseDTO>> getVehiclesByServiceProviderIdAndType(
            @PathVariable Long serviceProviderId,
            @PathVariable Vehicle.VehicleType type) {
        List<VehicleResponseDTO> vehicles = vehicleService.getVehiclesByServiceProviderIdAndType(serviceProviderId, type);
        return ResponseEntity.ok(vehicles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> updateVehicle(
            @PathVariable Long id,
            @RequestBody CreateVehicleDTO dto) {
        VehicleResponseDTO updatedVehicle = vehicleService.updateVehicle(id, dto);
        return ResponseEntity.ok(updatedVehicle);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<VehicleResponseDTO> updateVehicleStatus(
            @PathVariable Long id,
            @RequestParam Vehicle.VehicleStatus status) {
        VehicleResponseDTO updatedVehicle = vehicleService.updateVehicleStatus(id, status);
        return ResponseEntity.ok(updatedVehicle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable Long id) {
        boolean response = vehicleService.deleteVehicle(id);
        if (response) {
            return ResponseEntity.ok("Vehicle deleted successfully");
        }
        return ResponseEntity.noContent().build();
    }
}