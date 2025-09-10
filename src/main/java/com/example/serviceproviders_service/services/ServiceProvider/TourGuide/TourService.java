package com.example.serviceproviders_service.services.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.CreateTourDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.TourResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProvider;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProviderType;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.Tour;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourItinerary;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.repository.ServiceProvider.ServiceProviderRepo;
import com.example.serviceproviders_service.repository.ServiceProvider.TourGuide.TourItineraryRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TourGuide.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourService {

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private TourItineraryRepository tourItineraryRepository;

    @Autowired
    private ServiceProviderRepo serviceProviderRepo;

    // Service type validation method
    private void validateTourGuideAccess(Long serviceProviderId) {
        ServiceProvider serviceProvider = serviceProviderRepo.findById(serviceProviderId)
                .orElseThrow(() -> new BadRequestException("Service provider not found with id: " + serviceProviderId));

        if (!ServiceProviderType.TOUR_GUIDE.equals(serviceProvider.getServiceType())) {
            throw new BadRequestException("Only tour guide service providers can manage tours. Current service type: " + serviceProvider.getServiceType());
        }
    }

    @Transactional
    public TourResponseDTO createTour(CreateTourDTO dto) {
        validateCreateTourDTO(dto);

        // Validate service provider exists and is a tour guide
        validateTourGuideAccess(dto.getServiceProviderId());

        if (tourRepository.existsByServiceProviderIdAndName(dto.getServiceProviderId(), dto.getName())) {
            throw new BadRequestException("Tour with this name already exists for this service provider");
        }

        Tour tour = new Tour();
        tour.setServiceProviderId(dto.getServiceProviderId());
        tour.setName(dto.getName());
        tour.setImage(dto.getImage());
        tour.setOriginalPrice(dto.getOriginalPrice());
        tour.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : 0);
        tour.setDuration(dto.getDuration());
        tour.setPlaces(dto.getPlaces());
        tour.setHighlights(dto.getHighlights());
        tour.setStatus(dto.getStatus() != null ? dto.getStatus() : Tour.TourStatus.AVAILABLE);
        tour.setRating(dto.getRating() != null ? dto.getRating() : BigDecimal.valueOf(4.0));
        tour.setMaxPeople(dto.getMaxPeople());
        tour.setMinPeople(dto.getMinPeople());
        tour.setAboutTour(dto.getAboutTour());
        tour.setIncluded(dto.getIncluded());
        tour.setImportantNotes(dto.getImportantNotes());

        Tour savedTour = tourRepository.save(tour);

        // Save itinerary items
        List<TourItinerary> itineraryItems = saveItineraryItems(savedTour.getId(), dto.getItinerary());

        return TourResponseDTO.fromEntity(savedTour, itineraryItems);
    }

    @Transactional(readOnly = true)
    public TourResponseDTO getTourById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid tour ID");
        }
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tour not found with id: " + id));

        List<TourItinerary> itineraryItems = tourItineraryRepository.findByTourIdOrderByOrderIndex(id);
        return TourResponseDTO.fromEntity(tour, itineraryItems);
    }

    @Transactional(readOnly = true)
    public List<TourResponseDTO> getAllTours() {
        List<Tour> tours = tourRepository.findAll();
        return tours.stream()
                .map(tour -> {
                    List<TourItinerary> itineraryItems = tourItineraryRepository.findByTourIdOrderByOrderIndex(tour.getId());
                    return TourResponseDTO.fromEntity(tour, itineraryItems);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TourResponseDTO> getToursByServiceProviderId(Long serviceProviderId) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }

        // Validate service provider exists and is a tour guide
        validateTourGuideAccess(serviceProviderId);

        List<Tour> tours = tourRepository.findByServiceProviderId(serviceProviderId);
        return tours.stream()
                .map(tour -> {
                    List<TourItinerary> itineraryItems = tourItineraryRepository.findByTourIdOrderByOrderIndex(tour.getId());
                    return TourResponseDTO.fromEntity(tour, itineraryItems);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TourResponseDTO> getToursByServiceProviderIdAndStatus(Long serviceProviderId, Tour.TourStatus status) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }
        if (status == null) {
            throw new BadRequestException("Tour status is required");
        }

        // Validate service provider exists and is a tour guide
        validateTourGuideAccess(serviceProviderId);

        List<Tour> tours = tourRepository.findByServiceProviderIdAndStatus(serviceProviderId, status);
        return tours.stream()
                .map(tour -> {
                    List<TourItinerary> itineraryItems = tourItineraryRepository.findByTourIdOrderByOrderIndex(tour.getId());
                    return TourResponseDTO.fromEntity(tour, itineraryItems);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public TourResponseDTO updateTour(Long id, CreateTourDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid tour ID");
        }

        validateCreateTourDTO(dto);

        Tour existingTour = tourRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tour not found with id: " + id));

        // Validate service provider exists and is a tour guide
        validateTourGuideAccess(dto.getServiceProviderId());

        // Additional security check: Ensure the existing tour belongs to a tour guide
        validateTourGuideAccess(existingTour.getServiceProviderId());

        if (tourRepository.existsByServiceProviderIdAndNameAndIdNot(dto.getServiceProviderId(), dto.getName(), id)) {
            throw new BadRequestException("Tour with this name already exists for this service provider");
        }

        existingTour.setServiceProviderId(dto.getServiceProviderId());
        existingTour.setName(dto.getName());
        existingTour.setImage(dto.getImage());
        existingTour.setOriginalPrice(dto.getOriginalPrice());
        existingTour.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : 0);
        existingTour.setDuration(dto.getDuration());
        existingTour.setPlaces(dto.getPlaces());
        existingTour.setHighlights(dto.getHighlights());
        existingTour.setStatus(dto.getStatus() != null ? dto.getStatus() : existingTour.getStatus());
        existingTour.setRating(dto.getRating() != null ? dto.getRating() : existingTour.getRating());
        existingTour.setMaxPeople(dto.getMaxPeople());
        existingTour.setMinPeople(dto.getMinPeople());
        existingTour.setAboutTour(dto.getAboutTour());
        existingTour.setIncluded(dto.getIncluded());
        existingTour.setImportantNotes(dto.getImportantNotes());

        Tour updatedTour = tourRepository.save(existingTour);

        // Update itinerary items
        tourItineraryRepository.deleteByTourId(id);
        List<TourItinerary> itineraryItems = saveItineraryItems(id, dto.getItinerary());

        return TourResponseDTO.fromEntity(updatedTour, itineraryItems);
    }

    @Transactional
    public boolean deleteTour(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid tour ID");
        }
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tour not found with id: " + id));

        // Validate that the tour belongs to a tour guide service provider
        validateTourGuideAccess(tour.getServiceProviderId());

        // Delete itinerary items first
        tourItineraryRepository.deleteByTourId(id);
        tourRepository.delete(tour);
        return true;
    }

    @Transactional
    public TourResponseDTO updateTourStatus(Long id, Tour.TourStatus status) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid tour ID");
        }
        if (status == null) {
            throw new BadRequestException("Tour status is required");
        }

        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tour not found with id: " + id));

        // Validate that the tour belongs to a tour guide service provider
        validateTourGuideAccess(tour.getServiceProviderId());

        tour.setStatus(status);
        Tour updatedTour = tourRepository.save(tour);

        List<TourItinerary> itineraryItems = tourItineraryRepository.findByTourIdOrderByOrderIndex(id);
        return TourResponseDTO.fromEntity(updatedTour, itineraryItems);
    }

    private List<TourItinerary> saveItineraryItems(Long tourId, List<CreateTourDTO.ItineraryItemDTO> itineraryDTOs) {
        if (itineraryDTOs == null || itineraryDTOs.isEmpty()) {
            return List.of();
        }

        List<TourItinerary> itineraryItems = itineraryDTOs.stream()
                .map(dto -> new TourItinerary(tourId, dto.getTime(), dto.getActivity(), itineraryDTOs.indexOf(dto)))
                .collect(Collectors.toList());

        return tourItineraryRepository.saveAll(itineraryItems);
    }

    private void validateCreateTourDTO(CreateTourDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Tour data cannot be null");
        }
        if (dto.getServiceProviderId() == null || dto.getServiceProviderId() <= 0) {
            throw new BadRequestException("Service Provider ID is required and must be positive");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BadRequestException("Tour name is required");
        }
        if (dto.getName().length() > 200) {
            throw new BadRequestException("Tour name cannot exceed 200 characters");
        }
        if (dto.getOriginalPrice() == null || dto.getOriginalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Original price must be positive");
        }
        if (dto.getDiscount() != null && (dto.getDiscount() < 0 || dto.getDiscount() > 100)) {
            throw new BadRequestException("Discount must be between 0 and 100 percent");
        }
        if (dto.getDuration() == null || dto.getDuration().trim().isEmpty()) {
            throw new BadRequestException("Tour duration is required");
        }
        if (dto.getMaxPeople() == null || dto.getMaxPeople() <= 0) {
            throw new BadRequestException("Maximum people must be positive");
        }
        if (dto.getMinPeople() == null || dto.getMinPeople() <= 0) {
            throw new BadRequestException("Minimum people must be positive");
        }
        if (dto.getMinPeople() > dto.getMaxPeople()) {
            throw new BadRequestException("Minimum people cannot be greater than maximum people");
        }
        if (dto.getRating() != null && (dto.getRating().compareTo(BigDecimal.valueOf(1)) < 0 || dto.getRating().compareTo(BigDecimal.valueOf(5)) > 0)) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }
    }
}