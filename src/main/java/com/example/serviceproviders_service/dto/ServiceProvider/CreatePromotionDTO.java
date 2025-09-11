package com.example.serviceproviders_service.dto.ServiceProvider;

import com.example.serviceproviders_service.entity.serviceProvider.Promotion;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreatePromotionDTO {
    private Long serviceProviderId;
    private String title;
    private String description;
    private String image;
    private Integer discount;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Boolean isActive;
    private Promotion.PromotionStatus status;

    public CreatePromotionDTO() {}
}