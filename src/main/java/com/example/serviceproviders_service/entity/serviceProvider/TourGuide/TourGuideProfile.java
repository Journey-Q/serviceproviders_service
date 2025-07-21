package com.example.serviceproviders_service.entity.serviceProvider.TourGuide;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tour_guide_profiles")
@Getter
@Setter
public class TourGuideProfile {

    @Id
    private Long serviceProviderId;

    @Column(nullable = false, length = 100)
    private String companyName;

    @Column(name = "profile_photo_url", length = 255)
    private String profilePhotoUrl; // Store URL or file path

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "established_year")
    private Integer establishedYear;

    @Column(name = "number_of_guides")
    private Integer numberOfGuides;

    @Embedded
    private ContactInfo contactInfo;

    // Other fields as needed
}