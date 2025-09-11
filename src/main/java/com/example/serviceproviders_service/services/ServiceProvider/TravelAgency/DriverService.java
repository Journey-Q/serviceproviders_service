package com.example.serviceproviders_service.services.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.CreateDriverDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency.DriverResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProvider;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProviderType;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.Driver;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.repository.ServiceProvider.ServiceProviderRepo;
import com.example.serviceproviders_service.repository.ServiceProvider.TravelAgency.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private ServiceProviderRepo serviceProviderRepo;

    // Service type validation method
    private void validateTravelAgentAccess(Long serviceProviderId) {
        ServiceProvider serviceProvider = serviceProviderRepo.findById(serviceProviderId)
                .orElseThrow(() -> new BadRequestException("Service provider not found with id: " + serviceProviderId));

        if (!ServiceProviderType.TRAVEL_AGENT.equals(serviceProvider.getServiceType())) {
            throw new BadRequestException("Only travel agent service providers can manage drivers. Current service type: " + serviceProvider.getServiceType());
        }
    }

    @Transactional
    public DriverResponseDTO createDriver(CreateDriverDTO dto) {
        validateCreateDriverDTO(dto);

        // Validate service provider exists and is a travel agent
        validateTravelAgentAccess(dto.getServiceProviderId());

        if (driverRepository.existsByServiceProviderIdAndName(dto.getServiceProviderId(), dto.getName())) {
            throw new BadRequestException("Driver with this name already exists for this service provider");
        }

        if (driverRepository.existsByLicenseNumber(dto.getLicenseNumber())) {
            throw new BadRequestException("Driver with this license number already exists");
        }

        if (driverRepository.existsByContactNumber(dto.getContactNumber())) {
            throw new BadRequestException("Driver with this contact number already exists");
        }

        Driver driver = new Driver();
        driver.setServiceProviderId(dto.getServiceProviderId());
        driver.setName(dto.getName());
        driver.setProfilePhoto(dto.getProfilePhoto());
        driver.setExperience(dto.getExperience());
        driver.setLanguages(dto.getLanguages());
        driver.setContactNumber(dto.getContactNumber());
        driver.setLicenseNumber(dto.getLicenseNumber());
        driver.setRating(dto.getRating() != null ? dto.getRating() : BigDecimal.valueOf(0.0));
        driver.setStatus(dto.getStatus() != null ? dto.getStatus() : Driver.DriverStatus.AVAILABLE);

        Driver savedDriver = driverRepository.save(driver);
        return DriverResponseDTO.fromEntity(savedDriver);
    }

    @Transactional(readOnly = true)
    public DriverResponseDTO getDriverById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid driver ID");
        }
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Driver not found with id: " + id));
        return DriverResponseDTO.fromEntity(driver);
    }

    @Transactional(readOnly = true)
    public List<DriverResponseDTO> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(DriverResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DriverResponseDTO> getDriversByServiceProviderId(Long serviceProviderId) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }

        // Validate service provider exists and is a travel agent
        validateTravelAgentAccess(serviceProviderId);

        return driverRepository.findByServiceProviderId(serviceProviderId)
                .stream()
                .map(DriverResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DriverResponseDTO> getDriversByServiceProviderIdAndStatus(Long serviceProviderId, Driver.DriverStatus status) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }
        if (status == null) {
            throw new BadRequestException("Driver status is required");
        }

        // Validate service provider exists and is a travel agent
        validateTravelAgentAccess(serviceProviderId);

        return driverRepository.findByServiceProviderIdAndStatus(serviceProviderId, status)
                .stream()
                .map(DriverResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DriverResponseDTO> searchDrivers(Long serviceProviderId, String searchTerm) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new BadRequestException("Search term is required");
        }

        // Validate service provider exists and is a travel agent
        validateTravelAgentAccess(serviceProviderId);

        return driverRepository.findByServiceProviderIdAndNameOrLicenseContaining(serviceProviderId, searchTerm.trim())
                .stream()
                .map(DriverResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public DriverResponseDTO updateDriver(Long id, CreateDriverDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid driver ID");
        }

        validateCreateDriverDTO(dto);

        Driver existingDriver = driverRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Driver not found with id: " + id));

        // Validate service provider exists and is a travel agent
        validateTravelAgentAccess(dto.getServiceProviderId());

        // Additional security check: Ensure the existing driver belongs to a travel agent
        validateTravelAgentAccess(existingDriver.getServiceProviderId());

        if (driverRepository.existsByServiceProviderIdAndNameAndIdNot(dto.getServiceProviderId(), dto.getName(), id)) {
            throw new BadRequestException("Driver with this name already exists for this service provider");
        }

        if (driverRepository.existsByLicenseNumberAndIdNot(dto.getLicenseNumber(), id)) {
            throw new BadRequestException("Driver with this license number already exists");
        }

        if (driverRepository.existsByContactNumberAndIdNot(dto.getContactNumber(), id)) {
            throw new BadRequestException("Driver with this contact number already exists");
        }

        existingDriver.setServiceProviderId(dto.getServiceProviderId());
        existingDriver.setName(dto.getName());
        existingDriver.setProfilePhoto(dto.getProfilePhoto());
        existingDriver.setExperience(dto.getExperience());
        existingDriver.setLanguages(dto.getLanguages());
        existingDriver.setContactNumber(dto.getContactNumber());
        existingDriver.setLicenseNumber(dto.getLicenseNumber());
        existingDriver.setRating(dto.getRating() != null ? dto.getRating() : existingDriver.getRating());
        existingDriver.setStatus(dto.getStatus() != null ? dto.getStatus() : existingDriver.getStatus());

        Driver updatedDriver = driverRepository.save(existingDriver);
        return DriverResponseDTO.fromEntity(updatedDriver);
    }

    @Transactional
    public boolean deleteDriver(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid driver ID");
        }
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Driver not found with id: " + id));

        // Validate that the driver belongs to a travel agent service provider
        validateTravelAgentAccess(driver.getServiceProviderId());

        driverRepository.delete(driver);
        return true;
    }

    @Transactional
    public DriverResponseDTO updateDriverStatus(Long id, Driver.DriverStatus status) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid driver ID");
        }
        if (status == null) {
            throw new BadRequestException("Driver status is required");
        }

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Driver not found with id: " + id));

        // Validate that the driver belongs to a travel agent service provider
        validateTravelAgentAccess(driver.getServiceProviderId());

        driver.setStatus(status);
        Driver updatedDriver = driverRepository.save(driver);
        return DriverResponseDTO.fromEntity(updatedDriver);
    }

    @Transactional
    public DriverResponseDTO updateDriverRating(Long id, BigDecimal rating) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid driver ID");
        }
        if (rating == null || rating.compareTo(BigDecimal.valueOf(0)) < 0 || rating.compareTo(BigDecimal.valueOf(5)) > 0) {
            throw new BadRequestException("Rating must be between 0 and 5");
        }

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Driver not found with id: " + id));

        // Validate that the driver belongs to a travel agent service provider
        validateTravelAgentAccess(driver.getServiceProviderId());

        driver.setRating(rating);
        Driver updatedDriver = driverRepository.save(driver);
        return DriverResponseDTO.fromEntity(updatedDriver);
    }

    private void validateCreateDriverDTO(CreateDriverDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Driver data cannot be null");
        }
        if (dto.getServiceProviderId() == null || dto.getServiceProviderId() <= 0) {
            throw new BadRequestException("Service Provider ID is required and must be positive");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BadRequestException("Driver name is required");
        }
        if (dto.getName().length() > 200) {
            throw new BadRequestException("Driver name cannot exceed 200 characters");
        }
        if (dto.getExperience() == null || dto.getExperience() < 0) {
            throw new BadRequestException("Experience must be 0 or greater");
        }
        if (dto.getExperience() > 50) {
            throw new BadRequestException("Experience cannot exceed 50 years");
        }
        if (dto.getLanguages() == null || dto.getLanguages().isEmpty()) {
            throw new BadRequestException("At least one language is required");
        }
        if (dto.getContactNumber() == null || dto.getContactNumber().trim().isEmpty()) {
            throw new BadRequestException("Contact number is required");
        }
        if (dto.getLicenseNumber() == null || dto.getLicenseNumber().trim().isEmpty()) {
            throw new BadRequestException("License number is required");
        }
        if (dto.getRating() != null && (dto.getRating().compareTo(BigDecimal.valueOf(0)) < 0 || dto.getRating().compareTo(BigDecimal.valueOf(5)) > 0)) {
            throw new BadRequestException("Rating must be between 0 and 5");
        }
    }
}