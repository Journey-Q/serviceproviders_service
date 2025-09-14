package com.example.serviceproviders_service.dto.ServiceProvider;

import com.example.serviceproviders_service.entity.serviceProvider.Review;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReviewDTO {
    private Long serviceProviderId;
    private Long userId;
    private String bookingId;
    private String customerName;
    private Integer rating;
    private String reviewText;
    private String customerEmail;
    private String customerPhone;
    private Review.ReviewStatus status;
    private Boolean isVerified;

    public CreateReviewDTO() {}
}
