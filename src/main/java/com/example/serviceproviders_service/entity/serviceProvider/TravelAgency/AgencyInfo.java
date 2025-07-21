// Embedded Classes
package com.example.serviceproviders_service.entity.serviceProvider.TravelAgency;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class AgencyInfo {
    private String establishedYear;
    private String fleetSize;

    public AgencyInfo() {
    }

    public AgencyInfo(String establishedYear, String fleetSize) {
        this.establishedYear = establishedYear;
        this.fleetSize = fleetSize;
    }
}