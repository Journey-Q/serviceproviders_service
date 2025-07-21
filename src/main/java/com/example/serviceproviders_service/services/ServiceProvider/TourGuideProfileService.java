package com.example.serviceproviders_service.services.ServiceProvider;

import com.example.serviceproviders_service.dto.ServiceProvider.TourGuideProfileDTO;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.ContactInfo;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourGuideProfile;
import com.example.serviceproviders_service.exception.BadRequestException;

import com.example.serviceproviders_service.repository.ServiceProvider.TourGuideProfileRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TourGuideProfileService {

    private final TourGuideProfileRepository repository;
    private final Validator validator; // Bean validation

    @Transactional
    public TourGuideProfile createProfile(TourGuideProfileDTO dto) {
        validateDto(dto);

        // Check if profile already exists for this service provider
        if (repository.existsByServiceProviderId(dto.getServiceProviderId())) {
            throw new BadRequestException("Service provider can only have one tour guide profile");
        }

        // Check if company with same name exists at same address
        if (repository.existsByCompanyNameAndContactInfoAddress(
                dto.getCompanyName(),
                dto.getContactInfo().getAddress())) {
            throw new BadRequestException("A tour guide company with this name already exists at this location");
        }

        TourGuideProfile profile = new TourGuideProfile();
        mapDtoToEntity(dto, profile);
        return repository.save(profile);
    }

    @Transactional(readOnly = true)
    public TourGuideProfile getProfileById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tour guide profile not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public TourGuideProfile getProfileByServiceProviderId(Long serviceProviderId) {
        return repository.findByServiceProviderId(serviceProviderId)
                .orElseThrow(() -> new BadRequestException("Tour guide profile not found for service provider: " + serviceProviderId));
    }

    @Transactional
    public TourGuideProfile updateProfile(Long id, TourGuideProfileDTO dto) {
        validateDto(dto);

        TourGuideProfile existingProfile = getProfileById(id);

        // Prevent changing service provider ID
        if (!existingProfile.getServiceProviderId().equals(dto.getServiceProviderId())) {
            throw new BadRequestException("Cannot change service provider ID for an existing profile");
        }

        mapDtoToEntity(dto, existingProfile);
        return repository.save(existingProfile);
    }

    @Transactional
    public void deleteProfile(Long id) {
        if (!repository.existsById(id)) {
            throw new BadRequestException("Tour guide profile not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public boolean deleteTourGuideProfile(Long id){
        if(id == null || id <= 0){
            throw new BadRequestException("Invalid tour guide profile ID");
        }
        TourGuideProfile profile = getProfileById(id);
        repository.delete(profile);
        return true;
    }

    private void validateDto(TourGuideProfileDTO dto) {
        Set<ConstraintViolation<TourGuideProfileDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<TourGuideProfileDTO> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
            }
            throw new BadRequestException("Validation errors: " + sb.toString());
        }

        // Additional business validations
        if (dto.getEstablishedYear() != null && dto.getEstablishedYear() < 1900) {
            throw new BadRequestException("Established year must be after 1900");
        }

        if (dto.getNumberOfGuides() != null && dto.getNumberOfGuides() < 1) {
            throw new BadRequestException("Number of guides must be at least 1");
        }
    }

    private void mapDtoToEntity(TourGuideProfileDTO dto, TourGuideProfile entity) {
        entity.setServiceProviderId(dto.getServiceProviderId());
        entity.setCompanyName(dto.getCompanyName());
        entity.setProfilePhotoUrl(dto.getProfilePhotoUrl());
        entity.setDescription(dto.getDescription());
        entity.setEstablishedYear(dto.getEstablishedYear());
        entity.setNumberOfGuides(dto.getNumberOfGuides());

        if (dto.getContactInfo() != null) {
            ContactInfo contactInfo = entity.getContactInfo() != null ?
                    entity.getContactInfo() : new ContactInfo();
            contactInfo.setEmail(dto.getContactInfo().getEmail());
            contactInfo.setPhone(dto.getContactInfo().getPhone());
            contactInfo.setAddress(dto.getContactInfo().getAddress());
            entity.setContactInfo(contactInfo);
        }
    }
}