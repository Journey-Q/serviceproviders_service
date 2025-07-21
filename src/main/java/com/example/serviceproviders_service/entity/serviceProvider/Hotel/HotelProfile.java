package com.example.serviceproviders_service.entity.serviceProvider.Hotel;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "hotel_profiles")
@Getter
@Setter
public class HotelProfile {

    @Id
    private Long serviceProviderId;

    @Column(nullable = false)
    private String hotelName;

    @Column(nullable = false)
    private String location;

    @Embedded
    private Coordinates coordinates;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String hotelPhoto; // This could store URL or file path

    @ElementCollection
    @CollectionTable(name = "hotel_amenities", joinColumns = @JoinColumn(name = "hotel_id"))
    @Column(name = "amenity")
    private List<String> amenities;

    @Embedded
    private ContactInfo contactInfo;

    // Constructors, getters, and setters
    public HotelProfile() {
    }

    // Getters and setters for all fields
    // ...

}