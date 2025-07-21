// services/ServiceProviderService.java (with debug logging)
package com.example.serviceproviders_service.services.ServiceProvider;

import com.example.serviceproviders_service.config.AppConfig;
import com.example.serviceproviders_service.dto.ServiceProvider.AuthResponse;
import com.example.serviceproviders_service.dto.ServiceProvider.ServiceProviderLoginRequest;
import com.example.serviceproviders_service.dto.ServiceProvider.ServiceProviderSignupRequest;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProvider;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProviderPrincipal;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.repository.ServiceProvider.ServiceProviderRepo;
import com.example.serviceproviders_service.services.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceProviderService {

    @Autowired
    private final AppConfig appConfig;

    private final ServiceProviderRepo serviceProviderRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    @Lazy
    private final AuthenticationManager authenticationManager;

    public AuthResponse signup(ServiceProviderSignupRequest request) {
        // Check if username already exists
        if (serviceProviderRepo.existsByUsername(request.getUsername())) {
            return null; // Will be handled as error in controller
        }

        // Check if email already exists
        if (serviceProviderRepo.existsByEmail(request.getEmail())) {
            return null; // Will be handled as error in controller
        }

        // Check if business registration number already exists
        if (serviceProviderRepo.existsByBusinessRegistrationNumber(request.getBusinessRegistrationNumber())) {
            return null; // Will be handled as error in controller
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
        ServiceProviderPrincipal principal = new ServiceProviderPrincipal(serviceProvider);
        String jwtToken = jwtService.generateToken(principal);

        return new AuthResponse(
                jwtToken,
                null, // refreshToken not implemented yet
                jwtService.getExpirationTime(),
                serviceProvider
        );
    }

    public AuthResponse verify(ServiceProviderLoginRequest req) {
        try {
            // Check if email exists first
            boolean emailExists = serviceProviderRepo.existsByEmail(req.getEmail());
            if (!emailExists) {
                throw new BadRequestException("Email does not exist");
            }

            // Get the service provider to check approval status
            ServiceProvider serviceProvider = serviceProviderRepo.findByEmail(req.getEmail())
                    .orElseThrow(() -> new BadRequestException("Email does not exist"));

            // Check if service provider is approved
            if (serviceProvider.getIsApproved() == null || !serviceProvider.getIsApproved()) {
                throw new BadRequestException("Waiting for admin approval");
            }

            // Check if service provider is active
            if (!serviceProvider.getIsActive()) {
                throw new BadRequestException("Account is deactivated");
            }

            // Email exists and user is approved, now authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            ServiceProviderPrincipal userPrincipal = (ServiceProviderPrincipal) authentication.getPrincipal();
            ServiceProvider user = userPrincipal.getServiceProvider();
            String token = jwtService.generateToken(userPrincipal);

            AuthResponse authResponse = new AuthResponse();
            authResponse.setServiceProvider(user);
            authResponse.setAccessToken(token);
            authResponse.setExpiresIn(jwtService.getExpirationTime());

            return authResponse;

        } catch (BadRequestException e) {
            throw e; // Re-throw custom errors (email not found, not approved, etc.)
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect password");
        } catch (UsernameNotFoundException e) {
            // This shouldn't happen since we check email existence first
            throw new BadRequestException("Email does not exist");
        } catch (Exception e) {
            throw new RuntimeException("Authentication service unavailable", e);
        }
    }
    public ServiceProvider findByUsername(String username) {
        return serviceProviderRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Service provider not found"));
    }

    public ServiceProvider findByEmail(String email) {
        return serviceProviderRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Service provider not found"));
    }

    // Helper methods for controller
    public boolean existsByUsername(String username) {
        return serviceProviderRepo.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return serviceProviderRepo.existsByEmail(email);
    }

    public boolean existsByBusinessRegistrationNumber(String businessRegistrationNumber) {
        return serviceProviderRepo.existsByBusinessRegistrationNumber(businessRegistrationNumber);
    }
}