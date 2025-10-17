# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Service Providers Service is a Spring Boot 3.5.3 application that manages multiple types of tourism service providers (Hotels, Tour Guides, and Travel Agencies). It provides a multi-tenant platform with role-based access control, JWT authentication, and Stripe payment integration.

**Tech Stack:**
- Java 17
- Spring Boot 3.5.3 (Web, Security, Data JPA, Mail, Validation)
- PostgreSQL (AWS RDS)
- JWT authentication (jjwt 0.11.5)
- Stripe payment integration (29.6.0-beta.1)
- Lombok for boilerplate reduction
- Maven for build management

## Development Commands

### Build and Run
```bash
# Clean and build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run

# On Windows, use mvnw.cmd instead:
mvnw.cmd spring-boot:run

# Build without tests
./mvnw clean install -DskipTests

# Package as JAR
./mvnw clean package
```

### Testing
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ServiceprovidersServiceApplicationTests

# Run tests with coverage
./mvnw clean test jacoco:report
```

### Database
The application uses PostgreSQL (AWS RDS) with Hibernate DDL auto-update mode. Connection pooling is managed by HikariCP with configuration for 20 max connections and 10 minimum idle connections.

**Required environment variables:**
- `JWT_SECRET_KEY` - Secret key for JWT token generation
- `SUPPORT_EMAIL` - Email address for sending notifications
- `APP_PASSWORD` - App password for SMTP authentication

## Architecture

### Layered Architecture Pattern
The codebase follows a standard Spring Boot layered architecture:

```
Controller → Service → Repository → Entity
              ↕
             DTO
