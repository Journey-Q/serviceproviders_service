// controller/AuthController.java
package com.example.serviceproviders_service.controller;

import com.example.serviceproviders_service.dto.ApiError;
import com.example.serviceproviders_service.dto.AuthResponse;
import com.example.serviceproviders_service.dto.ServiceProviderLoginRequest;
import com.example.serviceproviders_service.dto.ServiceProviderSignupRequest;
import com.example.serviceproviders_service.entity.ServiceProvider;
import com.example.serviceproviders_service.services.ServiceProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final ServiceProviderService serviceProviderService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody ServiceProviderSignupRequest request) {
        try {
            // Basic validation
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Username is required", HttpStatus.BAD_REQUEST.value()));
            }

            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Email is required", HttpStatus.BAD_REQUEST.value()));
            }

            if (request.getPassword() == null || request.getPassword().length() < 6) {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Password must be at least 6 characters", HttpStatus.BAD_REQUEST.value()));
            }

            if (request.getServiceType() == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Service type is required", HttpStatus.BAD_REQUEST.value()));
            }

            if (request.getBusinessRegistrationNumber() == null || request.getBusinessRegistrationNumber().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Business registration number is required", HttpStatus.BAD_REQUEST.value()));
            }

            AuthResponse response = serviceProviderService.signup(request);

            if (response.getToken() != null) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiError(response.getMessage(), HttpStatus.BAD_REQUEST.value()));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Registration failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody ServiceProviderLoginRequest request) {
        try {
            // Basic validation
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Username is required", HttpStatus.BAD_REQUEST.value()));
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Password is required", HttpStatus.BAD_REQUEST.value()));
            }

            AuthResponse response = serviceProviderService.login(request);

            if (response.getToken() != null) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiError(response.getMessage(), HttpStatus.UNAUTHORIZED.value()));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Login failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            ServiceProvider serviceProvider = (ServiceProvider) authentication.getPrincipal();
            return ResponseEntity.ok(serviceProvider);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError("Unauthorized", HttpStatus.UNAUTHORIZED.value()));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Service Provider Auth API is working!");
    }
}