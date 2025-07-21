package com.example.serviceproviders_service.entity.serviceProvider.Hotel;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ContactInfo {
    private String phone;
    private String email;

    // Constructors, getters, and setters
    public ContactInfo() {
    }

    public ContactInfo(String phone, String email) {
        this.phone = phone;
        this.email = email;
    }

    // Getters and setters
    // ...
}