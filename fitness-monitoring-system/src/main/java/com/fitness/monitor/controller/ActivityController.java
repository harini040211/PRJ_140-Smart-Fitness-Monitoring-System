package com.fitness.monitor.controller;

import com.fitness.monitor.dto.ActivityRequest;
import com.fitness.monitor.dto.ApiResponse;
import com.fitness.monitor.model.ActivityLog;
import com.fitness.monitor.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ActivityLog>> save(@RequestAttribute("userId") Long userId,
                                                          @Valid @RequestBody ActivityRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Activity saved", activityService.save(userId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityLog>>> history(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("Activity loaded", activityService.history(userId)));
    }
}
