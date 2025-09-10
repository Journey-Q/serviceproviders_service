package com.example.serviceproviders_service.repository.ServiceProvider.Hotel;


import com.example.serviceproviders_service.entity.serviceProvider.Hotel.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByServiceProviderId(Long serviceProviderId);

    List<Room> findByServiceProviderIdAndStatus(Long serviceProviderId, Room.RoomStatus status);

    boolean existsByServiceProviderIdAndName(Long serviceProviderId, String name);

    boolean existsByServiceProviderIdAndNameAndIdNot(Long serviceProviderId, String name, Long id);

    @Query("SELECT r FROM Room r WHERE r.serviceProviderId = :serviceProviderId AND r.status = 'AVAILABLE'")
    List<Room> findAvailableRoomsByServiceProviderId(@Param("serviceProviderId") Long serviceProviderId);

    long countByServiceProviderId(Long serviceProviderId);

    long countByServiceProviderIdAndStatus(Long serviceProviderId, Room.RoomStatus status);

    @Query("SELECT r FROM Room r WHERE r.serviceProviderId = :serviceProviderId AND r.price BETWEEN :minPrice AND :maxPrice")
    List<Room> findByServiceProviderIdAndPriceRange(@Param("serviceProviderId") Long serviceProviderId,
                                                    @Param("minPrice") BigDecimal minPrice,
                                                    @Param("maxPrice") BigDecimal maxPrice);

    List<Room> findByServiceProviderIdAndMaxOccupancyGreaterThanEqual(Long serviceProviderId, Integer minOccupancy);
}
