package com.example.serviceproviders_service.dto.ServiceProvider.Hotel;

import com.example.serviceproviders_service.entity.serviceProvider.Hotel.Promotion;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class PromotionResponseDTO {
    private Long id;
    private Long serviceProviderId;
    private String title;
    private String description;
    private String image;
    private Integer discount;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Boolean isActive;
    private Promotion.PromotionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PromotionResponseDTO() {}

    public static PromotionResponseDTO fromEntity(Promotion promotion) {
        PromotionResponseDTO dto = new PromotionResponseDTO();
        dto.setId(promotion.getId());
        dto.setServiceProviderId(promotion.getServiceProviderId());
        dto.setTitle(promotion.getTitle());
        dto.setDescription(promotion.getDescription());
        dto.setImage(promotion.getImage());
        dto.setDiscount(promotion.getDiscount());
        dto.setValidFrom(promotion.getValidFrom());
        dto.setValidTo(promotion.getValidTo());
        dto.setIsActive(promotion.getIsActive());
        dto.setStatus(promotion.getStatus());
        dto.setCreatedAt(promotion.getCreatedAt());
        dto.setUpdatedAt(promotion.getUpdatedAt());
        return dto;
    }
}