package com.example.serviceproviders_service.services.ServiceProvider;

import com.example.serviceproviders_service.dto.ServiceProvider.CreateReviewDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.ReviewResponseDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.ReviewStatsDTO;
import com.example.serviceproviders_service.entity.serviceProvider.Review;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.repository.ServiceProvider.ReviewRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.ServiceProviderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ServiceProviderRepo serviceProviderRepo;

    // Service provider validation method (no service type restriction)
    private void validateServiceProviderExists(Long serviceProviderId) {
        serviceProviderRepo.findById(serviceProviderId)
                .orElseThrow(() -> new BadRequestException("Service provider not found with id: " + serviceProviderId));
    }

    @Transactional
    public ReviewResponseDTO createReview(CreateReviewDTO dto) {
        validateCreateReviewDTO(dto);

        // Validate service provider exists (any type allowed)
        validateServiceProviderExists(dto.getServiceProviderId());

        if (reviewRepository.existsByBookingId(dto.getBookingId())) {
            throw new BadRequestException("Review already exists for booking ID: " + dto.getBookingId());
        }

        Review review = new Review();
        review.setServiceProviderId(dto.getServiceProviderId());
        review.setUserId(dto.getUserId());
        review.setBookingId(dto.getBookingId());
        review.setCustomerName(dto.getCustomerName());
        review.setRating(dto.getRating());
        review.setReviewText(dto.getReviewText());
        review.setCustomerEmail(dto.getCustomerEmail());
        review.setCustomerPhone(dto.getCustomerPhone());
        review.setStatus(dto.getStatus() != null ? dto.getStatus() : Review.ReviewStatus.ACTIVE);
        review.setIsVerified(dto.getIsVerified() != null ? dto.getIsVerified() : false);

        Review savedReview = reviewRepository.save(review);
        return ReviewResponseDTO.fromEntity(savedReview);
    }

    @Transactional(readOnly = true)
    public ReviewResponseDTO getReviewById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid review ID");
        }

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Review not found with id: " + id));

        return ReviewResponseDTO.fromEntity(review);
    }

    @Transactional(readOnly = true)
    public ReviewResponseDTO getReviewByBookingId(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new BadRequestException("Booking ID is required");
        }

        Review review = reviewRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BadRequestException("Review not found for booking ID: " + bookingId));

        return ReviewResponseDTO.fromEntity(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(ReviewResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // User-based methods
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        List<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return reviews.stream()
                .map(ReviewResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getActiveReviewsByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        List<Review> reviews = reviewRepository.findActiveReviewsByUserId(userId);
        return reviews.stream()
                .map(ReviewResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Service provider-based methods
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByServiceProviderId(Long serviceProviderId) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }

        // Validate service provider exists
        validateServiceProviderExists(serviceProviderId);

        List<Review> reviews = reviewRepository.findByServiceProviderIdOrderByCreatedAtDesc(serviceProviderId);
        return reviews.stream()
                .map(ReviewResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getActiveReviewsByServiceProviderId(Long serviceProviderId) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }

        // Validate service provider exists
        validateServiceProviderExists(serviceProviderId);

        List<Review> reviews = reviewRepository.findActiveReviewsByServiceProviderId(serviceProviderId);
        return reviews.stream()
                .map(ReviewResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByServiceProviderIdAndStatus(Long serviceProviderId, Review.ReviewStatus status) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }
        if (status == null) {
            throw new BadRequestException("Review status is required");
        }

        // Validate service provider exists
        validateServiceProviderExists(serviceProviderId);

        List<Review> reviews = reviewRepository.findByServiceProviderIdAndStatus(serviceProviderId, status);
        return reviews.stream()
                .map(ReviewResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByServiceProviderIdAndRating(Long serviceProviderId, Integer rating) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }
        if (rating == null || rating < 1 || rating > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        // Validate service provider exists
        validateServiceProviderExists(serviceProviderId);

        List<Review> reviews = reviewRepository.findByServiceProviderIdAndRating(serviceProviderId, rating);
        return reviews.stream()
                .map(ReviewResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewStatsDTO getReviewStatsByServiceProviderId(Long serviceProviderId) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }

        // Validate service provider exists
        validateServiceProviderExists(serviceProviderId);

        long totalReviews = reviewRepository.countActiveReviewsByServiceProviderId(serviceProviderId);
        BigDecimal averageRating = reviewRepository.getAverageRatingByServiceProviderId(serviceProviderId);

        if (averageRating == null) {
            averageRating = BigDecimal.ZERO;
        }

        long fiveStarCount = reviewRepository.countByServiceProviderIdAndRatingAndStatusActive(serviceProviderId, 5);
        long fourStarCount = reviewRepository.countByServiceProviderIdAndRatingAndStatusActive(serviceProviderId, 4);
        long threeStarCount = reviewRepository.countByServiceProviderIdAndRatingAndStatusActive(serviceProviderId, 3);
        long twoStarCount = reviewRepository.countByServiceProviderIdAndRatingAndStatusActive(serviceProviderId, 2);
        long oneStarCount = reviewRepository.countByServiceProviderIdAndRatingAndStatusActive(serviceProviderId, 1);

        return new ReviewStatsDTO(totalReviews, averageRating, fiveStarCount, fourStarCount,
                threeStarCount, twoStarCount, oneStarCount);
    }

    // Combined user and service provider method
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByUserIdAndServiceProviderId(Long userId, Long serviceProviderId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }

        // Validate service provider exists
        validateServiceProviderExists(serviceProviderId);

        List<Review> reviews = reviewRepository.findByUserIdAndServiceProviderId(userId, serviceProviderId);
        return reviews.stream()
                .map(ReviewResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponseDTO updateReview(Long id, CreateReviewDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid review ID");
        }

        validateCreateReviewDTO(dto);

        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Review not found with id: " + id));

        // Validate service provider exists
        validateServiceProviderExists(dto.getServiceProviderId());

        // Additional security check: Ensure the existing review belongs to a valid service provider
        validateServiceProviderExists(existingReview.getServiceProviderId());

        // Check if booking ID is being changed and if it already exists
        if (!existingReview.getBookingId().equals(dto.getBookingId()) &&
                reviewRepository.existsByBookingId(dto.getBookingId())) {
            throw new BadRequestException("Review already exists for booking ID: " + dto.getBookingId());
        }

        existingReview.setServiceProviderId(dto.getServiceProviderId());
        existingReview.setUserId(dto.getUserId());
        existingReview.setBookingId(dto.getBookingId());
        existingReview.setCustomerName(dto.getCustomerName());
        existingReview.setRating(dto.getRating());
        existingReview.setReviewText(dto.getReviewText());
        existingReview.setCustomerEmail(dto.getCustomerEmail());
        existingReview.setCustomerPhone(dto.getCustomerPhone());
        existingReview.setStatus(dto.getStatus() != null ? dto.getStatus() : existingReview.getStatus());
        existingReview.setIsVerified(dto.getIsVerified() != null ? dto.getIsVerified() : existingReview.getIsVerified());

        Review updatedReview = reviewRepository.save(existingReview);
        return ReviewResponseDTO.fromEntity(updatedReview);
    }

    @Transactional
    public ReviewResponseDTO updateReviewStatus(Long id, Review.ReviewStatus status) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid review ID");
        }
        if (status == null) {
            throw new BadRequestException("Review status is required");
        }

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Review not found with id: " + id));

        // Validate that the review belongs to a valid service provider
        validateServiceProviderExists(review.getServiceProviderId());

        review.setStatus(status);
        Review updatedReview = reviewRepository.save(review);
        return ReviewResponseDTO.fromEntity(updatedReview);
    }

    @Transactional
    public ReviewResponseDTO updateReviewVerification(Long id, Boolean isVerified) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid review ID");
        }
        if (isVerified == null) {
            throw new BadRequestException("Verification status is required");
        }

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Review not found with id: " + id));

        // Validate that the review belongs to a valid service provider
        validateServiceProviderExists(review.getServiceProviderId());

        review.setIsVerified(isVerified);
        Review updatedReview = reviewRepository.save(review);
        return ReviewResponseDTO.fromEntity(updatedReview);
    }

    @Transactional
    public boolean deleteReview(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid review ID");
        }

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Review not found with id: " + id));

        // Validate that the review belongs to a valid service provider
        validateServiceProviderExists(review.getServiceProviderId());

        reviewRepository.delete(review);
        return true;
    }

    private void validateCreateReviewDTO(CreateReviewDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Review data cannot be null");
        }
        if (dto.getServiceProviderId() == null || dto.getServiceProviderId() <= 0) {
            throw new BadRequestException("Service Provider ID is required and must be positive");
        }
        if (dto.getUserId() == null || dto.getUserId() <= 0) {
            throw new BadRequestException("User ID is required and must be positive");
        }
        if (dto.getBookingId() == null || dto.getBookingId().trim().isEmpty()) {
            throw new BadRequestException("Booking ID is required");
        }
        if (dto.getBookingId().length() > 50) {
            throw new BadRequestException("Booking ID cannot exceed 50 characters");
        }
        if (dto.getCustomerName() == null || dto.getCustomerName().trim().isEmpty()) {
            throw new BadRequestException("Customer name is required");
        }
        if (dto.getCustomerName().length() > 100) {
            throw new BadRequestException("Customer name cannot exceed 100 characters");
        }
        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }
        if (dto.getReviewText() == null || dto.getReviewText().trim().isEmpty()) {
            throw new BadRequestException("Review text is required");
        }
        if (dto.getReviewText().length() > 5000) {
            throw new BadRequestException("Review text cannot exceed 5000 characters");
        }
        if (dto.getCustomerEmail() != null && !dto.getCustomerEmail().trim().isEmpty()) {
            if (!dto.getCustomerEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                throw new BadRequestException("Invalid email format");
            }
            if (dto.getCustomerEmail().length() > 100) {
                throw new BadRequestException("Email cannot exceed 100 characters");
            }
        }
        if (dto.getCustomerPhone() != null && !dto.getCustomerPhone().trim().isEmpty()) {
            if (dto.getCustomerPhone().length() > 15) {
                throw new BadRequestException("Phone number cannot exceed 15 characters");
            }
        }
    }
}
