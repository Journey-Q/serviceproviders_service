package com.example.serviceproviders_service.dto.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourPackageReview;
import lombok.Data;

@Data
public class CreateTourPackageReviewDTO {
    private Long tourId;
    private Long userId;
    private String bookingId;
    private String customerName;
    private Integer rating;
    private String reviewText;
    private String customerEmail;
    private String customerPhone;
    private TourPackageReview.ReviewStatus status;
    private Boolean isVerified;
}
