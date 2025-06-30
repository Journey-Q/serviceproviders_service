
// dto/ServiceProviderLoginRequest.java
package com.example.serviceproviders_service.dto;

import jakarta.validation.constraints.NotBlank;

public class ServiceProviderLoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    // Getter methods
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Setter methods
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
