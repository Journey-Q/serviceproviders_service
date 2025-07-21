package com.example.serviceproviders_service.services.ServiceProvider;

import com.example.serviceproviders_service.dto.ServiceProvider.CreateHotelProfileDTO;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.ContactInfo;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.Coordinates;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.HotelProfile;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.repository.ServiceProvider.HotelProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class HotelProfileService {

    @Autowired
    private HotelProfileRepository hotelProfileRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    @Transactional
    public HotelProfile createHotelProfile(CreateHotelProfileDTO dto) {
        // Basic validation
        if (dto == null) {
            throw new BadRequestException("Hotel profile data cannot be null");
        }
        if (dto.getHotelName() == null || dto.getHotelName().trim().isEmpty()) {
            throw new BadRequestException("Hotel name is required");
        }
        if (dto.getLocation() == null || dto.getLocation().trim().isEmpty()) {
            throw new BadRequestException("Location is required");
        }

        if (dto.getServiceProviderId() == null || dto.getServiceProviderId() <= 0) {
            throw new BadRequestException("Service provider ID is required");
        }

        if (hotelProfileRepository.existsByServiceProviderId(dto.getServiceProviderId())){
            throw new BadRequestException("Service provider already has a hotel profile");
        }

        // Validate coordinates if provided
        if (dto.getCoordinates() != null) {
            if (dto.getCoordinates().getLat() == null || dto.getCoordinates().getLng() == null) {
                throw new BadRequestException("Both latitude and longitude are required");
            }
            if (dto.getCoordinates().getLat() < -90 || dto.getCoordinates().getLat() > 90) {
                throw new BadRequestException("Latitude must be between -90 and 90");
            }
            if (dto.getCoordinates().getLng() < -180 || dto.getCoordinates().getLng() > 180) {
                throw new BadRequestException("Longitude must be between -180 and 180");
            }
        }

        // Validate email if provided
        if (dto.getContactInfo() != null && dto.getContactInfo().getEmail() != null) {
            if (!EMAIL_PATTERN.matcher(dto.getContactInfo().getEmail()).matches()) {
                throw new BadRequestException("Invalid email format");
            }
        }

        // Check for duplicate hotel
        boolean hotelExists = hotelProfileRepository.existsByHotelNameAndLocation(dto.getHotelName(), dto.getLocation());
        if (hotelExists) {
            throw new BadRequestException("Hotel with this name already exists at this location");
        }

        HotelProfile hotelProfile = new HotelProfile();
        hotelProfile.setServiceProviderId(dto.getServiceProviderId());
        hotelProfile.setHotelName(dto.getHotelName());
        hotelProfile.setLocation(dto.getLocation());
        hotelProfile.setDescription(dto.getDescription());
        hotelProfile.setHotelPhoto(dto.getHotelPhoto());

        if (dto.getCoordinates() != null) {
            hotelProfile.setCoordinates(new Coordinates(
                    dto.getCoordinates().getLat(),
                    dto.getCoordinates().getLng()
            ));
        }

        if (dto.getContactInfo() != null) {
            hotelProfile.setContactInfo(new ContactInfo(
                    dto.getContactInfo().getPhone(),
                    dto.getContactInfo().getEmail()
            ));
        }

        if (dto.getAmenities() != null) {
            hotelProfile.setAmenities(dto.getAmenities());
        } else {
            hotelProfile.setAmenities(List.of());
        }

        return hotelProfileRepository.save(hotelProfile);
    }

    @Transactional(readOnly = true)
    public HotelProfile getHotelProfileById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid hotel profile ID");
        }
        return hotelProfileRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Hotel profile not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<HotelProfile> getAllHotelProfiles() {
        return hotelProfileRepository.findAll();
    }

    @Transactional
    public HotelProfile updateHotelProfile(Long id, CreateHotelProfileDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid hotel profile ID");
        }
        if (dto == null) {
            throw new BadRequestException("Hotel profile data cannot be null");
        }
        if (dto.getHotelName() == null || dto.getHotelName().trim().isEmpty()) {
            throw new BadRequestException("Hotel name is required");
        }
        if (dto.getLocation() == null || dto.getLocation().trim().isEmpty()) {
            throw new BadRequestException("Location is required");
        }

        // Validate coordinates if provided
        if (dto.getCoordinates() != null) {
            if (dto.getCoordinates().getLat() == null || dto.getCoordinates().getLng() == null) {
                throw new BadRequestException("Both latitude and longitude are required");
            }
            if (dto.getCoordinates().getLat() < -90 || dto.getCoordinates().getLat() > 90) {
                throw new BadRequestException("Latitude must be between -90 and 90");
            }
            if (dto.getCoordinates().getLng() < -180 || dto.getCoordinates().getLng() > 180) {
                throw new BadRequestException("Longitude must be between -180 and 180");
            }
        }

        // Validate email if provided
        if (dto.getContactInfo() != null && dto.getContactInfo().getEmail() != null) {
            if (!EMAIL_PATTERN.matcher(dto.getContactInfo().getEmail()).matches()) {
                throw new BadRequestException("Invalid email format");
            }
        }

        HotelProfile existingProfile = getHotelProfileById(id);

        // Check for duplicate hotel (excluding current one)
        boolean hotelExists = hotelProfileRepository.existsByHotelNameAndLocationAndServiceProviderIdNot(dto.getHotelName(), dto.getLocation(), id);
        if (hotelExists) {
            throw new BadRequestException("Hotel with this name already exists at this location");
        }

        existingProfile.setHotelName(dto.getHotelName());
        existingProfile.setLocation(dto.getLocation());
        existingProfile.setDescription(dto.getDescription());
        existingProfile.setHotelPhoto(dto.getHotelPhoto());

        if (dto.getCoordinates() != null) {
            if (existingProfile.getCoordinates() == null) {
                existingProfile.setCoordinates(new Coordinates());
            }
            existingProfile.getCoordinates().setLat(dto.getCoordinates().getLat());
            existingProfile.getCoordinates().setLng(dto.getCoordinates().getLng());
        }

        if (dto.getContactInfo() != null) {
            if (existingProfile.getContactInfo() == null) {
                existingProfile.setContactInfo(new ContactInfo());
            }
            existingProfile.getContactInfo().setPhone(dto.getContactInfo().getPhone());
            existingProfile.getContactInfo().setEmail(dto.getContactInfo().getEmail());
        }

        if (dto.getAmenities() != null) {
            existingProfile.setAmenities(dto.getAmenities());
        }

        return hotelProfileRepository.save(existingProfile);
    }

    @Transactional
    public boolean deleteHotelProfile(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid hotel profile ID");
        }
        HotelProfile profile = getHotelProfileById(id);
        hotelProfileRepository.delete(profile);
        return true;
    }
}