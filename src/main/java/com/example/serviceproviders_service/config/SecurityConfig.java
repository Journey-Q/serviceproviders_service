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

                        // Public endpoints - Room Booking (for customers/guests)
                        .requestMatchers(HttpMethod.POST, "/service/roombookings/create-with-payment").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/roombookings/reference/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/roombookings/session/").permitAll()

                        // Public endpoints - Rooms (for browsing)
                        .requestMatchers(HttpMethod.GET, "/service/rooms/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/rooms/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/rooms/service-provider/").permitAll()

                        // Hotel role endpoints - Room management
                        .requestMatchers(HttpMethod.POST, "/service/rooms/create").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.PUT, "/service/rooms/*").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.PATCH, "/service/rooms/*/status").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.DELETE, "/service/rooms/*").hasRole("HOTEL")

                        // Webhook endpoints - Public (Stripe callbacks)
                        .requestMatchers("/service/roombookings/webhook/").permitAll()

                        // Authenticated user endpoints - Customers can view their own bookings
                        .requestMatchers(HttpMethod.GET, "/service/roombookings/user/").authenticated()
                        .requestMatchers(HttpMethod.GET, "/service/roombookings/guest/").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/service/roombookings/*/cancel").authenticated()

                        // Hotel/Service Provider endpoints - Manage bookings
                        .requestMatchers(HttpMethod.GET, "/service/roombookings/all").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.GET, "/service/roombookings/service-provider/").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.GET, "/service/roombookings/payments/").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.GET, "/service/roombookings/revenue/").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.GET, "/service/roombookings/count/").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.PATCH, "/service/roombookings/*/status").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.PATCH, "/service/roombookings/*/refund").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.POST, "/service/roombookings/create").hasRole("HOTEL")

                        // Get booking by ID - Authenticated (with service-level authorization)
                        .requestMatchers(HttpMethod.GET, "/service/roombookings/*").authenticated()

                        // Public endpoints - Tours (for browsing)
                        .requestMatchers(HttpMethod.GET, "/service/tours/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/tours/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/tours/service-provider/").permitAll()

                        // Tour Guide role endpoints - Tour management
                        .requestMatchers(HttpMethod.POST, "/service/tours/create").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.PUT, "/service/tours/*").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.PATCH, "/service/tours/*/status").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.DELETE, "/service/tours/*").hasRole("TOUR_GUIDE")

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
                        .requestMatchers(HttpMethod.GET, "/service/vehicles/service-provider/").permitAll()

                        // Travel Agent role endpoints - Vehicle management
                        .requestMatchers(HttpMethod.POST, "/service/vehicles/create").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.PUT, "/service/vehicles/*").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.PATCH, "/service/vehicles/*/status").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.DELETE, "/service/vehicles/*").hasRole("TRAVEL_AGENT")

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
                        .requestMatchers(HttpMethod.GET, "/service/reviews/service-provider/").permitAll()

                        // Authenticated users - Create and manage their own reviews
                        .requestMatchers(HttpMethod.POST, "/service/reviews/create").authenticated()
                        .requestMatchers(HttpMethod.GET, "/service/reviews/user/").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/service/reviews/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/service/reviews/*").authenticated()

                        // Service Provider - Manage review status and verification
                        .requestMatchers(HttpMethod.PATCH, "/service/reviews/*/status").hasAnyRole("HOTEL", "TOUR_GUIDE", "TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.PATCH, "/service/reviews/*/verification").hasAnyRole("HOTEL", "TOUR_GUIDE", "TRAVEL_AGENT", "ADMIN")


                        // Public endpoints - Tour Booking (for customers/guests)
                        .requestMatchers(HttpMethod.POST, "/service/tourbookings/create-with-payment").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/tourbookings/reference/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/tourbookings/session/*").permitAll()

                        // Webhook endpoints - Public (Stripe callbacks for tour bookings)
                        .requestMatchers("/service/tourbookings/webhook/*").permitAll()

                        // Authenticated user endpoints - Customers can view their own tour bookings
                        .requestMatchers(HttpMethod.GET, "/service/tourbookings/user/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/service/tourbookings/customer/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/service/tourbookings/*/cancel").authenticated()

                        // Tour Guide/Service Provider endpoints - Manage tour bookings
                        .requestMatchers(HttpMethod.GET, "/service/tourbookings/all").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.GET, "/service/tourbookings/service-provider/*").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.GET, "/service/tourbookings/payments/*").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.GET, "/service/tourbookings/revenue/*").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.GET, "/service/tourbookings/count/*").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.PATCH, "/service/tourbookings/*/status").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.PATCH, "/service/tourbookings/*/refund").hasRole("TOUR_GUIDE")
                        .requestMatchers(HttpMethod.POST, "/service/tourbookings/create").hasRole("TOUR_GUIDE")

                        // Get tour booking by ID - Authenticated (with service-level authorization)
                        .requestMatchers(HttpMethod.GET, "/service/tourbookings/*").authenticated()

                        // Public endpoints - Vehicle Booking (for customers/guests)
                        .requestMatchers(HttpMethod.POST, "/service/vehiclebookings/create-with-payment").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/vehiclebookings/reference/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/service/vehiclebookings/session/*").permitAll()

                        // Webhook endpoints - Public (Stripe callbacks for vehicle bookings)
                        .requestMatchers("/service/vehiclebookings/webhook/*").permitAll()

                        // Authenticated user endpoints - Customers can view their own vehicle bookings
                        .requestMatchers(HttpMethod.GET, "/service/vehiclebookings/user/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/service/vehiclebookings/customer/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/service/vehiclebookings/*/cancel").authenticated()

                        // Travel Agent/Service Provider endpoints - Manage vehicle bookings
                        .requestMatchers(HttpMethod.GET, "/service/vehiclebookings/all").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.GET, "/service/vehiclebookings/service-provider/*").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.GET, "/service/vehiclebookings/payments/*").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.GET, "/service/vehiclebookings/revenue/*").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.GET, "/service/vehiclebookings/count/*").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.PATCH, "/service/vehiclebookings/*/status").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.PATCH, "/service/vehiclebookings/*/refund").hasRole("TRAVEL_AGENT")
                        .requestMatchers(HttpMethod.POST, "/service/vehiclebookings/create").hasRole("TRAVEL_AGENT")

                        // Get vehicle booking by ID - Authenticated (with service-level authorization)
                        .requestMatchers(HttpMethod.GET, "/service/vehiclebookings/*").authenticated()

                        // Unified Booking History endpoints - Authenticated users can view their own booking history
                        .requestMatchers(HttpMethod.GET, "/api/booking-history/user/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/booking-history/user/*/hotels").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/booking-history/user/*/tours").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/booking-history/user/*/vehicles").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/booking-history/user/*/count").authenticated()

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
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Add "Authorization" to allowed headers
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token", "Authorization"));
        configuration.setExposedHeaders(List.of("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/", configuration);
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