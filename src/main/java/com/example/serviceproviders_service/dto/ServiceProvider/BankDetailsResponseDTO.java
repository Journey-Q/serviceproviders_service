package com.example.serviceproviders_service.dto.ServiceProvider;

import com.example.serviceproviders_service.entity.serviceProvider.BankDetails;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BankDetailsResponseDTO {
    private Long id;
    private Long serviceProviderId;
    private String accountHolderName;
    private String accountNumber;
    private String ifscCode;
    private String bankName;
    private String branchName;
    private BankDetails.AccountType accountType;
    private String mobileNumber;
    private String emailId;
    private Boolean isVerified;
    private BankDetails.VerificationStatus verificationStatus;
    private LocalDateTime lastVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BankDetailsResponseDTO() {}

    public static BankDetailsResponseDTO fromEntity(BankDetails bankDetails) {
        BankDetailsResponseDTO dto = new BankDetailsResponseDTO();
        dto.setId(bankDetails.getId());
        dto.setServiceProviderId(bankDetails.getServiceProviderId());
        dto.setAccountHolderName(bankDetails.getAccountHolderName());
        dto.setAccountNumber(bankDetails.getAccountNumber());
        dto.setIfscCode(bankDetails.getIfscCode());
        dto.setBankName(bankDetails.getBankName());
        dto.setBranchName(bankDetails.getBranchName());
        dto.setAccountType(bankDetails.getAccountType());
        dto.setMobileNumber(bankDetails.getMobileNumber());
        dto.setEmailId(bankDetails.getEmailId());
        dto.setIsVerified(bankDetails.getIsVerified());
        dto.setVerificationStatus(bankDetails.getVerificationStatus());
        dto.setLastVerified(bankDetails.getLastVerified());
        dto.setCreatedAt(bankDetails.getCreatedAt());
        dto.setUpdatedAt(bankDetails.getUpdatedAt());
        return dto;
    }
}
