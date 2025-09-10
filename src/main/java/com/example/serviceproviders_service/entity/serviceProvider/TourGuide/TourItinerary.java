package com.example.serviceproviders_service.entity.serviceProvider.TourGuide;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tour_itinerary")
@Getter
@Setter
public class TourItinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tourId;

    @Column(nullable = false, length = 20)
    private String time;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String activity;

    @Column(nullable = false)
    private Integer orderIndex;

    public TourItinerary() {}

    public TourItinerary(Long tourId, String time, String activity, Integer orderIndex) {
        this.tourId = tourId;
        this.time = time;
        this.activity = activity;
        this.orderIndex = orderIndex;
    }
}
