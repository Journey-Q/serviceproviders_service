package com.example.serviceproviders_service.repository.ServiceProvider.TourGuide;

import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.PastTourImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PastTourImageRepository extends JpaRepository<PastTourImage, Long> {

    List<PastTourImage> findByTourIdOrderByOrderIndex(Long tourId);

    void deleteByTourId(Long tourId);

    long countByTourId(Long tourId);
}