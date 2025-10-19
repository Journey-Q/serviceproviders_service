package com.example.serviceproviders_service.controller.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.CreateTourPackageReviewDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.TourPackageReviewResponseDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.TourPackageReviewStatsDTO;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourPackageReview;
import com.example.serviceproviders_service.services.ServiceProvider.TourGuide.TourPackageReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service/tour-package-reviews")
@CrossOrigin(origins = "*")
public class TourPackageReviewController {

    private final TourPackageReviewService reviewService;

    public TourPackageReviewController(TourPackageReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/create")
    public ResponseEntity<TourPackageReviewResponseDTO> createReview(@RequestBody CreateTourPackageReviewDTO dto) {
        TourPackageReviewResponseDTO createdReview = reviewService.createReview(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourPackageReviewResponseDTO> getReviewById(@PathVariable Long id) {
        TourPackageReviewResponseDTO review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<TourPackageReviewResponseDTO> getReviewByBookingId(@PathVariable String bookingId) {
        TourPackageReviewResponseDTO review = reviewService.getReviewByBookingId(bookingId);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TourPackageReviewResponseDTO>> getAllReviews() {
        List<TourPackageReviewResponseDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    // User-based endpoints
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TourPackageReviewResponseDTO>> getReviewsByUserId(@PathVariable Long userId) {
        List<TourPackageReviewResponseDTO> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<TourPackageReviewResponseDTO>> getActiveReviewsByUserId(@PathVariable Long userId) {
        List<TourPackageReviewResponseDTO> reviews = reviewService.getActiveReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    // Tour-based endpoints
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<TourPackageReviewResponseDTO>> getReviewsByTourId(@PathVariable Long tourId) {
        List<TourPackageReviewResponseDTO> reviews = reviewService.getReviewsByTourId(tourId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/tour/{tourId}/active")
    public ResponseEntity<List<TourPackageReviewResponseDTO>> getActiveReviewsByTourId(@PathVariable Long tourId) {
        List<TourPackageReviewResponseDTO> reviews = reviewService.getActiveReviewsByTourId(tourId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/tour/{tourId}/status/{status}")
    public ResponseEntity<List<TourPackageReviewResponseDTO>> getReviewsByTourIdAndStatus(
            @PathVariable Long tourId,
            @PathVariable TourPackageReview.ReviewStatus status) {
        List<TourPackageReviewResponseDTO> reviews = reviewService.getReviewsByTourIdAndStatus(tourId, status);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/tour/{tourId}/rating/{rating}")
    public ResponseEntity<List<TourPackageReviewResponseDTO>> getReviewsByTourIdAndRating(
            @PathVariable Long tourId,
            @PathVariable Integer rating) {
        List<TourPackageReviewResponseDTO> reviews = reviewService.getReviewsByTourIdAndRating(tourId, rating);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/tour/{tourId}/stats")
    public ResponseEntity<TourPackageReviewStatsDTO> getReviewStatsByTourId(@PathVariable Long tourId) {
        TourPackageReviewStatsDTO stats = reviewService.getReviewStatsByTourId(tourId);
        return ResponseEntity.ok(stats);
    }

    // Combined user and tour endpoint
    @GetMapping("/user/{userId}/tour/{tourId}")
    public ResponseEntity<List<TourPackageReviewResponseDTO>> getReviewsByUserIdAndTourId(
            @PathVariable Long userId,
            @PathVariable Long tourId) {
        List<TourPackageReviewResponseDTO> reviews = reviewService.getReviewsByUserIdAndTourId(userId, tourId);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourPackageReviewResponseDTO> updateReview(
            @PathVariable Long id,
            @RequestBody CreateTourPackageReviewDTO dto) {
        TourPackageReviewResponseDTO updatedReview = reviewService.updateReview(id, dto);
        return ResponseEntity.ok(updatedReview);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TourPackageReviewResponseDTO> updateReviewStatus(
            @PathVariable Long id,
            @RequestParam TourPackageReview.ReviewStatus status) {
        TourPackageReviewResponseDTO updatedReview = reviewService.updateReviewStatus(id, status);
        return ResponseEntity.ok(updatedReview);
    }

    @PatchMapping("/{id}/verification")
    public ResponseEntity<TourPackageReviewResponseDTO> updateReviewVerification(
            @PathVariable Long id,
            @RequestParam Boolean isVerified) {
        TourPackageReviewResponseDTO updatedReview = reviewService.updateReviewVerification(id, isVerified);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        boolean response = reviewService.deleteReview(id);
        if (response) {
            return ResponseEntity.ok("Tour package review deleted successfully");
        }
        return ResponseEntity.noContent().build();
    }
}
