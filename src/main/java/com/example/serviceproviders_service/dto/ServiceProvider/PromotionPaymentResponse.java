package com.example.serviceproviders_service.dto.ServiceProvider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for promotion payment response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionPaymentResponse {

    private Long promotionId;
    private String transactionId;
    private String paymentReferenceNumber;
    private BigDecimal amount;
    private String plan;
    private String duration;
    private LocalDateTime paymentDate;
    private String status; // SUCCESS, FAILED
    private String message;

    // Card details (masked)
    private String cardLastFourDigits;
    private String cardholderName;
}