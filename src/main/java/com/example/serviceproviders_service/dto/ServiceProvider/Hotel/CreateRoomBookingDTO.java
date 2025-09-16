package com.example.serviceproviders_service.dto.ServiceProvider.Hotel;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateRoomBookingDTO {
    private Long roomId;
    private Long userId;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private String specialRequests;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private BigDecimal roomPricePerNight;
    private String currency;

    public CreateRoomBookingDTO() {}
}