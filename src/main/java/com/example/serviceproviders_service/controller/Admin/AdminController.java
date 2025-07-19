// controller/AdminController.java
package com.example.serviceproviders_service.controller.Admin;

import com.example.serviceproviders_service.dto.Admin.AdminAuthResponse;
import com.example.serviceproviders_service.dto.Admin.AdminLoginRequest;
import com.example.serviceproviders_service.dto.ApiError;
import com.example.serviceproviders_service.entity.Admin.Admin;
import com.example.serviceproviders_service.entity.Admin.AdminPrincipal;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.services.Admin.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AdminLoginRequest request, BindingResult bindingResult) {
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    errors.put(error.getField(), error.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(errors);
            }

            AdminAuthResponse response = adminService.verify(request);
            return ResponseEntity.ok(response);

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError(e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError(e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Login failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            AdminPrincipal principal = (AdminPrincipal) authentication.getPrincipal();
            Admin admin = principal.getAdmin();

            // Create a response object without sensitive information
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("id", admin.getId());
            profileData.put("username", admin.getUsername());
            profileData.put("email", admin.getEmail());
            profileData.put("role", admin.getRole());
            profileData.put("isActive", admin.getIsActive());
            profileData.put("createdAt", admin.getCreatedAt());

            return ResponseEntity.ok(profileData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError("Unauthorized", HttpStatus.UNAUTHORIZED.value()));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Admin API is working!");
    }

    // Endpoint to create default admin (for testing only)
    @PostMapping("/setup")
    public ResponseEntity<?> setupDefaultAdmin() {
        try {
            Admin admin = adminService.createDefaultAdmin();
            if (admin != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Default admin created/exists");
                response.put("email", admin.getEmail());
                response.put("username", admin.getUsername());
                response.put("defaultPassword", "admin123");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiError("Failed to create admin", HttpStatus.BAD_REQUEST.value()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Setup failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}