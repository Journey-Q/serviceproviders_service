package com.example.serviceproviders_service.dto.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.Tour;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourItinerary;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TourResponseDTO {
    private Long id;
    private Long serviceProviderId;
    private String name;
    private String image;
    private BigDecimal originalPrice;
    private Integer discount;
    private BigDecimal finalPrice;
    private BigDecimal pricePerPerson;
    private String duration;
    private List<String> places;
    private List<String> highlights;
    private Tour.TourStatus status;
    private BigDecimal rating;
    private Integer maxPeople;
    private Integer minPeople;
    private String aboutTour;
    private List<String> included;
    private List<String> importantNotes;
    private List<ItineraryItemDTO> itinerary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    public static class ItineraryItemDTO {
        private String time;
        private String activity;

        public ItineraryItemDTO() {}

        public ItineraryItemDTO(String time, String activity) {
            this.time = time;
            this.activity = activity;
        }
    }

    public TourResponseDTO() {}

    public static TourResponseDTO fromEntity(Tour tour, List<TourItinerary> itineraryItems) {
        TourResponseDTO dto = new TourResponseDTO();
        dto.setId(tour.getId());
        dto.setServiceProviderId(tour.getServiceProviderId());
        dto.setName(tour.getName());
        dto.setImage(tour.getImage());
        dto.setOriginalPrice(tour.getOriginalPrice());
        dto.setDiscount(tour.getDiscount());
        dto.setFinalPrice(tour.getFinalPrice());
        dto.setPricePerPerson(tour.getPricePerPerson());
        dto.setDuration(tour.getDuration());
        dto.setPlaces(tour.getPlaces());
        dto.setHighlights(tour.getHighlights());
        dto.setStatus(tour.getStatus());
        dto.setRating(tour.getRating());
        dto.setMaxPeople(tour.getMaxPeople());
        dto.setMinPeople(tour.getMinPeople());
        dto.setAboutTour(tour.getAboutTour());
        dto.setIncluded(tour.getIncluded());
        dto.setImportantNotes(tour.getImportantNotes());
        dto.setCreatedAt(tour.getCreatedAt());
        dto.setUpdatedAt(tour.getUpdatedAt());

        // Convert itinerary
        if (itineraryItems != null) {
            List<ItineraryItemDTO> itineraryDTOs = itineraryItems.stream()
                    .map(item -> new ItineraryItemDTO(item.getTime(), item.getActivity()))
                    .toList();
            dto.setItinerary(itineraryDTOs);
        }

        return dto;
    }
}