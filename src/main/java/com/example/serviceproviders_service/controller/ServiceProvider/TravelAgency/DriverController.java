package com.example.serviceproviders_service.controller.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.CreateDriverDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.DriverResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.Driver;
import com.example.serviceproviders_service.services.ServiceProvider.TravelAgency.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/service/drivers")
@CrossOrigin(origins = "*")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping("/create")
    public ResponseEntity<DriverResponseDTO> createDriver(@RequestBody CreateDriverDTO dto) {
        DriverResponseDTO createdDriver = driverService.createDriver(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDriver);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> getDriverById(@PathVariable Long id) {
        DriverResponseDTO driver = driverService.getDriverById(id);
        return ResponseEntity.ok(driver);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers() {
        List<DriverResponseDTO> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/service-provider/{serviceProviderId}")
    public ResponseEntity<List<DriverResponseDTO>> getDriversByServiceProviderId(@PathVariable Long serviceProviderId) {
        List<DriverResponseDTO> drivers = driverService.getDriversByServiceProviderId(serviceProviderId);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/service-provider/{serviceProviderId}/status/{status}")
    public ResponseEntity<List<DriverResponseDTO>> getDriversByServiceProviderIdAndStatus(
            @PathVariable Long serviceProviderId,
            @PathVariable Driver.DriverStatus status) {
        List<DriverResponseDTO> drivers = driverService.getDriversByServiceProviderIdAndStatus(serviceProviderId, status);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/service-provider/{serviceProviderId}/search")
    public ResponseEntity<List<DriverResponseDTO>> searchDrivers(
            @PathVariable Long serviceProviderId,
            @RequestParam String searchTerm) {
        List<DriverResponseDTO> drivers = driverService.searchDrivers(serviceProviderId, searchTerm);
        return ResponseEntity.ok(drivers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> updateDriver(
            @PathVariable Long id,
            @RequestBody CreateDriverDTO dto) {
        DriverResponseDTO updatedDriver = driverService.updateDriver(id, dto);
        return ResponseEntity.ok(updatedDriver);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DriverResponseDTO> updateDriverStatus(
            @PathVariable Long id,
            @RequestParam Driver.DriverStatus status) {
        DriverResponseDTO updatedDriver = driverService.updateDriverStatus(id, status);
        return ResponseEntity.ok(updatedDriver);
    }

    @PatchMapping("/{id}/rating")
    public ResponseEntity<DriverResponseDTO> updateDriverRating(
            @PathVariable Long id,
            @RequestParam BigDecimal rating) {
        DriverResponseDTO updatedDriver = driverService.updateDriverRating(id, rating);
        return ResponseEntity.ok(updatedDriver);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDriver(@PathVariable Long id) {
        boolean response = driverService.deleteDriver(id);
        if (response) {
            return ResponseEntity.ok("Driver deleted successfully");
        }
        return ResponseEntity.noContent().build();
    }
}
