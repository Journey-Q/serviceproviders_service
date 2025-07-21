package com.example.serviceproviders_service.controller.ServiceProvider;

import com.example.serviceproviders_service.dto.ServiceProvider.TourGuideProfileDTO;
import com.example.serviceproviders_service.entity.serviceProvider.TourGuide.TourGuideProfile;
import com.example.serviceproviders_service.services.ServiceProvider.TourGuideProfileService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service/tour-guide-profiles")
@RequiredArgsConstructor
public class TourGuideProfileController {

    private final TourGuideProfileService profileService;

    @PostMapping("/create")
    public ResponseEntity<TourGuideProfile> createProfile(@RequestBody TourGuideProfileDTO dto) {
        TourGuideProfile createdProfile = profileService.createProfile(dto);
        return ResponseEntity.ok(createdProfile);
    }



    @GetMapping("/{id}")
    public ResponseEntity<TourGuideProfile> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getProfileById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourGuideProfile> updateProfile(
            @PathVariable Long id,
            @RequestBody TourGuideProfileDTO dto) {
        return ResponseEntity.ok(profileService.updateProfile(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        profileService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTourGuideProfile(@PathVariable Long id){
        boolean Response =  profileService.deleteTourGuideProfile(id);
        if (Response) {
            return ResponseEntity.ok("deleted sucessful");
        }
        return ResponseEntity.noContent().build();    }
}