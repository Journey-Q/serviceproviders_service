// dto/AuthResponse.java
package com.example.serviceproviders_service.dto.ServiceProvider;

import com.example.serviceproviders_service.entity.serviceProvider.ServiceProvider;

public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private ServiceProvider serviceProvider;

    // Constructor with all parameters
    public AuthResponse(String accessToken, String refreshToken, Long expiresIn, ServiceProvider serviceProvider) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.serviceProvider = serviceProvider;
    }

    // Default constructor
    public AuthResponse() {
    }

    // Getter methods
    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    // Setter methods
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }
}
