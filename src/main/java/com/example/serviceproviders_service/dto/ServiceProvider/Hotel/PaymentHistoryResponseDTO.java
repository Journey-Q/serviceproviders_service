package com.example.serviceproviders_service.dto.ServiceProvider.Hotel;

import com.example.serviceproviders_service.entity.serviceProvider.Hotel.PaymentHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryResponseDTO {

    private String id;
    private String paymentId;
    private String bookingId;

    // Guest Details
    private GuestDetails guest;

    // Payment Details
    private String date;
    private String time;
    private Integer amount; // Amount in cents
    private String method;
    private String status;
    private String invoice;
    private String cardLastFour;
    private String bankReference;
    private String transactionId;

    // Stay Details
    private String checkIn;
    private String checkOut;
    private Integer nights;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuestDetails {
        private String name;
        private String room;
        private String email;
        private String phone;
        private String address;
    }

    // Constructor from PaymentHistory entity
    public PaymentHistoryResponseDTO(PaymentHistory payment) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

        this.id = payment.getId() != null ? payment.getId().toString() : null;
        this.paymentId = payment.getPaymentId();
        this.bookingId = payment.getBookingId() != null ? "BK-" + String.format("%04d", payment.getBookingId()) : null;

        // Guest Details
        this.guest = new GuestDetails(
            payment.getGuestName(),
            payment.getRoomName(),
            payment.getGuestEmail(),
            payment.getGuestPhone(),
            payment.getGuestAddress()
        );

        // Payment Details
        LocalDateTime paymentDateTime = payment.getPaymentDate();
        this.date = paymentDateTime != null ? paymentDateTime.format(dateFormatter) : null;
        this.time = paymentDateTime != null ? paymentDateTime.format(timeFormatter) : null;

        // Convert amount from BigDecimal to cents (integer)
        this.amount = payment.getAmount() != null ? payment.getAmount().multiply(BigDecimal.valueOf(100)).intValue() : 0;

        this.method = payment.getPaymentMethod();
        this.status = payment.getStatus() != null ? payment.getStatus().name().toLowerCase() : "completed";
        this.invoice = payment.getInvoiceNumber();
        this.cardLastFour = payment.getCardLastFour();
        this.bankReference = payment.getBankReference();
        this.transactionId = payment.getTransactionId();

        // Stay Details
        this.checkIn = payment.getCheckInDate() != null ? payment.getCheckInDate().format(dateFormatter) : null;
        this.checkOut = payment.getCheckOutDate() != null ? payment.getCheckOutDate().format(dateFormatter) : null;
        this.nights = payment.getNumberOfNights();
    }
}