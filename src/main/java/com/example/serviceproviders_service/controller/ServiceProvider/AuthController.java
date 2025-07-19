// controller/AuthController.java
package com.example.serviceproviders_service.controller.ServiceProvider;

import com.example.serviceproviders_service.dto.ApiError;
import com.example.serviceproviders_service.dto.ServiceProvider.AuthResponse;
import com.example.serviceproviders_service.dto.ServiceProvider.ServiceProviderLoginRequest;
import com.example.serviceproviders_service.dto.ServiceProvider.ServiceProviderSignupRequest;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProvider;
import com.example.serviceproviders_service.entity.serviceProvider.ServiceProviderPrincipal;
import com.example.serviceproviders_service.services.ServiceProvider.ServiceProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("service/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final ServiceProviderService serviceProviderService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody ServiceProviderSignupRequest request, BindingResult bindingResult) {
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    errors.put(error.getField(), error.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(errors);
            }

            // Check for existing username
            if (serviceProviderService.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Username already exists", HttpStatus.BAD_REQUEST.value()));
            }

            // Check for existing email
            if (serviceProviderService.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Email already exists", HttpStatus.BAD_REQUEST.value()));
            }

            // Check for existing business registration number
            if (serviceProviderService.existsByBusinessRegistrationNumber(request.getBusinessRegistrationNumber())) {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Business registration number already exists", HttpStatus.BAD_REQUEST.value()));
            }

            AuthResponse response = serviceProviderService.signup(request);

            if (response != null) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Registration failed", HttpStatus.BAD_REQUEST.value()));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Registration failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody ServiceProviderLoginRequest request, BindingResult bindingResult) {
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    errors.put(error.getField(), error.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(errors);
            }

            AuthResponse response = serviceProviderService.verify(request);

            if (response != null) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiError("Invalid username or password", HttpStatus.UNAUTHORIZED.value()));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Login failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            ServiceProviderPrincipal principal = (ServiceProviderPrincipal) authentication.getPrincipal();
            ServiceProvider serviceProvider = principal.getServiceProvider();

            // Create a response object without sensitive information
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("id", serviceProvider.getId());
            profileData.put("username", serviceProvider.getUsername());
            profileData.put("email", serviceProvider.getEmail());
            profileData.put("serviceType", serviceProvider.getServiceType().name());
            profileData.put("businessRegistrationNumber", serviceProvider.getBusinessRegistrationNumber());
            profileData.put("address", serviceProvider.getAddress());
            profileData.put("contactNo", serviceProvider.getContactNo());
            profileData.put("isApproved", serviceProvider.getIsApproved());
            profileData.put("isActive", serviceProvider.getIsActive());
            profileData.put("createdAt", serviceProvider.getCreatedAt());

            return ResponseEntity.ok(profileData);
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