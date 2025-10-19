package com.example.serviceproviders_service.dto.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourPackageReview;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TourPackageReviewResponseDTO {
    private Long id;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TourPackageReviewResponseDTO fromEntity(TourPackageReview review) {
        TourPackageReviewResponseDTO dto = new TourPackageReviewResponseDTO();
        dto.setId(review.getId());
        dto.setTourId(review.getTourId());
        dto.setUserId(review.getUserId());
        dto.setBookingId(review.getBookingId());
        dto.setCustomerName(review.getCustomerName());
        dto.setRating(review.getRating());
        dto.setReviewText(review.getReviewText());
        dto.setCustomerEmail(review.getCustomerEmail());
        dto.setCustomerPhone(review.getCustomerPhone());
        dto.setStatus(review.getStatus());
        dto.setIsVerified(review.getIsVerified());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }
}
