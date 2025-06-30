// entity/ServiceProviderPrincipal.java
package com.example.serviceproviders_service.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

public class ServiceProviderPrincipal implements UserDetails {

    private ServiceProvider serviceProvider;

    public ServiceProviderPrincipal(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert service provider type to Spring Security authority format
        String role = "ROLE_" + serviceProvider.getServiceType().name();
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return serviceProvider.getPassword();
    }

    @Override
    public String getUsername() {
        return serviceProvider.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        // Account never expires in your system
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Use the isActive field from your ServiceProvider entity
        return serviceProvider.getIsActive() != null ? serviceProvider.getIsActive() : false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Credentials (password) never expire in your system
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Service provider must be both active AND approved to be enabled
        boolean isActive = serviceProvider.getIsActive() != null ? serviceProvider.getIsActive() : false;
        boolean isApproved = serviceProvider.getIsApproved() != null ? serviceProvider.getIsApproved() : false;
        return isActive && isApproved;
    }

    // Additional methods to access the wrapped ServiceProvider entity
    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public Long getId() {
        return serviceProvider.getId();
    }

    public String getEmail() {
        return serviceProvider.getEmail();
    }

    public ServiceProviderType getServiceType() {
        return serviceProvider.getServiceType();
    }

    public String getBusinessRegistrationNumber() {
        return serviceProvider.getBusinessRegistrationNumber();
    }

    public String getAddress() {
        return serviceProvider.getAddress();
    }

    public String getContactNo() {
        return serviceProvider.getContactNo();
    }

    public Boolean getIsApproved() {
        return serviceProvider.getIsApproved();
    }

    public Boolean getIsActive() {
        return serviceProvider.getIsActive();
    }

    public LocalDateTime getCreatedAt() {
        return serviceProvider.getCreatedAt();
    }

    public String getVerificationCode() {
        return serviceProvider.getVerificationCode();
    }

    public LocalDateTime getVerificationExpiration() {
        return serviceProvider.getVerificationExpiration();
    }

    public boolean isActive() {
        return serviceProvider.getIsActive() != null ? serviceProvider.getIsActive() : false;
    }

    public boolean isApproved() {
        return serviceProvider.getIsApproved() != null ? serviceProvider.getIsApproved() : false;
    }
}