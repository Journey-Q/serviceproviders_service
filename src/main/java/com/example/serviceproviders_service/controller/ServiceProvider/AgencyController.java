// Controller
package com.example.serviceproviders_service.controller.ServiceProvider;

import com.example.serviceproviders_service.dto.ServiceProvider.CreateAgencyProfileDTO;
import com.example.serviceproviders_service.entity.serviceProvider.TravelAgency.AgencyProfile;

import com.example.serviceproviders_service.services.ServiceProvider.AgencyProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("service/agency-profiles")
public class AgencyController {

    private final AgencyProfileService agencyProfileService;

    public AgencyController(AgencyProfileService agencyProfileService) {
        this.agencyProfileService = agencyProfileService;
    }

    @PostMapping("/create")
    public ResponseEntity<AgencyProfile> createAgencyProfile(@RequestBody CreateAgencyProfileDTO dto) {
        AgencyProfile createdProfile = agencyProfileService.createAgencyProfile(dto);
        return ResponseEntity.ok(createdProfile);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgencyProfile> getAgencyProfileById(@PathVariable Long id) {
        AgencyProfile agencyProfile = agencyProfileService.getAgencyProfileById(id);
        return ResponseEntity.ok(agencyProfile);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AgencyProfile>> getAllAgencyProfiles() {
        List<AgencyProfile> profiles = agencyProfileService.getAllAgencyProfiles();
        return ResponseEntity.ok(profiles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgencyProfile> updateAgencyProfile(
            @PathVariable Long id,
            @RequestBody CreateAgencyProfileDTO dto) {
        AgencyProfile updatedProfile = agencyProfileService.updateAgencyProfile(id, dto);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAgencyProfile(@PathVariable Long id) {
        boolean response = agencyProfileService.deleteAgencyProfile(id);
        if (response) {
            return ResponseEntity.ok("deleted successfully");
        }
        return ResponseEntity.noContent().build();
    }
}

