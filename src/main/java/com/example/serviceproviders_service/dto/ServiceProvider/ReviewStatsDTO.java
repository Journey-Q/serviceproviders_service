package com.example.serviceproviders_service.dto.ServiceProvider;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReviewStatsDTO {
    private Long totalReviews;
    private BigDecimal averageRating;
    private Long fiveStarCount;
    private Long fourStarCount;
    private Long threeStarCount;
    private Long twoStarCount;
    private Long oneStarCount;

    public ReviewStatsDTO() {}

    public ReviewStatsDTO(Long totalReviews, BigDecimal averageRating,
                          Long fiveStarCount, Long fourStarCount, Long threeStarCount,
                          Long twoStarCount, Long oneStarCount) {
        this.totalReviews = totalReviews;
        this.averageRating = averageRating;
        this.fiveStarCount = fiveStarCount;
        this.fourStarCount = fourStarCount;
        this.threeStarCount = threeStarCount;
        this.twoStarCount = twoStarCount;
        this.oneStarCount = oneStarCount;
    }
}