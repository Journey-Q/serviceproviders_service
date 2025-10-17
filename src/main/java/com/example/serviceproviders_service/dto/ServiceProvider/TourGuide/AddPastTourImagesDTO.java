package com.example.serviceproviders_service.dto.ServiceProvider.TourGuide;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddPastTourImagesDTO {
    private Long tourId;
    private List<String> imageUrls;

    public AddPastTourImagesDTO() {}

    public AddPastTourImagesDTO(Long tourId, List<String> imageUrls) {
        this.tourId = tourId;
        this.imageUrls = imageUrls;
    }
}