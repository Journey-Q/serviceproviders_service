// config/SecurityConfig.java
package com.example.serviceproviders_service.config;

import com.example.serviceproviders_service.services.ServiceProvider.ServiceProviderUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtAuthFilter;
    private final ServiceProviderUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Add CORS configuration
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/service/auth/signup", "/service/auth/login", "/service/auth/test").permitAll()
                        .requestMatchers("/admin/auth/login", "/admin/auth/test", "/admin/auth/setup").permitAll()

                        // Public endpoints - Rooms (for browsing)
                        .requestMatchers(HttpMethod.GET, "/service/rooms/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/rooms/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/rooms/service-provider/*").permitAll()

                        // Hotel role endpoints - Room management
                        .requestMatchers(HttpMethod.POST, "/service/rooms/create").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.PUT, "/service/rooms/*").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.PATCH, "/service/rooms/*/status").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.DELETE, "/service/rooms/*").hasRole("HOTEL")

                        // Public endpoints - Room Bookings (customers can book and check bookings)
                        .requestMatchers(HttpMethod.POST, "/service/room-bookings/create").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/room-bookings/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/room-bookings/customer/*").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/service/room-bookings/*/cancel").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/room-bookings/check-availability").permitAll()

                        // Hotel role endpoints - Room Booking management
                        .requestMatchers(HttpMethod.GET, "/service/room-bookings/room/*").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.GET, "/service/room-bookings/provider/*").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.PUT, "/service/room-bookings/*/status").hasRole("HOTEL")

                        // Public endpoints - Tours (for browsing)
                        .requestMatchers(HttpMethod.GET, "/service/tours/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/tours/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/tours/service-provider/").permitAll()

                        // Tour Guide role endpoints - Tour management
                        .requestMatchers(HttpMethod.POST, "/service/tours/create").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.PUT, "/service/tours/*").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.PATCH, "/service/tours/*/status").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.DELETE, "/service/tours/*").hasRole("TOUR_GUIDE")

                        // Public endpoints - Tour Bookings (customers can book and check bookings)
                        .requestMatchers(HttpMethod.POST, "/service/tour-bookings/create").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/tour-bookings/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/tour-bookings/customer/*").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/service/tour-bookings/*/cancel").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/tour-bookings/tour/*/capacity").permitAll()

                        // Tour Guide role endpoints - Tour Booking management (approval workflow)
                        .requestMatchers(HttpMethod.GET, "/service/tour-bookings/tour/*").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.GET, "/service/tour-bookings/guide/*").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.PUT, "/service/tour-bookings/*/approve").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.PUT, "/service/tour-bookings/*/reject").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.PUT, "/service/tour-bookings/*/complete").hasRole("TOUR_GUIDE")

                        // Public endpoints - Drivers (for browsing)
                        .requestMatchers(HttpMethod.GET, "/service/drivers/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/drivers/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/drivers/service-provider/").permitAll()

                        // Travel Agent role endpoints - Driver management
                        .requestMatchers(HttpMethod.POST, "/service/drivers/create").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.PUT, "/service/drivers/*").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.PATCH, "/service/drivers/*/status").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.PATCH, "/service/drivers/*/rating").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.DELETE, "/service/drivers/*").hasRole("TRAVEL_AGENT")

                        // Public endpoints - Vehicles (for browsing)
                        .requestMatchers(HttpMethod.GET, "/service/vehicles/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/vehicles/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/vehicles/service-provider/**").permitAll()

                        // Travel Agent role endpoints.
                        // - Vehicle management
                        .requestMatchers(HttpMethod.POST, "/service/vehicles/create").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.PUT, "/service/vehicles/*").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.PATCH, "/service/vehicles/*/status").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.DELETE, "/service/vehicles/*").hasRole("TRAVEL_AGENT")

                        // Public endpoints - Vehicle Bookings (customers can book and check bookings)
                        .requestMatchers(HttpMethod.POST, "/service/vehicle-bookings/create").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/vehicle-bookings/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/vehicle-bookings/customer/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/vehicle-bookings/user/*").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/service/vehicle-bookings/*/cancel").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/vehicle-bookings/vehicle/*/availability").permitAll()

                        // Travel Agent role endpoints - Vehicle Booking management (approval workflow)
                        .requestMatchers(HttpMethod.GET, "/service/vehicle-bookings/vehicle/*").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.GET, "/service/vehicle-bookings/agency/*").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.PUT, "/service/vehicle-bookings/*/approve").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.PUT, "/service/vehicle-bookings/*/reject").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.PUT, "/service/vehicle-bookings/*/complete").hasRole("TRAVEL_AGENT")

                        // Public endpoints - Agency Profiles (for browsing)
                        .requestMatchers(HttpMethod.GET, "/service/agency-profiles/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/agency-profiles/all").permitAll()

                        // Travel Agent role endpoints - Agency Profile management
                        .requestMatchers(HttpMethod.POST, "/service/agency-profiles/create").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.PUT, "/service/agency-profiles/*").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.DELETE, "/service/agency-profiles/*").hasRole("TRAVEL_AGENT")

                        // Public endpoints - Hotel Profiles (for browsing)
                        .requestMatchers(HttpMethod.GET, "/service/hotel-profiles/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/hotel-profiles/all").permitAll()

                        // Hotel role endpoints - Hotel Profile management
                        .requestMatchers(HttpMethod.POST, "/service/hotel-profiles/create").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.PUT, "/service/hotel-profiles/*").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.DELETE, "/service/hotel-profiles/*").hasRole("HOTEL")


                        // Public endpoints - Tour Guide Profiles (for browsing)
                        .requestMatchers(HttpMethod.GET, "/service/tour-guide-profiles/*").permitAll()

                        // Tour Guide role endpoints - Tour Guide Profile management
                        .requestMatchers(HttpMethod.POST, "/service/tour-guide-profiles/create").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.PUT, "/service/tour-guide-profiles/*").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.DELETE, "/service/tour-guide-profiles/*").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.DELETE, "/service/tour-guide-profiles/delete/*").hasRole("TOUR_GUIDE")


                        // Public endpoints - Promotions (for browsing)
                        .requestMatchers(HttpMethod.GET, "/service/promotions/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/promotions/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/promotions/service-provider/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/promotions/status/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/promotions/active").permitAll()


                        // Public endpoints - Reviews (for browsing)
                        .requestMatchers(HttpMethod.GET, "/service/reviews/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/reviews/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/reviews/booking/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/reviews/service-provider/**").permitAll()

                        // Authenticated users - Create and manage their own reviews
                        .requestMatchers(HttpMethod.POST, "/service/reviews/create").authenticated()
                        .requestMatchers(HttpMethod.GET, "/service/reviews/user/").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/service/reviews/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/service/reviews/*").authenticated()

                        // Service Provider - Manage review status and verification
                        .requestMatchers(HttpMethod.PATCH, "/service/reviews/*/status").hasAnyRole("HOTEL", "TOUR_GUIDE", "TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.PATCH, "/service/reviews/*/verification").hasAnyRole("HOTEL", "TOUR_GUIDE", "TRAVEL_AGENT", "ADMIN")

                        // Public endpoints - Tour Package Reviews (for browsing)
                        .requestMatchers(HttpMethod.GET, "/service/tour-package-reviews/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/tour-package-reviews/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/tour-package-reviews/booking/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/tour-package-reviews/tour/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/tour-package-reviews/user/*").permitAll()

                        // Public - Create tour package reviews (customers)
                        .requestMatchers(HttpMethod.POST, "/service/tour-package-reviews/create").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/service/tour-package-reviews/*").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/service/tour-package-reviews/*").permitAll()

                        // Tour Guide - Manage tour package review status and verification
                        .requestMatchers(HttpMethod.PATCH, "/service/tour-package-reviews/*/status").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.PATCH, "/service/tour-package-reviews/*/verification").hasAnyRole("TOUR_GUIDE", "ADMIN")

                        // Public endpoints - Booking History (customers can view their booking history)
                        .requestMatchers(HttpMethod.GET, "/api/booking-history/user/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/booking-history/user/*/rooms").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/booking-history/user/*/tours").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/booking-history/user/*/vehicles").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/booking-history/*").permitAll()

                        // Public endpoints - Unified Service Provider Access (for browsing all providers)
                        .requestMatchers(HttpMethod.GET, "/service/providers/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/providers/approved").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/providers/hotels").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/providers/tour-guides").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/providers/agencies").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/providers/by-type/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/providers/*").permitAll()

                        // Public endpoints - Unified Service Provider Profiles Only (profile details only, no resources/revenue)
                        .requestMatchers(HttpMethod.GET, "/service/providers/profiles/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/providers/profiles/approved").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/providers/profiles/hotels").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/providers/profiles/tour-guides").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/providers/profiles/agencies").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/providers/profiles/by-type/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/providers/profiles/*").permitAll()

                        // Service Provider endpoints
                        .requestMatchers("/service/auth/profile").hasAnyRole("HOTEL", "TOUR_GUIDE", "TRAVEL_AGENT")
                        .requestMatchers("/service").permitAll()

                        // Admin endpoints
                        .requestMatchers("/admin/profile", "/admin/").hasAnyRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*")); // Allow all origins with credentials support
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token", "Authorization"));
        configuration.setExposedHeaders(List.of("x-auth-token"));
        configuration.setAllowCredentials(true); // Enable credentials (cookies, authorization headers, etc.)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
