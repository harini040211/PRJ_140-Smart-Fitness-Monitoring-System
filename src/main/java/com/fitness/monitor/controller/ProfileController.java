package com.fitness.monitor.controller;

import com.fitness.monitor.dto.ApiResponse;
import com.fitness.monitor.dto.GoalsRequest;
import com.fitness.monitor.dto.ProfileRequest;
import com.fitness.monitor.model.Profile;
import com.fitness.monitor.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Profile>> get(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("Profile loaded", profileService.find(userId).orElse(null)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Profile>> save(@RequestAttribute("userId") Long userId,
                                                      @Valid @RequestBody ProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Profile saved", profileService.save(userId, request)));
    }

    @PutMapping("/goals")
    public ResponseEntity<ApiResponse<Profile>> goals(@RequestAttribute("userId") Long userId,
                                                       @Valid @RequestBody GoalsRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Goals updated", profileService.updateGoals(userId, request)));
    }
}
