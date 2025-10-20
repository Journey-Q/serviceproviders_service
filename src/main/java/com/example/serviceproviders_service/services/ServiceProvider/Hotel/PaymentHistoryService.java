package com.example.serviceproviders_service.services.ServiceProvider.Hotel;

import com.example.serviceproviders_service.dto.ServiceProvider.Hotel.PaymentHistoryResponseDTO;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.PaymentHistory;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.Room;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.RoomBooking;
import com.example.serviceproviders_service.repository.ServiceProvider.Hotel.PaymentHistoryRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.Hotel.RoomBookingRepository;
import com.example.serviceproviders_service.repository.ServiceProvider.Hotel.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final RoomBookingRepository roomBookingRepository;
    private final RoomRepository roomRepository;

    /**
     * Get all completed payments for a service provider
     */
    public List<PaymentHistoryResponseDTO> getAllCompletedPayments(Long serviceProviderId) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByServiceProviderIdAndStatus(
                serviceProviderId, PaymentHistory.PaymentStatus.COMPLETED
        );
        return payments.stream()
                .map(PaymentHistoryResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get completed payments filtered by month and year
     */
    public List<PaymentHistoryResponseDTO> getPaymentsByMonthAndYear(
            Long serviceProviderId, Integer month, Integer year) {

        List<PaymentHistory> payments;

        if (month == null || month == 0) {
            // Get all payments for the year
            payments = paymentHistoryRepository.findCompletedPaymentsByServiceProviderIdAndYear(
                    serviceProviderId, year
            );
        } else {
            // Get payments for specific month and year
            payments = paymentHistoryRepository.findCompletedPaymentsByServiceProviderIdAndYearAndMonth(
                    serviceProviderId, year, month
            );
        }

        return payments.stream()
                .map(PaymentHistoryResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get payment by ID
     */
    public PaymentHistoryResponseDTO getPaymentById(Long id) {
        PaymentHistory payment = paymentHistoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + id));
        return new PaymentHistoryResponseDTO(payment);
    }

    /**
     * Get payment by payment ID
     */
    public PaymentHistoryResponseDTO getPaymentByPaymentId(String paymentId) {
        PaymentHistory payment = paymentHistoryRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with payment ID: " + paymentId));
        return new PaymentHistoryResponseDTO(payment);
    }

    /**
     * Create payment history from a confirmed booking
     * This is called when a booking is confirmed/paid
     */
    @Transactional
    public PaymentHistory createPaymentHistoryFromBooking(RoomBooking booking) {
        // Check if payment history already exists for this booking
        if (paymentHistoryRepository.findByBookingId(booking.getId()).isPresent()) {
            throw new IllegalStateException("Payment history already exists for booking ID: " + booking.getId());
        }

        // Get room details
        Room room = roomRepository.findById(booking.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + booking.getRoomId()));

        PaymentHistory payment = new PaymentHistory();

        // Generate unique payment ID
        payment.setPaymentId(generatePaymentId());
        payment.setBookingId(booking.getId());
        payment.setServiceProviderId(booking.getServiceProviderId());
        payment.setRoomId(booking.getRoomId());

        // Guest information
        payment.setGuestName(booking.getCustomerName());
        payment.setGuestEmail(booking.getCustomerEmail());
        payment.setGuestPhone(booking.getCustomerPhone());
        payment.setGuestAddress(booking.getBillingAddress());

        // Room information
        payment.setRoomName(room.getName());

        // Payment details
        payment.setAmount(booking.getTotalAmount());

        // Determine payment method from card details
        String cardNumber = booking.getCardNumber();
        String paymentMethod = determineCardType(cardNumber);
        payment.setPaymentMethod(paymentMethod);

        // Store last 4 digits of card
        if (cardNumber != null && cardNumber.length() >= 4) {
            payment.setCardLastFour(cardNumber.substring(cardNumber.length() - 4));
        }

        // Generate transaction ID and invoice number
        payment.setTransactionId(generateTransactionId());
        payment.setInvoiceNumber(generateInvoiceNumber());

        // Stay details
        payment.setCheckInDate(booking.getCheckInDate());
        payment.setCheckOutDate(booking.getCheckOutDate());
        payment.setNumberOfNights(booking.getNumberOfNights());

        // Status
        payment.setStatus(PaymentHistory.PaymentStatus.COMPLETED);
        payment.setPaymentDate(booking.getConfirmedAt() != null ? booking.getConfirmedAt() : LocalDateTime.now());

        return paymentHistoryRepository.save(payment);
    }

    /**
     * Generate unique payment ID in format: PMT-YYYY-MMDD-XXX
     */
    private String generatePaymentId() {
        LocalDate now = LocalDate.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyy-MMdd"));

        // Get count of payments today to generate sequential number
        LocalDateTime startOfDay = now.atStartOfDay();
        LocalDateTime endOfDay = now.atTime(LocalTime.MAX);

        long countToday = paymentHistoryRepository.findByServiceProviderIdAndDateRange(
                -1L, startOfDay, endOfDay
        ).size();

        String sequentialNumber = String.format("%03d", countToday + 1);
        return "PMT-" + dateStr + "-" + sequentialNumber;
    }

    /**
     * Generate transaction ID in format: TXN-XXXXXXXXX
     */
    private String generateTransactionId() {
        long timestamp = System.currentTimeMillis();
        return "TXN-" + String.valueOf(timestamp).substring(3);
    }

    /**
     * Generate invoice number in format: #INV-YYYY-MMDD-XXX
     */
    private String generateInvoiceNumber() {
        LocalDate now = LocalDate.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyy-MMdd"));

        LocalDateTime startOfDay = now.atStartOfDay();
        LocalDateTime endOfDay = now.atTime(LocalTime.MAX);

        long countToday = paymentHistoryRepository.findByServiceProviderIdAndDateRange(
                -1L, startOfDay, endOfDay
        ).size();

        String sequentialNumber = String.format("%03d", countToday + 1);
        return "#INV-" + dateStr + "-" + sequentialNumber;
    }

    /**
     * Determine card type from card number
     */
    private String determineCardType(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return "Credit Card";
        }

        // Remove spaces and dashes
        cardNumber = cardNumber.replaceAll("[\\s-]", "");

        if (cardNumber.startsWith("4")) {
            return "Credit Card (VISA)";
        } else if (cardNumber.startsWith("5")) {
            return "Credit Card (MasterCard)";
        } else if (cardNumber.startsWith("3")) {
            return "Credit Card (AMEX)";
        } else {
            return "Credit Card";
        }
    }

    /**
     * Get all payments for a specific room
     */
    public List<PaymentHistoryResponseDTO> getPaymentsByRoom(Long roomId) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByRoomId(roomId);
        return payments.stream()
                .map(PaymentHistoryResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all payments by guest email
     */
    public List<PaymentHistoryResponseDTO> getPaymentsByGuestEmail(String email) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByGuestEmail(email);
        return payments.stream()
                .map(PaymentHistoryResponseDTO::new)
                .collect(Collectors.toList());
    }
}