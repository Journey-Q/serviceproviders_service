package com.example.serviceproviders_service.dto.ServiceProvider.TravelAgency;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleBookingPaymentDTO {
    private CreateVehicleBookingDTO vehicleBookingDetails;
    private String successUrl;
    private String cancelUrl;

    public VehicleBookingPaymentDTO() {}
}
