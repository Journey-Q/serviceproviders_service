package com.example.serviceproviders_service.dto.ServiceProvider;

import com.example.serviceproviders_service.entity.serviceProvider.Review;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponseDTO {
    private Long id;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReviewResponseDTO() {}

    public static ReviewResponseDTO fromEntity(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setServiceProviderId(review.getServiceProviderId());
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