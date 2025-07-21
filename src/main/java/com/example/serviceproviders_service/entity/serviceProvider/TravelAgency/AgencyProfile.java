// Entity
package com.example.serviceproviders_service.entity.serviceProvider.TravelAgency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "agency_profiles")
@Getter
@Setter
public class AgencyProfile {

    @Id
    private Long serviceProviderId;

    @Column(nullable = false)
    private String agencyName;

    private String profilePhoto;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Embedded
    private AgencyInfo agencyInfo;

    @Embedded
    private ContactInfo contactInfo;

    public AgencyProfile() {
    }
}