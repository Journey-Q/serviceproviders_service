package com.example.serviceproviders_service.repository.ServiceProvider;

import com.example.serviceproviders_service.entity.serviceProvider.Hotel.HotelProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelProfileRepository extends JpaRepository<HotelProfile, Long> {

    // Check if hotel exists by name and location (for duplicate prevention)
    boolean existsByHotelNameAndLocation(String hotelName, String location);

    // Check if hotel exists by name and location excluding specific serviceProviderId (for updates)
    // Changed from 'IdNot' to 'ServiceProviderIdNot' to match your entity field
    boolean existsByHotelNameAndLocationAndServiceProviderIdNot(String hotelName, String location, Long serviceProviderId);

    boolean existsByServiceProviderId(Long serviceProviderId);
}