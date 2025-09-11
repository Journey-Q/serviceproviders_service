package com.example.serviceproviders_service.services.ServiceProvider;

import com.example.serviceproviders_service.dto.ServiceProvider.CreateBankDetailsDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.BankDetailsResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.BankDetails;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProvider;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.repository.ServiceProvider.BankDetailsRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.ServiceProviderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankDetailsService {

    @Autowired
    private BankDetailsRepository bankDetailsRepository;

    @Autowired
    private ServiceProviderRepo serviceProviderRepo;

    // Service provider validation method (no service type restriction)
    private void validateServiceProviderExists(Long serviceProviderId) {
        serviceProviderRepo.findById(serviceProviderId)
                .orElseThrow(() -> new BadRequestException("Service provider not found with id: " + serviceProviderId));
    }

    @Transactional
    public BankDetailsResponseDTO createBankDetails(CreateBankDetailsDTO dto) {
        validateCreateBankDetailsDTO(dto);

        // Validate service provider exists (any type allowed)
        validateServiceProviderExists(dto.getServiceProviderId());

        if (bankDetailsRepository.existsByServiceProviderId(dto.getServiceProviderId())) {
            throw new BadRequestException("Bank details already exist for this service provider");
        }

        if (bankDetailsRepository.existsByAccountNumber(dto.getAccountNumber())) {
            throw new BadRequestException("Account number already exists in the system");
        }

        BankDetails bankDetails = new BankDetails();
        bankDetails.setServiceProviderId(dto.getServiceProviderId());
        bankDetails.setAccountHolderName(dto.getAccountHolderName());
        bankDetails.setAccountNumber(dto.getAccountNumber());
        bankDetails.setIfscCode(dto.getIfscCode());
        bankDetails.setBankName(dto.getBankName());
        bankDetails.setBranchName(dto.getBranchName());
        bankDetails.setAccountType(dto.getAccountType() != null ? dto.getAccountType() : BankDetails.AccountType.SAVINGS);
        bankDetails.setMobileNumber(dto.getMobileNumber());
        bankDetails.setEmailId(dto.getEmailId());
        bankDetails.setIsVerified(false);
        bankDetails.setVerificationStatus(BankDetails.VerificationStatus.PENDING);

        BankDetails savedBankDetails = bankDetailsRepository.save(bankDetails);
        return BankDetailsResponseDTO.fromEntity(savedBankDetails);
    }

    @Transactional(readOnly = true)
    public BankDetailsResponseDTO getBankDetailsByServiceProviderId(Long serviceProviderId) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }

        // Validate service provider exists
        validateServiceProviderExists(serviceProviderId);

        BankDetails bankDetails = bankDetailsRepository.findByServiceProviderId(serviceProviderId)
                .orElseThrow(() -> new BadRequestException("Bank details not found for service provider with id: " + serviceProviderId));

        return BankDetailsResponseDTO.fromEntity(bankDetails);
    }

    @Transactional(readOnly = true)
    public BankDetailsResponseDTO getBankDetailsById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid bank details ID");
        }

        BankDetails bankDetails = bankDetailsRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Bank details not found with id: " + id));

        return BankDetailsResponseDTO.fromEntity(bankDetails);
    }

    @Transactional(readOnly = true)
    public List<BankDetailsResponseDTO> getAllBankDetails() {
        List<BankDetails> bankDetailsList = bankDetailsRepository.findAll();
        return bankDetailsList.stream()
                .map(BankDetailsResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BankDetailsResponseDTO> getBankDetailsByVerificationStatus(BankDetails.VerificationStatus status) {
        if (status == null) {
            throw new BadRequestException("Verification status is required");
        }

        List<BankDetails> bankDetailsList = bankDetailsRepository.findByVerificationStatus(status);
        return bankDetailsList.stream()
                .map(BankDetailsResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public BankDetailsResponseDTO updateBankDetails(Long serviceProviderId, CreateBankDetailsDTO dto) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }

        validateCreateBankDetailsDTO(dto);

        // Validate service provider exists
        validateServiceProviderExists(serviceProviderId);

        BankDetails existingBankDetails = bankDetailsRepository.findByServiceProviderId(serviceProviderId)
                .orElseThrow(() -> new BadRequestException("Bank details not found for service provider with id: " + serviceProviderId));

        // Check if account number is being changed and if it already exists
        if (!existingBankDetails.getAccountNumber().equals(dto.getAccountNumber()) &&
                bankDetailsRepository.existsByAccountNumberAndServiceProviderIdNot(dto.getAccountNumber(), serviceProviderId)) {
            throw new BadRequestException("Account number already exists in the system");
        }

        existingBankDetails.setAccountHolderName(dto.getAccountHolderName());
        existingBankDetails.setAccountNumber(dto.getAccountNumber());
        existingBankDetails.setIfscCode(dto.getIfscCode());
        existingBankDetails.setBankName(dto.getBankName());
        existingBankDetails.setBranchName(dto.getBranchName());
        existingBankDetails.setAccountType(dto.getAccountType() != null ? dto.getAccountType() : existingBankDetails.getAccountType());
        existingBankDetails.setMobileNumber(dto.getMobileNumber());
        existingBankDetails.setEmailId(dto.getEmailId());

        // Reset verification status when details are updated
        existingBankDetails.setIsVerified(false);
        existingBankDetails.setVerificationStatus(BankDetails.VerificationStatus.PENDING);
        existingBankDetails.setLastVerified(null);

        BankDetails updatedBankDetails = bankDetailsRepository.save(existingBankDetails);
        return BankDetailsResponseDTO.fromEntity(updatedBankDetails);
    }

    @Transactional
    public boolean deleteBankDetails(Long serviceProviderId) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }

        // Validate service provider exists
        validateServiceProviderExists(serviceProviderId);

        BankDetails bankDetails = bankDetailsRepository.findByServiceProviderId(serviceProviderId)
                .orElseThrow(() -> new BadRequestException("Bank details not found for service provider with id: " + serviceProviderId));

        bankDetailsRepository.delete(bankDetails);
        return true;
    }

    @Transactional
    public BankDetailsResponseDTO updateVerificationStatus(Long serviceProviderId, BankDetails.VerificationStatus status) {
        if (serviceProviderId == null || serviceProviderId <= 0) {
            throw new BadRequestException("Invalid service provider ID");
        }
        if (status == null) {
            throw new BadRequestException("Verification status is required");
        }

        // Validate service provider exists
        validateServiceProviderExists(serviceProviderId);

        BankDetails bankDetails = bankDetailsRepository.findByServiceProviderId(serviceProviderId)
                .orElseThrow(() -> new BadRequestException("Bank details not found for service provider with id: " + serviceProviderId));

        bankDetails.setVerificationStatus(status);
        bankDetails.setIsVerified(status == BankDetails.VerificationStatus.VERIFIED);

        if (status == BankDetails.VerificationStatus.VERIFIED) {
            bankDetails.setLastVerified(LocalDateTime.now());
        } else {
            bankDetails.setLastVerified(null);
        }

        BankDetails updatedBankDetails = bankDetailsRepository.save(bankDetails);
        return BankDetailsResponseDTO.fromEntity(updatedBankDetails);
    }

    private void validateCreateBankDetailsDTO(CreateBankDetailsDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Bank details data cannot be null");
        }
        if (dto.getServiceProviderId() == null || dto.getServiceProviderId() <= 0) {
            throw new BadRequestException("Service Provider ID is required and must be positive");
        }
        if (dto.getAccountHolderName() == null || dto.getAccountHolderName().trim().isEmpty()) {
            throw new BadRequestException("Account holder name is required");
        }
        if (dto.getAccountHolderName().length() > 200) {
            throw new BadRequestException("Account holder name cannot exceed 200 characters");
        }
        if (dto.getAccountNumber() == null || dto.getAccountNumber().trim().isEmpty()) {
            throw new BadRequestException("Account number is required");
        }
        if (dto.getAccountNumber().length() < 9 || dto.getAccountNumber().length() > 18) {
            throw new BadRequestException("Account number must be between 9-18 digits");
        }
        if (!dto.getAccountNumber().matches("\\d+")) {
            throw new BadRequestException("Account number must contain only digits");
        }
        if (dto.getIfscCode() == null || dto.getIfscCode().trim().isEmpty()) {
            throw new BadRequestException("IFSC code is required");
        }
        if (!dto.getIfscCode().matches("^[A-Z]{4}0[A-Z0-9]{6}$")) {
            throw new BadRequestException("Invalid IFSC code format");
        }
        if (dto.getBankName() == null || dto.getBankName().trim().isEmpty()) {
            throw new BadRequestException("Bank name is required");
        }
        if (dto.getBankName().length() > 100) {
            throw new BadRequestException("Bank name cannot exceed 100 characters");
        }
        if (dto.getBranchName() == null || dto.getBranchName().trim().isEmpty()) {
            throw new BadRequestException("Branch name is required");
        }
        if (dto.getBranchName().length() > 100) {
            throw new BadRequestException("Branch name cannot exceed 100 characters");
        }
        if (dto.getMobileNumber() == null || dto.getMobileNumber().trim().isEmpty()) {
            throw new BadRequestException("Mobile number is required");
        }
        if (!dto.getMobileNumber().matches("^[+]?[1-9][\\d\\s\\-()]{8,15}$")) {
            throw new BadRequestException("Invalid mobile number format");
        }
        if (dto.getEmailId() == null || dto.getEmailId().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        if (!dto.getEmailId().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new BadRequestException("Invalid email format");
        }
        if (dto.getEmailId().length() > 100) {
            throw new BadRequestException("Email cannot exceed 100 characters");
        }
    }
}
