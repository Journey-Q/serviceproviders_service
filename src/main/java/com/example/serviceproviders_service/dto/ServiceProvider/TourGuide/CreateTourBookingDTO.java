package com.example.serviceproviders_service.dto.ServiceProvider.TourGuide;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateTourBookingDTO {
    private Long tourId;
    private Long userId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String specialRequests;
    private LocalDate tourDate;
    private Integer numberOfPeople;
    private BigDecimal pricePerPerson;
    private String currency;

    public CreateTourBookingDTO() {}
}