package com.example.serviceproviders_service.services.ServiceProvider;

import com.example.serviceproviders_service.dto.ServiceProvider.CreatePromotionDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.PromotionResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.Promotion;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.repository.ServiceProvider.ServiceProviderRepo;
import com.example.serviceproviders_service.repository.ServiceProvider.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
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