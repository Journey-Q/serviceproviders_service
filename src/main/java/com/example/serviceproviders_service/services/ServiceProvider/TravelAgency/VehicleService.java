package com.example.serviceproviders_service.services.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.CreateVehicleDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.VehicleResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProvider;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProviderType;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.Vehicle;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.repository.ServiceProvider.ServiceProviderRepo;
import com.example.serviceproviders_service.repository.ServiceProvider.TravelAgency.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ServiceProviderRepo serviceProviderRepo;

    // Service type validation method
    private void validateTravelAgentAccess(Long serviceProviderId) {
        ServiceProvider serviceProvider = serviceProviderRepo.findById(serviceProviderId)
                .orElseThrow(() -> new BadRequestException("Service provider not found with id: " + serviceProviderId));

        if (!ServiceProviderType.TRAVEL_AGENT.equals(serviceProvider.getServiceType())) {
            throw new BadRequestException("Only travel agent service providers can manage vehicles. Current service type: " + serviceProvider.getServiceType());
        }
    }

    @Transactional
    public VehicleResponseDTO createVehicle(CreateVehicleDTO dto) {
        validateCreateVehicleDTO(dto);

        // Validate service provider exists and is a travel agent
        validateTravelAgentAccess(dto.getServiceProviderId());

        if (vehicleRepository.existsByServiceProviderIdAndName(dto.getServiceProviderId(), dto.getName())) {
            throw new BadRequestException("Vehicle with this name already exists for this service provider");
        }

        if (vehicleRepository.existsByLicensePlate(dto.getLicensePlate())) {
            throw new BadRequestException("Vehicle with this license plate already exists");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setServiceProviderId(dto.getServiceProviderId());
        vehicle.setName(dto.getName());
        vehicle.setImage(dto.getImage());
        vehicle.setType(dto.getType());
        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        vehicle.setYear(dto.getYear());
        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setAc(dto.getAc() != null ? dto.getAc() : true);
        vehicle.setNumberOfSeats(dto.getNumberOfSeats());
        vehicle.setPricePerKmWithAC(dto.getPricePerKmWithAC());
        vehicle.setPricePerKmWithoutAC(dto.getPricePerKmWithoutAC());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setTransmission(dto.getTransmission());
        vehicle.setFeatures(dto.getFeatures());
        vehicle.setStatus(dto.getStatus() != null ? dto.getStatus() : Vehicle.VehicleStatus.AVAILABLE);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return VehicleResponseDTO.fromEntity(savedVehicle);
    }

    @Transactional(readOnly = true)
    public VehicleResponseDTO getVehicleById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid vehicle ID");
        }
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Vehicle not found with id: " + id));
        return VehicleResponseDTO.fromEntity(vehicle);
    }

    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(VehicleResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> getVehiclesByServiceProviderId(Long serviceProviderId) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }

        // Validate service provider exists and is a travel agent
        validateTravelAgentAccess(serviceProviderId);

        return vehicleRepository.findByServiceProviderId(serviceProviderId)
                .stream()
                .map(VehicleResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> getVehiclesByServiceProviderIdAndStatus(Long serviceProviderId, Vehicle.VehicleStatus status) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }
        if (status == null) {
            throw new BadRequestException("Vehicle status is required");
        }

        // Validate service provider exists and is a travel agent
        validateTravelAgentAccess(serviceProviderId);

        return vehicleRepository.findByServiceProviderIdAndStatus(serviceProviderId, status)
                .stream()
                .map(VehicleResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> getVehiclesByServiceProviderIdAndType(Long serviceProviderId, Vehicle.VehicleType type) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }
        if (type == null) {
            throw new BadRequestException("Vehicle type is required");
        }

        // Validate service provider exists and is a travel agent
        validateTravelAgentAccess(serviceProviderId);

        return vehicleRepository.findByServiceProviderIdAndType(serviceProviderId, type)
                .stream()
                .map(VehicleResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public VehicleResponseDTO updateVehicle(Long id, CreateVehicleDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid vehicle ID");
        }

        validateCreateVehicleDTO(dto);

        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Vehicle not found with id: " + id));

        // Validate service provider exists and is a travel agent
        validateTravelAgentAccess(dto.getServiceProviderId());

        // Additional security check: Ensure the existing vehicle belongs to a travel agent
        validateTravelAgentAccess(existingVehicle.getServiceProviderId());

        if (vehicleRepository.existsByServiceProviderIdAndNameAndIdNot(dto.getServiceProviderId(), dto.getName(), id)) {
            throw new BadRequestException("Vehicle with this name already exists for this service provider");
        }

        if (vehicleRepository.existsByLicensePlateAndIdNot(dto.getLicensePlate(), id)) {
            throw new BadRequestException("Vehicle with this license plate already exists");
        }

        existingVehicle.setServiceProviderId(dto.getServiceProviderId());
        existingVehicle.setName(dto.getName());
        existingVehicle.setImage(dto.getImage());
        existingVehicle.setType(dto.getType());
        existingVehicle.setBrand(dto.getBrand());
        existingVehicle.setModel(dto.getModel());
        existingVehicle.setYear(dto.getYear());
        existingVehicle.setLicensePlate(dto.getLicensePlate());
        existingVehicle.setAc(dto.getAc() != null ? dto.getAc() : existingVehicle.getAc());
        existingVehicle.setNumberOfSeats(dto.getNumberOfSeats());
        existingVehicle.setPricePerKmWithAC(dto.getPricePerKmWithAC());
        existingVehicle.setPricePerKmWithoutAC(dto.getPricePerKmWithoutAC());
        existingVehicle.setFuelType(dto.getFuelType());
        existingVehicle.setTransmission(dto.getTransmission());
        existingVehicle.setFeatures(dto.getFeatures());
        existingVehicle.setStatus(dto.getStatus() != null ? dto.getStatus() : existingVehicle.getStatus());

        Vehicle updatedVehicle = vehicleRepository.save(existingVehicle);
        return VehicleResponseDTO.fromEntity(updatedVehicle);
    }

    @Transactional
    public boolean deleteVehicle(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid vehicle ID");
        }
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Vehicle not found with id: " + id));

        // Validate that the vehicle belongs to a travel agent service provider
        validateTravelAgentAccess(vehicle.getServiceProviderId());

        vehicleRepository.delete(vehicle);
        return true;
    }

    @Transactional
    public VehicleResponseDTO updateVehicleStatus(Long id, Vehicle.VehicleStatus status) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid vehicle ID");
        }
        if (status == null) {
            throw new BadRequestException("Vehicle status is required");
        }

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Vehicle not found with id: " + id));

        // Validate that the vehicle belongs to a travel agent service provider
        validateTravelAgentAccess(vehicle.getServiceProviderId());

        vehicle.setStatus(status);
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return VehicleResponseDTO.fromEntity(updatedVehicle);
    }

    private void validateCreateVehicleDTO(CreateVehicleDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Vehicle data cannot be null");
        }
        if (dto.getServiceProviderId() == null || dto.getServiceProviderId() <= 0) {
            throw new BadRequestException("Service Provider ID is required and must be positive");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BadRequestException("Vehicle name is required");
        }
        if (dto.getName().length() > 200) {
            throw new BadRequestException("Vehicle name cannot exceed 200 characters");
        }
        if (dto.getType() == null) {
            throw new BadRequestException("Vehicle type is required");
        }
        if (dto.getBrand() == null || dto.getBrand().trim().isEmpty()) {
            throw new BadRequestException("Vehicle brand is required");
        }
        if (dto.getModel() == null || dto.getModel().trim().isEmpty()) {
            throw new BadRequestException("Vehicle model is required");
        }
        if (dto.getYear() == null || dto.getYear() < 1900 || dto.getYear() > (java.time.Year.now().getValue() + 1)) {
            throw new BadRequestException("Vehicle year must be between 1900 and " + (java.time.Year.now().getValue() + 1));
        }
        if (dto.getLicensePlate() == null || dto.getLicensePlate().trim().isEmpty()) {
            throw new BadRequestException("License plate is required");
        }
        if (dto.getNumberOfSeats() == null || dto.getNumberOfSeats() <= 0) {
            throw new BadRequestException("Number of seats must be positive");
        }
        if (dto.getNumberOfSeats() > 100) {
            throw new BadRequestException("Number of seats cannot exceed 100");
        }
        if (dto.getPricePerKmWithAC() == null || dto.getPricePerKmWithAC().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Price per km with AC must be positive");
        }
        if (dto.getPricePerKmWithoutAC() == null || dto.getPricePerKmWithoutAC().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Price per km without AC must be positive");
        }
        if (dto.getFuelType() == null) {
            throw new BadRequestException("Fuel type is required");
        }
        if (dto.getTransmission() == null) {
            throw new BadRequestException("Transmission type is required");
        }
    }
}