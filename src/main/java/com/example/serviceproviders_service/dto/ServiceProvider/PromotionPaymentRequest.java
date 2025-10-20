package com.example.serviceproviders_service.dto.ServiceProvider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for processing promotion payment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionPaymentRequest {

    private Long promotionId;

    // Pricing plan selection
    private String plan; // basic, premium, featured

    // Payment card details
    private String cardNumber; // Will be processed and only last 4 digits stored
    private String expiryDate; // MM/YY format
    private String cvv; // Not stored, only validated
    private String cardholderName;

    // Billing address
    private String billingAddress;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    // Amount (calculated from plan)
    private BigDecimal amount;
}