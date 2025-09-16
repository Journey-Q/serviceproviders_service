package com.example.serviceproviders_service.dto.ServiceProvider.Hotel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomBookingPaymentDTO {
    private CreateRoomBookingDTO roomBookingDetails;
    private String successUrl;
    private String cancelUrl;

    public RoomBookingPaymentDTO() {}
}