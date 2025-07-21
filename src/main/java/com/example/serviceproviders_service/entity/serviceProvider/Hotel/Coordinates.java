package com.example.serviceproviders_service.entity.serviceProvider.Hotel;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Coordinates {
    private Double lat;
    private Double lng;

    // Constructors, getters, and setters
    public Coordinates() {
    }

    public Coordinates(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    // Getters and setters
    // ...
}