```

### Package Structure by Feature

The application is organized by feature with clear separation between Admin and Service Provider domains:

**Entity Layer** (`entity/`)
- `Admin/` - Admin user and subscription plan entities
- `serviceProvider/` - Base ServiceProvider entity with ServiceProviderType enum (HOTEL, TOUR_GUIDE, TRAVEL_AGENT)
  - `Hotel/` - HotelProfile, Room, RoomBooking, ContactInfo, Coordinates
  - `TourGuide/` - TourGuideProfile, Tour, TourItinerary, PastTourImage
  - `TravelAgency/` - AgencyInfo, Driver, Vehicle

**Service Layer** (`services/`)
- `Admin/` - Admin authentication, service provider management, subscription plans
- `ServiceProvider/` - Service provider authentication and profile services
  - `Hotel/` - Room management, booking system, Stripe integration
  - `TourGuide/` - Tour management and tour guide profiles
  - `TravelAgency/` - Driver and vehicle management
- Cross-cutting: `JWTService`, `StripeService` (shared payment processing)

**Controller Layer** (`controller/`)
- `Admin/` - Admin operations and subscription management
- `ServiceProvider/` - Authentication, profiles, reviews, promotions
  - `Hotel/` - Room and booking endpoints
  - `TourGuide/` - Tour management endpoints
  - `TravelAgency/` - Driver and vehicle endpoints

**Repository Layer** (`repository/`)
- Spring Data JPA repositories following the same feature-based organization

**DTO Layer** (`dto/`)
- Request/Response objects organized by feature to match controllers

### Key Design Patterns

**Multi-tenant Service Provider Model:**
- Single `ServiceProvider` entity with discriminator field `serviceType` (enum: HOTEL, TOUR_GUIDE, TRAVEL_AGENT)
- Each provider type has dedicated profile entities (HotelProfile, TourGuideProfile, AgencyInfo)
- Role-based access control enforced at SecurityConfig level with roles: ROLE_HOTEL, ROLE_TOUR_GUIDE, ROLE_TRAVEL_AGENT, ROLE_ADMIN

**Authentication & Authorization:**
- JWT-based stateless authentication (JwtFilter → UsernamePasswordAuthenticationFilter)
- Custom UserDetailsService implementations: ServiceProviderUserDetailsService (for service providers), separate for Admin
- UserPrincipal pattern: ServiceProviderPrincipal wraps ServiceProvider entity
- BCrypt password encoding
- Token expiration: 1 hour (configurable via `security.jwt.expiration-time`)

**Security Configuration** (`SecurityConfig.java`):
- Extensive endpoint-level authorization rules (lines 43-178)
- Public endpoints: signup/login, browsing (rooms, tours, drivers, vehicles, reviews)
- Role-specific endpoints: Room/Tour/Driver/Vehicle CRUD operations restricted by service type
- Webhook endpoints (Stripe callbacks) are public
- CORS enabled for all origins with Authorization header support

**Payment Integration:**
- Stripe integration for room bookings (`HotelRoomBookingStripeService`)
- Webhook handling for payment confirmation (`/service/roombookings/webhook/`)
- Payment session tracking with reference numbers

**Email Notifications:**
- Gmail SMTP integration (requires `SUPPORT_EMAIL` and `APP_PASSWORD` env vars)
- Used for verification codes and booking confirmations

**Booking System (Hotel-specific):**
- RoomBooking entity tracks customer reservations
- Payment processing integrated with Stripe
- Booking statuses: PENDING, CONFIRMED, CANCELLED, COMPLETED
- Revenue tracking and reporting for service providers

**Common Features:**
- Reviews system (customers can review services)
- Promotions (service providers can create promotional offers)
- Bank details management for service providers
- Admin approval workflow (isApproved field on ServiceProvider)

## Important Configuration Notes

**Database Configuration:**
- PostgreSQL connection with HikariCP
- `spring.jpa.hibernate.ddl-auto=update` - schema auto-updates on startup
- Connection leak detection enabled (60s threshold)
- Show SQL and format SQL enabled for debugging

**Security Best Practices:**
- Sensitive data (DB credentials, Stripe keys) should be externalized to environment variables
- Current application.properties contains test credentials - rotate for production
- JWT secret key must be set via environment variable
- Session management is STATELESS (no server-side sessions)

**CORS Configuration:**
- Currently allows all origins (`*`) - restrict in production
- Exposes `x-auth-token` header for custom authentication flows

## Service Provider Workflow

1. **Registration:** POST `/service/auth/signup` with ServiceProviderSignupRequest (includes serviceType, businessRegistrationNumber)
2. **Login:** POST `/service/auth/login` → receives JWT token
3. **Profile Creation:** Based on serviceType, create corresponding profile:
   - HOTEL → POST `/service/hotel-profiles/create`
   - TOUR_GUIDE → POST `/service/tour-guide-profiles/create`
   - TRAVEL_AGENT → POST `/service/agency-profiles/create`
4. **Manage Resources:** Add rooms/tours/drivers based on role
5. **Handle Bookings:** Track and manage customer bookings (for hotels)
6. **Admin Approval:** Admin reviews and approves service providers via admin endpoints

## Testing

The project has minimal test coverage currently (only ApplicationTests). When adding tests:
- Use `@SpringBootTest` for integration tests
- Use `@WebMvcTest` for controller tests
- Mock external services (Stripe, Email) in tests
- Use `@DataJpaTest` for repository tests

## Common Development Tasks

**Adding a new Service Provider Type:**
1. Add enum value to `ServiceProviderType`
2. Create profile entity in `entity/serviceProvider/<NewType>/`
3. Create repository, service, controller, and DTOs
4. Update `SecurityConfig` with role-based rules
5. Update authentication logic if needed

**Adding new endpoints:**
1. Create controller method with appropriate `@RequestMapping`
2. Update `SecurityConfig.securityFilterChain()` with authorization rules
3. Ensure DTOs validate inputs (`@Valid` annotation)
4. Return consistent error responses using `ApiError`

**Database Schema Changes:**
- Modify entity annotations - Hibernate will auto-update schema on restart (ddl-auto=update)
- For production, consider migration tools like Flyway/Liquibase

**Debugging Authentication Issues:**
- Check JWT token expiration
- Verify role names match SecurityConfig (must be prefixed with ROLE_ in authorities)
- Review debug logs: `logging.level.org.springframework.security=DEBUG`
- Use `/service/auth/test` or `/admin/auth/test` endpoints to verify API is up