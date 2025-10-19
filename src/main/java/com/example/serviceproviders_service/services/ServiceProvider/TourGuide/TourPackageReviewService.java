package com.example.serviceproviders_service.services.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.CreateTourPackageReviewDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.TourPackageReviewResponseDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.TourPackageReviewStatsDTO;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourPackageReview;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.repository.ServiceProvider.TourGuide.TourPackageReviewRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.TourGuide.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourPackageReviewService {

    @Autowired
    private TourPackageReviewRepository reviewRepository;

    @Autowired
    private TourRepository tourRepository;

    // Validate tour exists
    private void validateTourExists(Long tourId) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> new BadRequestException("Tour not found with id: " + tourId));
    }

    @Transactional
    public TourPackageReviewResponseDTO createReview(CreateTourPackageReviewDTO dto) {
        validateCreateReviewDTO(dto);

        // Validate tour exists
        validateTourExists(dto.getTourId());

        if (reviewRepository.existsByBookingId(dto.getBookingId())) {
            throw new BadRequestException("Review already exists for booking ID: " + dto.getBookingId());
        }

        TourPackageReview review = new TourPackageReview();
        review.setTourId(dto.getTourId());
        review.setUserId(dto.getUserId());
        review.setBookingId(dto.getBookingId());
        review.setCustomerName(dto.getCustomerName());
        review.setRating(dto.getRating());
        review.setReviewText(dto.getReviewText());
        review.setCustomerEmail(dto.getCustomerEmail());
        review.setCustomerPhone(dto.getCustomerPhone());
        review.setStatus(dto.getStatus() != null ? dto.getStatus() : TourPackageReview.ReviewStatus.ACTIVE);
        review.setIsVerified(dto.getIsVerified() != null ? dto.getIsVerified() : false);

        TourPackageReview savedReview = reviewRepository.save(review);
        return TourPackageReviewResponseDTO.fromEntity(savedReview);
    }

    @Transactional(readOnly = true)
    public TourPackageReviewResponseDTO getReviewById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid review ID");
        }

        TourPackageReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Review not found with id: " + id));

        return TourPackageReviewResponseDTO.fromEntity(review);
    }

    @Transactional(readOnly = true)
    public TourPackageReviewResponseDTO getReviewByBookingId(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new BadRequestException("Booking ID is required");
        }

        TourPackageReview review = reviewRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BadRequestException("Review not found for booking ID: " + bookingId));

        return TourPackageReviewResponseDTO.fromEntity(review);
    }

    @Transactional(readOnly = true)
    public List<TourPackageReviewResponseDTO> getAllReviews() {
        List<TourPackageReview> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(TourPackageReviewResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // User-based methods
    @Transactional(readOnly = true)
    public List<TourPackageReviewResponseDTO> getReviewsByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        List<TourPackageReview> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return reviews.stream()
                .map(TourPackageReviewResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TourPackageReviewResponseDTO> getActiveReviewsByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        List<TourPackageReview> reviews = reviewRepository.findActiveReviewsByUserId(userId);
        return reviews.stream()
                .map(TourPackageReviewResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Tour-based methods
    @Transactional(readOnly = true)
    public List<TourPackageReviewResponseDTO> getReviewsByTourId(Long tourId) {
        if (tourId == null || tourId <= 0) {
            throw new BadRequestException("Invalid tour ID");
        }

        // Validate tour exists
        validateTourExists(tourId);

        List<TourPackageReview> reviews = reviewRepository.findByTourIdOrderByCreatedAtDesc(tourId);
        return reviews.stream()
                .map(TourPackageReviewResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TourPackageReviewResponseDTO> getActiveReviewsByTourId(Long tourId) {
        if (tourId == null || tourId <= 0) {
            throw new BadRequestException("Invalid tour ID");
        }

        // Validate tour exists
        validateTourExists(tourId);

        List<TourPackageReview> reviews = reviewRepository.findActiveReviewsByTourId(tourId);
        return reviews.stream()
                .map(TourPackageReviewResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TourPackageReviewResponseDTO> getReviewsByTourIdAndStatus(Long tourId, TourPackageReview.ReviewStatus status) {
        if (tourId == null || tourId <= 0) {
            throw new BadRequestException("Invalid tour ID");
        }
        if (status == null) {
            throw new BadRequestException("Review status is required");
        }

        // Validate tour exists
        validateTourExists(tourId);

        List<TourPackageReview> reviews = reviewRepository.findByTourIdAndStatus(tourId, status);
        return reviews.stream()
                .map(TourPackageReviewResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TourPackageReviewResponseDTO> getReviewsByTourIdAndRating(Long tourId, Integer rating) {
        if (tourId == null || tourId <= 0) {
            throw new BadRequestException("Invalid tour ID");
        }
        if (rating == null || rating < 1 || rating > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        // Validate tour exists
        validateTourExists(tourId);

        List<TourPackageReview> reviews = reviewRepository.findByTourIdAndRating(tourId, rating);
        return reviews.stream()
                .map(TourPackageReviewResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TourPackageReviewStatsDTO getReviewStatsByTourId(Long tourId) {
        if (tourId == null || tourId <= 0) {
            throw new BadRequestException("Invalid tour ID");
        }

        // Validate tour exists
        validateTourExists(tourId);

        long totalReviews = reviewRepository.countActiveReviewsByTourId(tourId);
        BigDecimal averageRating = reviewRepository.getAverageRatingByTourId(tourId);

        if (averageRating == null) {
            averageRating = BigDecimal.ZERO;
        }

        long fiveStarCount = reviewRepository.countByTourIdAndRatingAndStatusActive(tourId, 5);
        long fourStarCount = reviewRepository.countByTourIdAndRatingAndStatusActive(tourId, 4);
        long threeStarCount = reviewRepository.countByTourIdAndRatingAndStatusActive(tourId, 3);
        long twoStarCount = reviewRepository.countByTourIdAndRatingAndStatusActive(tourId, 2);
        long oneStarCount = reviewRepository.countByTourIdAndRatingAndStatusActive(tourId, 1);

        return new TourPackageReviewStatsDTO(totalReviews, averageRating, fiveStarCount, fourStarCount,
                threeStarCount, twoStarCount, oneStarCount);
    }

    // Combined user and tour method
    @Transactional(readOnly = true)
    public List<TourPackageReviewResponseDTO> getReviewsByUserIdAndTourId(Long userId, Long tourId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }
        if (tourId == null || tourId <= 0) {
            throw new BadRequestException("Invalid tour ID");
        }

        // Validate tour exists
        validateTourExists(tourId);

        List<TourPackageReview> reviews = reviewRepository.findByUserIdAndTourId(userId, tourId);
        return reviews.stream()
                .map(TourPackageReviewResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TourPackageReviewResponseDTO updateReview(Long id, CreateTourPackageReviewDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid review ID");
        }

        validateCreateReviewDTO(dto);

        TourPackageReview existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Review not found with id: " + id));

        // Validate tour exists
        validateTourExists(dto.getTourId());

        // Additional security check: Ensure the existing review's tour is valid
        validateTourExists(existingReview.getTourId());

        // Check if booking ID is being changed and if it already exists
        if (!existingReview.getBookingId().equals(dto.getBookingId()) &&
                reviewRepository.existsByBookingId(dto.getBookingId())) {
            throw new BadRequestException("Review already exists for booking ID: " + dto.getBookingId());
        }

        existingReview.setTourId(dto.getTourId());
        existingReview.setUserId(dto.getUserId());
        existingReview.setBookingId(dto.getBookingId());
        existingReview.setCustomerName(dto.getCustomerName());
        existingReview.setRating(dto.getRating());
        existingReview.setReviewText(dto.getReviewText());
        existingReview.setCustomerEmail(dto.getCustomerEmail());
        existingReview.setCustomerPhone(dto.getCustomerPhone());
        existingReview.setStatus(dto.getStatus() != null ? dto.getStatus() : existingReview.getStatus());
        existingReview.setIsVerified(dto.getIsVerified() != null ? dto.getIsVerified() : existingReview.getIsVerified());

        TourPackageReview updatedReview = reviewRepository.save(existingReview);
        return TourPackageReviewResponseDTO.fromEntity(updatedReview);
    }

    @Transactional
    public TourPackageReviewResponseDTO updateReviewStatus(Long id, TourPackageReview.ReviewStatus status) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid review ID");
        }
        if (status == null) {
            throw new BadRequestException("Review status is required");
        }

        TourPackageReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Review not found with id: " + id));

        // Validate that the review's tour is valid
        validateTourExists(review.getTourId());

        review.setStatus(status);
        TourPackageReview updatedReview = reviewRepository.save(review);
        return TourPackageReviewResponseDTO.fromEntity(updatedReview);
    }

    @Transactional
    public TourPackageReviewResponseDTO updateReviewVerification(Long id, Boolean isVerified) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid review ID");
        }
        if (isVerified == null) {
            throw new BadRequestException("Verification status is required");
        }

        TourPackageReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Review not found with id: " + id));

        // Validate that the review's tour is valid
        validateTourExists(review.getTourId());

        review.setIsVerified(isVerified);
        TourPackageReview updatedReview = reviewRepository.save(review);
        return TourPackageReviewResponseDTO.fromEntity(updatedReview);
    }

    @Transactional
    public boolean deleteReview(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid review ID");
        }

        TourPackageReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Review not found with id: " + id));

        // Validate that the review's tour is valid
        validateTourExists(review.getTourId());

        reviewRepository.delete(review);
        return true;
    }

    private void validateCreateReviewDTO(CreateTourPackageReviewDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Review data cannot be null");
        }
        if (dto.getTourId() == null || dto.getTourId() <= 0) {
            throw new BadRequestException("Tour ID is required and must be positive");
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
