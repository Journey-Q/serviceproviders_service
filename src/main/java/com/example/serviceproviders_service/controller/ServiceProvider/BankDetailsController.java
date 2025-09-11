package com.example.serviceproviders_service.controller.ServiceProvider;

import com.example.serviceproviders_service.dto.ServiceProvider.CreateBankDetailsDTO;
import com.example.serviceproviders_service.dto.ServiceProvider.BankDetailsResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.BankDetails;
import com.example.serviceproviders_service.services.ServiceProvider.BankDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service/bank-details")
@CrossOrigin(origins = "*")
public class BankDetailsController {

    private final BankDetailsService bankDetailsService;

    public BankDetailsController(BankDetailsService bankDetailsService) {
        this.bankDetailsService = bankDetailsService;
    }

    @PostMapping("/create")
    public ResponseEntity<BankDetailsResponseDTO> createBankDetails(@RequestBody CreateBankDetailsDTO dto) {
        BankDetailsResponseDTO createdBankDetails = bankDetailsService.createBankDetails(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBankDetails);
    }

    @GetMapping("/service-provider/{serviceProviderId}")
    public ResponseEntity<BankDetailsResponseDTO> getBankDetailsByServiceProviderId(@PathVariable Long serviceProviderId) {
        BankDetailsResponseDTO bankDetails = bankDetailsService.getBankDetailsByServiceProviderId(serviceProviderId);
        return ResponseEntity.ok(bankDetails);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankDetailsResponseDTO> getBankDetailsById(@PathVariable Long id) {
        BankDetailsResponseDTO bankDetails = bankDetailsService.getBankDetailsById(id);
        return ResponseEntity.ok(bankDetails);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BankDetailsResponseDTO>> getAllBankDetails() {
        List<BankDetailsResponseDTO> bankDetailsList = bankDetailsService.getAllBankDetails();
        return ResponseEntity.ok(bankDetailsList);
    }

    @GetMapping("/verification-status/{status}")
    public ResponseEntity<List<BankDetailsResponseDTO>> getBankDetailsByVerificationStatus(@PathVariable BankDetails.VerificationStatus status) {
        List<BankDetailsResponseDTO> bankDetailsList = bankDetailsService.getBankDetailsByVerificationStatus(status);
        return ResponseEntity.ok(bankDetailsList);
    }

    @PutMapping("/service-provider/{serviceProviderId}")
    public ResponseEntity<BankDetailsResponseDTO> updateBankDetails(
            @PathVariable Long serviceProviderId,
            @RequestBody CreateBankDetailsDTO dto) {
        BankDetailsResponseDTO updatedBankDetails = bankDetailsService.updateBankDetails(serviceProviderId, dto);
        return ResponseEntity.ok(updatedBankDetails);
    }

    @PatchMapping("/service-provider/{serviceProviderId}/verification-status")
    public ResponseEntity<BankDetailsResponseDTO> updateVerificationStatus(
            @PathVariable Long serviceProviderId,
            @RequestParam BankDetails.VerificationStatus status) {
        BankDetailsResponseDTO updatedBankDetails = bankDetailsService.updateVerificationStatus(serviceProviderId, status);
        return ResponseEntity.ok(updatedBankDetails);
    }

    @DeleteMapping("/service-provider/{serviceProviderId}")
    public ResponseEntity<String> deleteBankDetails(@PathVariable Long serviceProviderId) {
        boolean response = bankDetailsService.deleteBankDetails(serviceProviderId);
        if (response) {
            return ResponseEntity.ok("Bank details deleted successfully");
        }
        return ResponseEntity.noContent().build();
    }
}