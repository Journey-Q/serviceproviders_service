package com.example.serviceproviders_service.dto.ServiceProvider.TourGuide;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TourPackageReviewStatsDTO {
    private long totalReviews;
    private BigDecimal averageRating;
    private long fiveStarCount;
    private long fourStarCount;
    private long threeStarCount;
    private long twoStarCount;
    private long oneStarCount;
}