
// dto/AuthResponse.java
package com.example.serviceproviders_service.dto;

public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private String serviceType;
    private boolean isApproved;
    private String message;

    // Constructor
    public AuthResponse(String token, String username, String email, String serviceType, boolean isApproved, String message) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.serviceType = serviceType;
        this.isApproved = isApproved;
        this.message = message;
    }

    // Getter methods
    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getServiceType() {
        return serviceType;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public String getMessage() {
        return message;
    }

    // Setter methods
    public void setToken(String token) {
        this.token = token;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
