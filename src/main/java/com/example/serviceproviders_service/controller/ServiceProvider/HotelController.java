package com.example.serviceproviders_service.controller.ServiceProvider;

import com.example.serviceproviders_service.dto.ServiceProvider.CreateHotelProfileDTO;
import com.example.serviceproviders_service.entity.serviceProvider.Hotel.HotelProfile;
import com.example.serviceproviders_service.services.ServiceProvider.HotelProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("service/hotel-profiles")
public class HotelController {

    private final HotelProfileService hotelProfileService;

    public HotelController(HotelProfileService hotelProfileService) {
        this.hotelProfileService = hotelProfileService;
    }

    @PostMapping("/create")
    public ResponseEntity<HotelProfile> createHotelProfile(@RequestBody CreateHotelProfileDTO dto) {
        HotelProfile createdProfile = hotelProfileService.createHotelProfile(dto);
        return ResponseEntity.ok(createdProfile);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelProfile> getHotelProfileById(@PathVariable Long id) {
        HotelProfile hotelProfile = hotelProfileService.getHotelProfileById(id);
        return ResponseEntity.ok(hotelProfile);
    }

    @GetMapping("/all")
    public ResponseEntity<List<HotelProfile>> getAllHotelProfiles() {
        List<HotelProfile> profiles = hotelProfileService.getAllHotelProfiles();
        return ResponseEntity.ok(profiles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelProfile> updateHotelProfile(
            @PathVariable Long id,
            @RequestBody CreateHotelProfileDTO dto) {
        HotelProfile updatedProfile = hotelProfileService.updateHotelProfile(id, dto);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHotelProfile(@PathVariable Long id) {
        boolean Response =  hotelProfileService.deleteHotelProfile(id);
        if (Response) {
            return ResponseEntity.ok("deleted sucessfully");
        }
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/by-amenity/{amenity}")
//    public ResponseEntity<List<HotelProfile>> getHotelsByAmenity(@PathVariable String amenity) {
//        List<HotelProfile> profiles = hotelProfileService.findHotelsByAmenity(amenity);
//        return ResponseEntity.ok(profiles);
//    }
}