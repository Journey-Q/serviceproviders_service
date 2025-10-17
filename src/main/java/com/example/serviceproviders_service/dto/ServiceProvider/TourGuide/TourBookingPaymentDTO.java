package com.example.serviceproviders_service.dto.ServiceProvider.TourGuide;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourBookingPaymentDTO {
    private CreateTourBookingDTO tourBookingDetails;
    private String successUrl;
    private String cancelUrl;

    public TourBookingPaymentDTO() {}
}