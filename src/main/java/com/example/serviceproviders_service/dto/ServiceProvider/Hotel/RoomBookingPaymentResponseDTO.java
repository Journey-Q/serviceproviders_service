package com.example.serviceproviders_service.dto.ServiceProvider.Hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomBookingPaymentResponseDTO {
    private String status;
    private String message;
    private String sessionId;
    private String sessionUrl;
    private Long roomBookingId;
    private String bookingReference;
    private String currency;
    private String totalAmount;
    private String paymentStatus;
    private String errorCode;
    private String errorMessage;
}