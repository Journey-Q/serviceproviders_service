package com.example.serviceproviders_service.dto.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.Tour;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateTourDTO {
    private Long serviceProviderId;
    private String name;
    private String image;
    private BigDecimal originalPrice;
    private Integer discount;
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

    public CreateTourDTO() {}
}
