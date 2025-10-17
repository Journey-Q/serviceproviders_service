package com.example.serviceproviders_service.dto.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.PastTourImage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PastTourImageResponseDTO {
    private Long id;
    private Long tourId;
    private String imageUrl;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PastTourImageResponseDTO() {}

    public PastTourImageResponseDTO(Long id, Long tourId, String imageUrl, Integer orderIndex, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tourId = tourId;
        this.imageUrl = imageUrl;
        this.orderIndex = orderIndex;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PastTourImageResponseDTO fromEntity(PastTourImage image) {
        return new PastTourImageResponseDTO(
                image.getId(),
                image.getTourId(),
                image.getImageUrl(),
                image.getOrderIndex(),
                image.getCreatedAt(),
                image.getUpdatedAt()
        );
    }
}