// services/ServiceProviderService.java (with debug logging)
package com.example.serviceproviders_service.services;

import com.example.serviceproviders_service.dto.AuthResponse;
import com.example.serviceproviders_service.dto.ServiceProviderLoginRequest;
import com.example.serviceproviders_service.dto.ServiceProviderSignupRequest;
import com.example.serviceproviders_service.entity.ServiceProvider;
import com.example.serviceproviders_service.repository.ServiceProviderRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceProviderService {

    private final ServiceProviderRepo serviceProviderRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    @Lazy
    private final AuthenticationManager authenticationManager;

    public AuthResponse signup(ServiceProviderSignupRequest request) {
        // Check if username already exists
        if (serviceProviderRepo.existsByUsername(request.getUsername())) {
            return new AuthResponse(null, null, null, null, false, "Username already exists");
        }

        // Check if email already exists
        if (serviceProviderRepo.existsByEmail(request.getEmail())) {
            return new AuthResponse(null, null, null, null, false, "Email already exists");
        }

        // Check if business registration number already exists
        if (serviceProviderRepo.existsByBusinessRegistrationNumber(request.getBusinessRegistrationNumber())) {
            return new AuthResponse(null, null, null, null, false, "Business registration number already exists");
        }

        // Create new service provider
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        log.debug("Signup - Original password length: {}, Encoded password: {}",
                request.getPassword().length(), encodedPassword);

        ServiceProvider serviceProvider = new ServiceProvider(
                request.getUsername(),
                request.getEmail(),
                encodedPassword,
                request.getServiceType(),
                request.getBusinessRegistrationNumber(),
                request.getAddress(),
                request.getContactNo()
        );

        serviceProviderRepo.save(serviceProvider);
        log.info("Service provider saved successfully: {}", serviceProvider.getUsername());

        // Generate JWT token
        String jwtToken = jwtService.generateToken(serviceProvider);

        return new AuthResponse(
                jwtToken,
                serviceProvider.getUsername(),
                serviceProvider.getEmail(),
                serviceProvider.getServiceType().name(),
                serviceProvider.getIsApproved(),
                "Service provider registered successfully. Awaiting approval."
        );
    }

    public AuthResponse login(ServiceProviderLoginRequest request) {
        try {
            log.debug("Login attempt for username: {}", request.getUsername());

            // Check if user exists first
            ServiceProvider serviceProvider = serviceProviderRepo.findByUsername(request.getUsername())
                    .orElse(null);

            if (serviceProvider == null) {
                log.warn("User not found: {}", request.getUsername());
                return new AuthResponse(null, null, null, null, false, "Invalid username or password");
            }

            log.debug("User found: {}, Active: {}, Approved: {}",
                    serviceProvider.getUsername(),
                    serviceProvider.getIsActive(),
                    serviceProvider.getIsApproved());

            // Check if password matches
            boolean passwordMatches = passwordEncoder.matches(request.getPassword(), serviceProvider.getPassword());
            log.debug("Password matches: {}", passwordMatches);

            if (!passwordMatches) {
                log.warn("Password mismatch for user: {}", request.getUsername());
                return new AuthResponse(null, null, null, null, false, "Invalid username or password");
            }

            // Now try authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            if (!serviceProvider.getIsActive()) {
                return new AuthResponse(null, null, null, null, false, "Account is deactivated");
            }

            String jwtToken = jwtService.generateToken(serviceProvider);

            String message = serviceProvider.getIsApproved() ?
                    "Login successful" :
                    "Login successful. Account pending approval.";

            log.info("Login successful for user: {}", serviceProvider.getUsername());

            return new AuthResponse(
                    jwtToken,
                    serviceProvider.getUsername(),
                    serviceProvider.getEmail(),
                    serviceProvider.getServiceType().name(),
                    serviceProvider.getIsApproved(),
                    message
            );

        } catch (Exception e) {
            log.error("Login failed for user: {} - Error: {}", request.getUsername(), e.getMessage());
            return new AuthResponse(null, null, null, null, false, "Invalid username or password");
        }
    }

    public ServiceProvider findByUsername(String username) {
        return serviceProviderRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Service provider not found"));
    }
}