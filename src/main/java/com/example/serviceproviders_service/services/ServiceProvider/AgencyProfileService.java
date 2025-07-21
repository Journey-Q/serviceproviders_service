// Service
package com.example.serviceproviders_service.services.ServiceProvider;

import com.example.serviceproviders_service.dto.ServiceProvider.CreateAgencyProfileDTO;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.AgencyProfile;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.AgencyInfo;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.ContactInfo;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.repository.ServiceProvider.AgencyProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class AgencyProfileService {

    @Autowired
    private AgencyProfileRepository agencyProfileRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    @Transactional
    public AgencyProfile createAgencyProfile(CreateAgencyProfileDTO dto) {
        // Basic validation
        if (dto == null) {
            throw new BadRequestException("Agency profile data cannot be null");
        }
        if (dto.getAgencyName() == null || dto.getAgencyName().trim().isEmpty()) {
            throw new BadRequestException("Agency name is required");
        }

        if (dto.getServiceProviderId() == null || dto.getServiceProviderId() <= 0) {
            throw new BadRequestException("Service provider ID is required");
        }

        if (agencyProfileRepository.existsByServiceProviderId(dto.getServiceProviderId())) {
            throw new BadRequestException("Service provider already has an agency profile");
        }

        // Validate email if provided
        if (dto.getContactInfo() != null && dto.getContactInfo().getEmail() != null) {
            if (!EMAIL_PATTERN.matcher(dto.getContactInfo().getEmail()).matches()) {
                throw new BadRequestException("Invalid email format");
            }
        }

        // Check for duplicate agency
        boolean agencyExists = agencyProfileRepository.existsByAgencyName(dto.getAgencyName());
        if (agencyExists) {
            throw new BadRequestException("Agency with this name already exists");
        }

        AgencyProfile agencyProfile = new AgencyProfile();
        agencyProfile.setServiceProviderId(dto.getServiceProviderId());
        agencyProfile.setAgencyName(dto.getAgencyName());
        agencyProfile.setProfilePhoto(dto.getProfilePhoto());
        agencyProfile.setDescription(dto.getDescription());

        if (dto.getAgencyInfo() != null) {
            agencyProfile.setAgencyInfo(new AgencyInfo(
                    dto.getAgencyInfo().getEstablishedYear(),
                    dto.getAgencyInfo().getFleetSize()
            ));
        }

        if (dto.getContactInfo() != null) {
            agencyProfile.setContactInfo(new ContactInfo(
                    dto.getContactInfo().getPhone(),
                    dto.getContactInfo().getAddress(),
                    dto.getContactInfo().getEmail()
            ));
        }

        return agencyProfileRepository.save(agencyProfile);
    }

    @Transactional(readOnly = true)
    public AgencyProfile getAgencyProfileById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid agency profile ID");
        }
        return agencyProfileRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Agency profile not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<AgencyProfile> getAllAgencyProfiles() {
        return agencyProfileRepository.findAll();
    }

    @Transactional
    public AgencyProfile updateAgencyProfile(Long id, CreateAgencyProfileDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid agency profile ID");
        }
        if (dto == null) {
            throw new BadRequestException("Agency profile data cannot be null");
        }
        if (dto.getAgencyName() == null || dto.getAgencyName().trim().isEmpty()) {
            throw new BadRequestException("Agency name is required");
        }

        // Validate email if provided
        if (dto.getContactInfo() != null && dto.getContactInfo().getEmail() != null) {
            if (!EMAIL_PATTERN.matcher(dto.getContactInfo().getEmail()).matches()) {
                throw new BadRequestException("Invalid email format");
            }
        }

        AgencyProfile existingProfile = getAgencyProfileById(id);

        // Check for duplicate agency (excluding current one)
        boolean agencyExists = agencyProfileRepository.existsByAgencyNameAndServiceProviderIdNot(dto.getAgencyName(), id);
        if (agencyExists) {
            throw new BadRequestException("Agency with this name already exists");
        }

        existingProfile.setAgencyName(dto.getAgencyName());
        existingProfile.setProfilePhoto(dto.getProfilePhoto());
        existingProfile.setDescription(dto.getDescription());

        if (dto.getAgencyInfo() != null) {
            if (existingProfile.getAgencyInfo() == null) {
                existingProfile.setAgencyInfo(new AgencyInfo());
            }
            existingProfile.getAgencyInfo().setEstablishedYear(dto.getAgencyInfo().getEstablishedYear());
            existingProfile.getAgencyInfo().setFleetSize(dto.getAgencyInfo().getFleetSize());
        }

        if (dto.getContactInfo() != null) {
            if (existingProfile.getContactInfo() == null) {
                existingProfile.setContactInfo(new ContactInfo());
            }
            existingProfile.getContactInfo().setPhone(dto.getContactInfo().getPhone());
            existingProfile.getContactInfo().setAddress(dto.getContactInfo().getAddress());
            existingProfile.getContactInfo().setEmail(dto.getContactInfo().getEmail());
        }

        return agencyProfileRepository.save(existingProfile);
    }

    @Transactional
    public boolean deleteAgencyProfile(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid agency profile ID");
        }
        AgencyProfile profile = getAgencyProfileById(id);
        agencyProfileRepository.delete(profile);
        return true;
    }
}