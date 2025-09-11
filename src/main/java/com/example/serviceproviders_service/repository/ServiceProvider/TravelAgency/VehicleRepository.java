package com.example.serviceproviders_service.repository.ServiceProvider.TravelAgency;

import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByServiceProviderId(Long serviceProviderId);

    List<Vehicle> findByServiceProviderIdAndStatus(Long serviceProviderId, Vehicle.VehicleStatus status);

    List<Vehicle> findByServiceProviderIdAndType(Long serviceProviderId, Vehicle.VehicleType type);

    List<Vehicle> findByStatus(Vehicle.VehicleStatus status);

    boolean existsByServiceProviderIdAndName(Long serviceProviderId, String name);

    boolean existsByServiceProviderIdAndNameAndIdNot(Long serviceProviderId, String name, Long id);

    boolean existsByLicensePlate(String licensePlate);

    boolean existsByLicensePlateAndIdNot(String licensePlate, Long id);

    @Query("SELECT v FROM Vehicle v WHERE v.serviceProviderId = :serviceProviderId AND v.status = 'AVAILABLE'")
    List<Vehicle> findAvailableVehiclesByServiceProviderId(@Param("serviceProviderId") Long serviceProviderId);

    @Query("SELECT v FROM Vehicle v WHERE v.serviceProviderId = :serviceProviderId AND v.numberOfSeats >= :minSeats")
    List<Vehicle> findByServiceProviderIdAndMinSeats(@Param("serviceProviderId") Long serviceProviderId, @Param("minSeats") Integer minSeats);

    @Query("SELECT v FROM Vehicle v WHERE v.serviceProviderId = :serviceProviderId AND v.pricePerKmWithAC BETWEEN :minPrice AND :maxPrice")
    List<Vehicle> findByServiceProviderIdAndPriceRange(@Param("serviceProviderId") Long serviceProviderId,
                                                       @Param("minPrice") BigDecimal minPrice,
                                                       @Param("maxPrice") BigDecimal maxPrice);

    List<Vehicle> findByServiceProviderIdAndFuelType(Long serviceProviderId, Vehicle.FuelType fuelType);

    List<Vehicle> findByServiceProviderIdAndAc(Long serviceProviderId, Boolean ac);

    long countByServiceProviderId(Long serviceProviderId);

    long countByServiceProviderIdAndStatus(Long serviceProviderId, Vehicle.VehicleStatus status);
}
