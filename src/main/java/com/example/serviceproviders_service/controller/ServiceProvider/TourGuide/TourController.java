package com.example.serviceproviders_service.controller.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.CreateTourDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.TourResponseDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.AddPastTourImagesDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.TourGuide.PastTourImageResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.Tour;
import com.example.serviceproviders_service.services.ServiceProvider.TourGuide.TourService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service/tours")
@CrossOrigin(origins = "*")
public class TourController {

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    // ==================== Tour Management Endpoints ====================

    @PostMapping("/create")
    public ResponseEntity<TourResponseDTO> createTour(@RequestBody CreateTourDTO dto) {
        TourResponseDTO createdTour = tourService.createTour(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTour);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourResponseDTO> getTourById(@PathVariable Long id) {
        TourResponseDTO tour = tourService.getTourById(id);
        return ResponseEntity.ok(tour);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TourResponseDTO>> getAllTours() {
        List<TourResponseDTO> tours = tourService.getAllTours();
        return ResponseEntity.ok(tours);
    }

    @GetMapping("/service-provider/{serviceProviderId}")
    public ResponseEntity<List<TourResponseDTO>> getToursByServiceProviderId(@PathVariable Long serviceProviderId) {
        List<TourResponseDTO> tours = tourService.getToursByServiceProviderId(serviceProviderId);
        return ResponseEntity.ok(tours);
    }

    @GetMapping("/service-provider/{serviceProviderId}/status/{status}")
    public ResponseEntity<List<TourResponseDTO>> getToursByServiceProviderIdAndStatus(
            @PathVariable Long serviceProviderId,
            @PathVariable Tour.TourStatus status) {
        List<TourResponseDTO> tours = tourService.getToursByServiceProviderIdAndStatus(serviceProviderId, status);
        return ResponseEntity.ok(tours);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourResponseDTO> updateTour(
            @PathVariable Long id,
            @RequestBody CreateTourDTO dto) {
        TourResponseDTO updatedTour = tourService.updateTour(id, dto);
        return ResponseEntity.ok(updatedTour);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TourResponseDTO> updateTourStatus(
            @PathVariable Long id,
            @RequestParam Tour.TourStatus status) {
        TourResponseDTO updatedTour = tourService.updateTourStatus(id, status);
        return ResponseEntity.ok(updatedTour);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTour(@PathVariable Long id) {
        boolean response = tourService.deleteTour(id);
        if (response) {
            return ResponseEntity.ok("Tour deleted successfully");
        }
        return ResponseEntity.noContent().build();
    }

    // ==================== Past Tour Images Endpoints ====================

    @PostMapping("/{tourId}/past-images")
    public ResponseEntity<List<PastTourImageResponseDTO>> addPastTourImages(
            @PathVariable Long tourId,
            @RequestBody AddPastTourImagesDTO dto) {
        dto.setTourId(tourId);
        List<PastTourImageResponseDTO> images = tourService.addPastTourImages(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(images);
    }

    @GetMapping("/{tourId}/past-images")
    public ResponseEntity<List<PastTourImageResponseDTO>> getPastTourImages(@PathVariable Long tourId) {
        List<PastTourImageResponseDTO> images = tourService.getPastTourImages(tourId);
        return ResponseEntity.ok(images);
    }

    @PutMapping("/past-images/{imageId}")
    public ResponseEntity<PastTourImageResponseDTO> updatePastTourImage(
            @PathVariable Long imageId,
            @RequestParam String imageUrl) {
        PastTourImageResponseDTO updatedImage = tourService.updatePastTourImage(imageId, imageUrl);
        return ResponseEntity.ok(updatedImage);
    }

    @DeleteMapping("/past-images/{imageId}")
    public ResponseEntity<String> deletePastTourImage(@PathVariable Long imageId) {
        tourService.deletePastTourImage(imageId);
        return ResponseEntity.ok("Past tour image deleted successfully");
    }

    @DeleteMapping("/{tourId}/past-images")
    public ResponseEntity<String> deletePastTourImagesByTourId(@PathVariable Long tourId) {
        tourService.deletePastTourImagesByTourId(tourId);
        return ResponseEntity.ok("All past tour images for this tour deleted successfully");
    }

    @GetMapping("/{tourId}/past-images/count")
    public ResponseEntity<Long> getPastTourImagesCount(@PathVariable Long tourId) {
        long count = tourService.getPastTourImagesCount(tourId);
        return ResponseEntity.ok(count);
    }
}