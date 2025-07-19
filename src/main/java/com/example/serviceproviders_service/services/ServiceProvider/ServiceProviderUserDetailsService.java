// services/ServiceProviderUserDetailsService.java
package com.example.serviceproviders_service.services.ServiceProvider;

import com.example.serviceproviders_service.entity.Admin.Admin;
import com.example.serviceproviders_service.entity.Admin.AdminPrincipal;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProvider;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProviderPrincipal;
import com.example.serviceproviders_service.repository.Admin.AdminRepo;
import com.example.serviceproviders_service.repository.ServiceProvider.ServiceProviderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceProviderUserDetailsService implements UserDetailsService {

    private final ServiceProviderRepo serviceProviderRepo;
    private final AdminRepo adminRepo;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Try to find admin by email first
        Admin admin = adminRepo.findByEmail(identifier).orElse(null);
        if (admin != null) {
            return new AdminPrincipal(admin);
        }

        // Try to find service provider by email
        ServiceProvider serviceProvider = serviceProviderRepo.findByEmail(identifier).orElse(null);
        if (serviceProvider != null) {
            return new ServiceProviderPrincipal(serviceProvider);
        }

        // Try to find service provider by username (for JWT token validation)
        serviceProvider = serviceProviderRepo.findByUsername(identifier).orElse(null);
        if (serviceProvider != null) {
            return new ServiceProviderPrincipal(serviceProvider);
        }

        // Try to find admin by username (for JWT token validation)
        admin = adminRepo.findByUsername(identifier).orElse(null);
        if (admin != null) {
            return new AdminPrincipal(admin);
        }

        throw new UsernameNotFoundException("User not found with identifier: " + identifier);
    }

    // Helper method to load service provider by email (for login)
    public UserDetails loadServiceProviderByEmail(String email) throws UsernameNotFoundException {
        ServiceProvider serviceProvider = serviceProviderRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Service provider not found with email: " + email));

        return new ServiceProviderPrincipal(serviceProvider);
    }

    // Helper method to load admin by email (for login)
    public UserDetails loadAdminByEmail(String email) throws UsernameNotFoundException {
        Admin admin = adminRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with email: " + email));

        return new AdminPrincipal(admin);
    }

    // Helper method to load by username (for JWT filter)
    public UserDetails loadUserByUsernameField(String username) throws UsernameNotFoundException {
        // Try service provider first
        ServiceProvider serviceProvider = serviceProviderRepo.findByUsername(username).orElse(null);
        if (serviceProvider != null) {
            return new ServiceProviderPrincipal(serviceProvider);
        }

        // Try admin
        Admin admin = adminRepo.findByUsername(username).orElse(null);
        if (admin != null) {
            return new AdminPrincipal(admin);
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}