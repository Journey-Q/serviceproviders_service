// Embedded Classes
package com.example.serviceproviders_service.entity.serviceProvider.TravelAgency;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ContactInfo {
    private String phone;
    private String address;
    private String email;

    public ContactInfo() {
    }

    public ContactInfo(String phone, String address, String email) {
        this.phone = phone;
        this.address = address;
        this.email = email;
    }
}