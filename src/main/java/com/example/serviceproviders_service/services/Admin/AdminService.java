// services/AdminService.java
package com.example.serviceproviders_service.services.Admin;

import com.example.serviceproviders_service.dto.Admin.AdminAuthResponse;
import com.example.serviceproviders_service.dto.Admin.AdminLoginRequest;
import com.example.serviceproviders_service.entity.Admin.Admin;
import com.example.serviceproviders_service.entity.Admin.AdminPrincipal;
import com.example.serviceproviders_service.exception.BadRequestException;
import com.example.serviceproviders_service.repository.Admin.AdminRepo;
import com.example.serviceproviders_service.services.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class AdminService {

    private final AdminRepo adminRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    @Lazy
    private final AuthenticationManager authenticationManager;

    public AdminAuthResponse verify(AdminLoginRequest req) {
        try {
            // Check if email exists first
            boolean emailExists = adminRepo.existsByEmail(req.getEmail());
            if (!emailExists) {
                throw new BadRequestException("Email does not exist");
            }

            // Email exists, now authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            AdminPrincipal userPrincipal = (AdminPrincipal) authentication.getPrincipal();
            Admin admin = userPrincipal.getAdmin();
            String token = jwtService.generateToken(userPrincipal);

            AdminAuthResponse authResponse = new AdminAuthResponse();
            authResponse.setAdmin(admin);
            authResponse.setAccessToken(token);
            authResponse.setExpiresIn(jwtService.getExpirationTime());

            return authResponse;

        } catch (BadRequestException e) {
            throw e; // Re-throw email not found error
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect password");
        } catch (UsernameNotFoundException e) {
            // This shouldn't happen since we check email existence first
            throw new BadRequestException("Email does not exist");
        } catch (Exception e) {
            throw new RuntimeException("Authentication service unavailable", e);
        }
    }

    public Admin findByEmail(String email) {
        return adminRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    public Admin findByUsername(String username) {
        return adminRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    // Method to create default admin (for testing)
    public Admin createDefaultAdmin() {
        if (!adminRepo.existsByEmail("admin@serviceproviders.com")) {
            Admin admin = new Admin(
                    "admin",
                    "admin@serviceproviders.com",
                    passwordEncoder.encode("admin123")
            );
            return adminRepo.save(admin);
        }
        return adminRepo.findByEmail("admin@serviceproviders.com").orElse(null);
    }
}