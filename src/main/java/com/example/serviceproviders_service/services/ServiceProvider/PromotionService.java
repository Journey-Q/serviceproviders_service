package com.example.serviceproviders_service.services.ServiceProvider;

import com.example.serviceproviders_service.dto.ServiceProvider.CreatePromotionDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.PromotionPaymentRequest;
import com.example.serviceproviders_service.dto.ServiceProvider.PromotionPaymentResponse;
import com.example.serviceproviders_service.dto.ServiceProvider.PromotionResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.Promotion;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.repository.ServiceProvider.ServiceProviderRepo;
import com.example.serviceproviders_service.repository.ServiceProvider.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private ServiceProviderRepo serviceProviderRepo;

    // Service provider validation method (no service type restriction)
    private void validateServiceProviderExists(Long serviceProviderId) {
        serviceProviderRepo.findById(serviceProviderId)
                .orElseThrow(() -> new BadRequestException("Service provider not found with id: " + serviceProviderId));
    }

    @Transactional
    public PromotionResponseDTO createPromotion(CreatePromotionDTO dto) {
        validateCreatePromotionDTO(dto);

        // Validate service provider exists (any type allowed)
        validateServiceProviderExists(dto.getServiceProviderId());

        if (promotionRepository.existsByServiceProviderIdAndTitle(dto.getServiceProviderId(), dto.getTitle())) {
            throw new BadRequestException("Promotion with this title already exists for this service provider");
        }

        Promotion promotion = new Promotion();
        promotion.setServiceProviderId(dto.getServiceProviderId());
        promotion.setTitle(dto.getTitle());
        promotion.setDescription(dto.getDescription());
        promotion.setImage(dto.getImage());
        promotion.setDiscount(dto.getDiscount());
        promotion.setValidFrom(dto.getValidFrom());
        promotion.setValidTo(dto.getValidTo());
        promotion.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        // New promotions always start as REQUESTED (waiting for admin approval)
        promotion.setStatus(Promotion.PromotionStatus.REQUESTED);

        Promotion savedPromotion = promotionRepository.save(promotion);
        return PromotionResponseDTO.fromEntity(savedPromotion);
    }

    @Transactional(readOnly = true)
    public PromotionResponseDTO getPromotionById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid promotion ID");
        }
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Promotion not found with id: " + id));
        return PromotionResponseDTO.fromEntity(promotion);
    }

    @Transactional(readOnly = true)
    public List<PromotionResponseDTO> getAllPromotions() {
        return promotionRepository.findAll()
                .stream()
                .map(PromotionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionResponseDTO> getPromotionsByServiceProviderId(Long serviceProviderId) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }

        // Validate service provider exists
        validateServiceProviderExists(serviceProviderId);

        return promotionRepository.findByServiceProviderId(serviceProviderId)
                .stream()
                .map(PromotionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionResponseDTO> getPromotionsByServiceProviderIdAndStatus(Long serviceProviderId, Promotion.PromotionStatus status) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }
        if (status == null) {
            throw new BadRequestException("Promotion status is required");
        }

        // Validate service provider exists
        validateServiceProviderExists(serviceProviderId);

        return promotionRepository.findByServiceProviderIdAndStatus(serviceProviderId, status)
                .stream()
                .map(PromotionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionResponseDTO> getPromotionsByStatus(Promotion.PromotionStatus status) {
        if (status == null) {
            throw new BadRequestException("Promotion status is required");
        }
        return promotionRepository.findByStatus(status)
                .stream()
                .map(PromotionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionResponseDTO> getCurrentActivePromotions() {
        LocalDate currentDate = LocalDate.now();
        return promotionRepository.findCurrentActivePromotions(currentDate)
                .stream()
                .map(PromotionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public PromotionResponseDTO updatePromotion(Long id, CreatePromotionDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid promotion ID");
        }

        validateCreatePromotionDTO(dto);

        Promotion existingPromotion = promotionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Promotion not found with id: " + id));

        // Validate service provider exists (any type allowed)
        validateServiceProviderExists(dto.getServiceProviderId());

        // Additional security check: Ensure the existing promotion belongs to a valid service provider
        validateServiceProviderExists(existingPromotion.getServiceProviderId());

        if (promotionRepository.existsByServiceProviderIdAndTitleAndIdNot(dto.getServiceProviderId(), dto.getTitle(), id)) {
            throw new BadRequestException("Promotion with this title already exists for this service provider");
        }

        existingPromotion.setServiceProviderId(dto.getServiceProviderId());
        existingPromotion.setTitle(dto.getTitle());
        existingPromotion.setDescription(dto.getDescription());
        existingPromotion.setImage(dto.getImage());
        existingPromotion.setDiscount(dto.getDiscount());
        existingPromotion.setValidFrom(dto.getValidFrom());
        existingPromotion.setValidTo(dto.getValidTo());
        existingPromotion.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : existingPromotion.getIsActive());

        // Only update status if explicitly provided and promotion is not currently advertised
        if (dto.getStatus() != null && existingPromotion.getStatus() != Promotion.PromotionStatus.ADVERTISED) {
            existingPromotion.setStatus(dto.getStatus());
        }

        Promotion updatedPromotion = promotionRepository.save(existingPromotion);
        return PromotionResponseDTO.fromEntity(updatedPromotion);
    }

    @Transactional
    public PromotionResponseDTO updatePromotionStatus(Long id, Promotion.PromotionStatus status) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid promotion ID");
        }
        if (status == null) {
            throw new BadRequestException("Promotion status is required");
        }

        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Promotion not found with id: " + id));

        // Validate that the promotion belongs to a valid service provider
        validateServiceProviderExists(promotion.getServiceProviderId());

        promotion.setStatus(status);
        Promotion updatedPromotion = promotionRepository.save(promotion);
        return PromotionResponseDTO.fromEntity(updatedPromotion);
    }

    @Transactional
    public PromotionResponseDTO togglePromotionActive(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid promotion ID");
        }

        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Promotion not found with id: " + id));

        // Validate that the promotion belongs to a valid service provider
        validateServiceProviderExists(promotion.getServiceProviderId());

        promotion.setIsActive(!promotion.getIsActive());
        Promotion updatedPromotion = promotionRepository.save(promotion);
        return PromotionResponseDTO.fromEntity(updatedPromotion);
    }

    @Transactional
    public boolean deletePromotion(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid promotion ID");
        }
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Promotion not found with id: " + id));

        // Validate that the promotion belongs to a valid service provider
        validateServiceProviderExists(promotion.getServiceProviderId());

        // Don't allow deletion of currently advertised promotions
        if (promotion.getStatus() == Promotion.PromotionStatus.ADVERTISED) {
            throw new BadRequestException("Cannot delete an advertised promotion. Please disable it first.");
        }

        promotionRepository.delete(promotion);
        return true;
    }

    // ==================== PAYMENT METHODS ====================

    /**
     * Process payment for promotion advertisement
     */
    @Transactional
    public PromotionPaymentResponse processPromotionPayment(PromotionPaymentRequest paymentRequest) {
        // Validate payment request
        validatePaymentRequest(paymentRequest);

        // Get promotion
        Promotion promotion = promotionRepository.findById(paymentRequest.getPromotionId())
                .orElseThrow(() -> new BadRequestException("Promotion not found with id: " + paymentRequest.getPromotionId()));

        // Check if promotion is approved by admin
        if (promotion.getStatus() != Promotion.PromotionStatus.APPROVED) {
            throw new BadRequestException("Promotion must be approved by admin before payment. Current status: " + promotion.getStatus());
        }

        // Check if already paid
        if (promotion.getIsPaid()) {
            throw new BadRequestException("Promotion has already been paid for");
        }

        // Get plan details
        Map<String, Object> planDetails = getPlanDetails(paymentRequest.getPlan());

        // Simulate payment processing (validate card details)
        boolean paymentSuccess = processPayment(paymentRequest);

        if (!paymentSuccess) {
            return PromotionPaymentResponse.builder()
                    .promotionId(promotion.getId())
                    .status("FAILED")
                    .message("Payment processing failed. Please check your card details and try again.")
                    .build();
        }

        // Generate transaction reference
        String transactionId = "TXN" + System.currentTimeMillis();
        String paymentReference = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Extract last 4 digits of card
        String cardLastFour = paymentRequest.getCardNumber().replaceAll("\\s", "");
        cardLastFour = cardLastFour.substring(cardLastFour.length() - 4);

        // Calculate total amount with GST
        BigDecimal baseAmount = (BigDecimal) planDetails.get("price");
        BigDecimal gstAmount = baseAmount.multiply(new BigDecimal("0.18"));
        BigDecimal totalAmount = baseAmount.add(gstAmount);

        // Update promotion with payment details
        promotion.setIsPaid(true);
        promotion.setPaymentAmount(totalAmount);
        promotion.setPaymentPlan(paymentRequest.getPlan());
        promotion.setAdvertisementDuration((String) planDetails.get("duration"));
        promotion.setTransactionId(transactionId);
        promotion.setPaymentReferenceNumber(paymentReference);
        promotion.setPaymentDate(LocalDateTime.now());
        promotion.setCardLastFourDigits(cardLastFour);
        promotion.setCardholderName(paymentRequest.getCardholderName());
        promotion.setStatus(Promotion.PromotionStatus.ADVERTISED); // Change status to ADVERTISED

        Promotion updatedPromotion = promotionRepository.save(promotion);

        // Return success response
        return PromotionPaymentResponse.builder()
                .promotionId(updatedPromotion.getId())
                .transactionId(transactionId)
                .paymentReferenceNumber(paymentReference)
                .amount(totalAmount)
                .plan(paymentRequest.getPlan())
                .duration((String) planDetails.get("duration"))
                .paymentDate(updatedPromotion.getPaymentDate())
                .cardLastFourDigits(cardLastFour)
                .cardholderName(paymentRequest.getCardholderName())
                .status("SUCCESS")
                .message("Payment successful! Your promotion is now being advertised.")
                .build();
    }

    /**
     * Get pricing plan details
     */
    private Map<String, Object> getPlanDetails(String plan) {
        Map<String, Object> planMap = new HashMap<>();

        switch (plan.toLowerCase()) {
            case "basic":
                planMap.put("name", "Basic Plan");
                planMap.put("price", new BigDecimal("1999"));
                planMap.put("duration", "7 days");
                break;
            case "premium":
                planMap.put("name", "Premium Plan");
                planMap.put("price", new BigDecimal("4999"));
                planMap.put("duration", "30 days");
                break;
            case "featured":
                planMap.put("name", "Featured Plan");
                planMap.put("price", new BigDecimal("9999"));
                planMap.put("duration", "60 days");
                break;
            default:
                throw new BadRequestException("Invalid plan selected. Available plans: basic, premium, featured");
        }

        return planMap;
    }

    /**
     * Simulate payment processing (In real scenario, integrate with payment gateway)
     */
    private boolean processPayment(PromotionPaymentRequest paymentRequest) {
        // Basic validation (in real scenario, this would call payment gateway API)

        // Validate card number (simple Luhn algorithm check)
        String cardNumber = paymentRequest.getCardNumber().replaceAll("\\s", "");
        if (cardNumber.length() < 13 || cardNumber.length() > 19) {
            throw new BadRequestException("Invalid card number");
        }

        // Validate expiry date
        if (paymentRequest.getExpiryDate() == null || !paymentRequest.getExpiryDate().matches("\\d{2}/\\d{2}")) {
            throw new BadRequestException("Invalid expiry date format. Use MM/YY");
        }

        // Validate CVV
        if (paymentRequest.getCvv() == null || !paymentRequest.getCvv().matches("\\d{3,4}")) {
            throw new BadRequestException("Invalid CVV");
        }

        // Simulate successful payment (always returns true for now)
        // In production, integrate with actual payment gateway
        return true;
    }

    /**
     * Validate payment request
     */
    private void validatePaymentRequest(PromotionPaymentRequest request) {
        if (request == null) {
            throw new BadRequestException("Payment request cannot be null");
        }
        if (request.getPromotionId() == null || request.getPromotionId() <= 0) {
            throw new BadRequestException("Valid promotion ID is required");
        }
        if (request.getPlan() == null || request.getPlan().trim().isEmpty()) {
            throw new BadRequestException("Payment plan is required");
        }
        if (request.getCardNumber() == null || request.getCardNumber().trim().isEmpty()) {
            throw new BadRequestException("Card number is required");
        }
        if (request.getExpiryDate() == null || request.getExpiryDate().trim().isEmpty()) {
            throw new BadRequestException("Card expiry date is required");
        }
        if (request.getCvv() == null || request.getCvv().trim().isEmpty()) {
            throw new BadRequestException("CVV is required");
        }
        if (request.getCardholderName() == null || request.getCardholderName().trim().isEmpty()) {
            throw new BadRequestException("Cardholder name is required");
        }
        if (request.getBillingAddress() == null || request.getBillingAddress().trim().isEmpty()) {
            throw new BadRequestException("Billing address is required");
        }
        if (request.getCity() == null || request.getCity().trim().isEmpty()) {
            throw new BadRequestException("City is required");
        }
        if (request.getZipCode() == null || request.getZipCode().trim().isEmpty()) {
            throw new BadRequestException("ZIP code is required");
        }
    }

    /**
     * Get payment details for a promotion
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPromotionPaymentDetails(Long promotionId) {
        if (promotionId == null || promotionId <= 0) {
            throw new BadRequestException("Invalid promotion ID");
        }

        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new BadRequestException("Promotion not found with id: " + promotionId));

        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("promotionId", promotion.getId());
        paymentDetails.put("isPaid", promotion.getIsPaid());
        paymentDetails.put("paymentAmount", promotion.getPaymentAmount());
        paymentDetails.put("paymentPlan", promotion.getPaymentPlan());
        paymentDetails.put("advertisementDuration", promotion.getAdvertisementDuration());
        paymentDetails.put("transactionId", promotion.getTransactionId());
        paymentDetails.put("paymentReferenceNumber", promotion.getPaymentReferenceNumber());
        paymentDetails.put("paymentDate", promotion.getPaymentDate());
        paymentDetails.put("cardLastFourDigits", promotion.getCardLastFourDigits());
        paymentDetails.put("cardholderName", promotion.getCardholderName());
        paymentDetails.put("status", promotion.getStatus());

        return paymentDetails;
    }

    private void validateCreatePromotionDTO(CreatePromotionDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Promotion data cannot be null");
        }
        if (dto.getServiceProviderId() == null || dto.getServiceProviderId() <= 0) {
            throw new BadRequestException("Service Provider ID is required and must be positive");
        }
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Promotion title is required");
        }
        if (dto.getTitle().length() > 200) {
            throw new BadRequestException("Promotion title cannot exceed 200 characters");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new BadRequestException("Promotion description is required");
        }
        if (dto.getDiscount() == null || dto.getDiscount() < 0 || dto.getDiscount() > 100) {
            throw new BadRequestException("Discount must be between 0 and 100 percent");
        }
        if (dto.getValidFrom() == null) {
            throw new BadRequestException("Valid from date is required");
        }
        if (dto.getValidTo() == null) {
            throw new BadRequestException("Valid to date is required");
        }
        if (dto.getValidFrom().isAfter(dto.getValidTo())) {
            throw new BadRequestException("Valid from date must be before valid to date");
        }
        if (dto.getValidTo().isBefore(LocalDate.now())) {
            throw new BadRequestException("Valid to date cannot be in the past");
        }
    }
}