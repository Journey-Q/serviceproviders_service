package com.example.serviceproviders_service.dto.ServiceProvider;

import com.example.serviceproviders_service.entity.serviceProvider.BankDetails;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBankDetailsDTO {
    private Long serviceProviderId;
    private String accountHolderName;
    private String accountNumber;
    private String ifscCode;
    private String bankName;
    private String branchName;
    private BankDetails.AccountType accountType;
    private String mobileNumber;
    private String emailId;

    public CreateBankDetailsDTO() {}
}

