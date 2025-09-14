package com.example.serviceproviders_service.controller.ServiceProvider;

import com.example.serviceproviders_service.dto.ServiceProvider.CreateReviewDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.ReviewResponseDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.ReviewStatsDTO;
import com.example.serviceproviders_service.entity.serviceProvider.Review;
import com.example.serviceproviders_service.services.ServiceProvider.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/create")
    public ResponseEntity<ReviewResponseDTO> createReview(@RequestBody CreateReviewDTO dto) {
        ReviewResponseDTO createdReview = reviewService.createReview(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable Long id) {
        ReviewResponseDTO review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ReviewResponseDTO> getReviewByBookingId(@PathVariable String bookingId) {
        ReviewResponseDTO review = reviewService.getReviewByBookingId(bookingId);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        List<ReviewResponseDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    // User-based endpoints
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByUserId(@PathVariable Long userId) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<ReviewResponseDTO>> getActiveReviewsByUserId(@PathVariable Long userId) {
        List<ReviewResponseDTO> reviews = reviewService.getActiveReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    // Service provider-based endpoints
    @GetMapping("/service-provider/{serviceProviderId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByServiceProviderId(@PathVariable Long serviceProviderId) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByServiceProviderId(serviceProviderId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/service-provider/{serviceProviderId}/active")
    public ResponseEntity<List<ReviewResponseDTO>> getActiveReviewsByServiceProviderId(@PathVariable Long serviceProviderId) {
        List<ReviewResponseDTO> reviews = reviewService.getActiveReviewsByServiceProviderId(serviceProviderId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/service-provider/{serviceProviderId}/status/{status}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByServiceProviderIdAndStatus(
            @PathVariable Long serviceProviderId,
            @PathVariable Review.ReviewStatus status) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByServiceProviderIdAndStatus(serviceProviderId, status);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/service-provider/{serviceProviderId}/rating/{rating}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByServiceProviderIdAndRating(
            @PathVariable Long serviceProviderId,
            @PathVariable Integer rating) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByServiceProviderIdAndRating(serviceProviderId, rating);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/service-provider/{serviceProviderId}/stats")
    public ResponseEntity<ReviewStatsDTO> getReviewStatsByServiceProviderId(@PathVariable Long serviceProviderId) {
        ReviewStatsDTO stats = reviewService.getReviewStatsByServiceProviderId(serviceProviderId);
        return ResponseEntity.ok(stats);
    }

    // Combined user and service provider endpoint
    @GetMapping("/user/{userId}/service-provider/{serviceProviderId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByUserIdAndServiceProviderId(
            @PathVariable Long userId,
            @PathVariable Long serviceProviderId) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByUserIdAndServiceProviderId(userId, serviceProviderId);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long id,
            @RequestBody CreateReviewDTO dto) {
        ReviewResponseDTO updatedReview = reviewService.updateReview(id, dto);
        return ResponseEntity.ok(updatedReview);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ReviewResponseDTO> updateReviewStatus(
            @PathVariable Long id,
            @RequestParam Review.ReviewStatus status) {
        ReviewResponseDTO updatedReview = reviewService.updateReviewStatus(id, status);
        return ResponseEntity.ok(updatedReview);
    }

    @PatchMapping("/{id}/verification")
    public ResponseEntity<ReviewResponseDTO> updateReviewVerification(
            @PathVariable Long id,
            @RequestParam Boolean isVerified) {
        ReviewResponseDTO updatedReview = reviewService.updateReviewVerification(id, isVerified);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        boolean response = reviewService.deleteReview(id);
        if (response) {
            return ResponseEntity.ok("Review deleted successfully");
        }
        return ResponseEntity.noContent().build();
    }
}