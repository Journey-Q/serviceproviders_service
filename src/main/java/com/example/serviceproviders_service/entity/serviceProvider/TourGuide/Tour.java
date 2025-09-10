package com.example.serviceproviders_service.entity.serviceProvider.TourGuide;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tours")
@Getter
@Setter
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long serviceProviderId;

    @Column(nullable = false, length = 200)
    private String name;

    private String image;

    @Column(nullable = false)
    private BigDecimal originalPrice;

    @Column(nullable = false)
    private Integer discount = 0; // Percentage discount

    @Column(nullable = false)
    private BigDecimal finalPrice;

    @Column(nullable = false)
    private BigDecimal pricePerPerson;

    @Column(nullable = false, length = 50)
    private String duration;

    @ElementCollection
    @CollectionTable(name = "tour_places", joinColumns = @JoinColumn(name = "tour_id"))
    @Column(name = "place")
    private List<String> places;

    @ElementCollection
    @CollectionTable(name = "tour_highlights", joinColumns = @JoinColumn(name = "tour_id"))
    @Column(name = "highlight")
    private List<String> highlights;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourStatus status = TourStatus.AVAILABLE;

    @Column(nullable = false)
    private BigDecimal rating = BigDecimal.valueOf(4.0);

    @Column(nullable = false)
    private Integer maxPeople;

    @Column(nullable = false)
    private Integer minPeople;

    @Column(columnDefinition = "TEXT")
    private String aboutTour;

    @ElementCollection
    @CollectionTable(name = "tour_included_items", joinColumns = @JoinColumn(name = "tour_id"))
    @Column(name = "included_item")
    private List<String> included;

    @ElementCollection
    @CollectionTable(name = "tour_important_notes", joinColumns = @JoinColumn(name = "tour_id"))
    @Column(name = "note")
    private List<String> importantNotes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateFinalPrice();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateFinalPrice();
    }

    private void calculateFinalPrice() {
        if (originalPrice != null && discount != null) {
            BigDecimal discountAmount = originalPrice.multiply(BigDecimal.valueOf(discount))
                    .divide(BigDecimal.valueOf(100));
            this.finalPrice = originalPrice.subtract(discountAmount);
            this.pricePerPerson = this.finalPrice;
        }
    }

    public enum TourStatus {
        AVAILABLE,
        UNAVAILABLE
    }

    public Tour() {}
}
