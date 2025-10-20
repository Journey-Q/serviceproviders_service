package com.example.serviceproviders_service.controller.ServiceProvider;

import com.example.serviceproviders_service.dto.ServiceProvider.CreatePromotionDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.PromotionPaymentRequest;
import com.example.serviceproviders_service.dto.ServiceProvider.PromotionPaymentResponse;
import com.example.serviceproviders_service.dto.ServiceProvider.PromotionResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.Promotion;
import com.example.serviceproviders_service.services.ServiceProvider.PromotionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/service/promotions")
@CrossOrigin(origins = "*")
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @PostMapping("/create")
    public ResponseEntity<PromotionResponseDTO> createPromotion(@RequestBody CreatePromotionDTO dto) {
        PromotionResponseDTO createdPromotion = promotionService.createPromotion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPromotion);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionResponseDTO> getPromotionById(@PathVariable Long id) {
        PromotionResponseDTO promotion = promotionService.getPromotionById(id);
        return ResponseEntity.ok(promotion);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PromotionResponseDTO>> getAllPromotions() {
        List<PromotionResponseDTO> promotions = promotionService.getAllPromotions();
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/service-provider/{serviceProviderId}")
    public ResponseEntity<List<PromotionResponseDTO>> getPromotionsByServiceProviderId(@PathVariable Long serviceProviderId) {
        List<PromotionResponseDTO> promotions = promotionService.getPromotionsByServiceProviderId(serviceProviderId);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/service-provider/{serviceProviderId}/status/{status}")
    public ResponseEntity<List<PromotionResponseDTO>> getPromotionsByServiceProviderIdAndStatus(
            @PathVariable Long serviceProviderId,
            @PathVariable Promotion.PromotionStatus status) {
        List<PromotionResponseDTO> promotions = promotionService.getPromotionsByServiceProviderIdAndStatus(serviceProviderId, status);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PromotionResponseDTO>> getPromotionsByStatus(@PathVariable Promotion.PromotionStatus status) {
        List<PromotionResponseDTO> promotions = promotionService.getPromotionsByStatus(status);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PromotionResponseDTO>> getCurrentActivePromotions() {
        List<PromotionResponseDTO> promotions = promotionService.getCurrentActivePromotions();
        return ResponseEntity.ok(promotions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromotionResponseDTO> updatePromotion(
            @PathVariable Long id,
            @RequestBody CreatePromotionDTO dto) {
        PromotionResponseDTO updatedPromotion = promotionService.updatePromotion(id, dto);
        return ResponseEntity.ok(updatedPromotion);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PromotionResponseDTO> updatePromotionStatus(
            @PathVariable Long id,
            @RequestParam Promotion.PromotionStatus status) {
        PromotionResponseDTO updatedPromotion = promotionService.updatePromotionStatus(id, status);
        return ResponseEntity.ok(updatedPromotion);
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<PromotionResponseDTO> togglePromotionActive(@PathVariable Long id) {
        PromotionResponseDTO updatedPromotion = promotionService.togglePromotionActive(id);
        return ResponseEntity.ok(updatedPromotion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePromotion(@PathVariable Long id) {
        boolean response = promotionService.deletePromotion(id);
        if (response) {
            return ResponseEntity.ok("Promotion deleted successfully");
        }
        return ResponseEntity.noContent().build();
    }

    // ==================== PAYMENT ENDPOINTS ====================

    /**
     * Process payment for promotion advertisement
     * Endpoint: POST /service/promotions/pay
     */
    @PostMapping("/pay")
    public ResponseEntity<PromotionPaymentResponse> processPromotionPayment(@RequestBody PromotionPaymentRequest paymentRequest) {
        PromotionPaymentResponse response = promotionService.processPromotionPayment(paymentRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment details for a promotion
     * Endpoint: GET /service/promotions/{id}/payment-details
     */
    @GetMapping("/{id}/payment-details")
    public ResponseEntity<Map<String, Object>> getPromotionPaymentDetails(@PathVariable Long id) {
        Map<String, Object> paymentDetails = promotionService.getPromotionPaymentDetails(id);
        return ResponseEntity.ok(paymentDetails);
    }

    /**
     * Get all paid/advertised promotions (for admin to see all currently running ads)
     * Endpoint: GET /service/promotions/advertised
     */
    @GetMapping("/advertised")
    public ResponseEntity<List<PromotionResponseDTO>> getAdvertisedPromotions() {
        List<PromotionResponseDTO> promotions = promotionService.getPromotionsByStatus(Promotion.PromotionStatus.ADVERTISED);
        return ResponseEntity.ok(promotions);
    }

    /**
     * Get all approved promotions waiting for payment
     * Endpoint: GET /service/promotions/approved-pending-payment
     */
    @GetMapping("/approved-pending-payment")
    public ResponseEntity<List<PromotionResponseDTO>> getApprovedPendingPayment() {
        List<PromotionResponseDTO> promotions = promotionService.getPromotionsByStatus(Promotion.PromotionStatus.APPROVED);
        return ResponseEntity.ok(promotions);
    }
}