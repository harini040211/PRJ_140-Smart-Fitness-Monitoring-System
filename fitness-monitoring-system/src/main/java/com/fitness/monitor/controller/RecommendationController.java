package com.fitness.monitor.controller;

import com.fitness.monitor.dto.ApiResponse;
import com.fitness.monitor.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendations;

    public RecommendationController(RecommendationService recommendations) {
        this.recommendations = recommendations;
    }

    @GetMapping("/diet")
    public ResponseEntity<ApiResponse<Map<String, Object>>> diet(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("Diet plan ready", recommendations.dietPlan(userId)));
    }

    @GetMapping("/workout")
    public ResponseEntity<ApiResponse<Map<String, Object>>> workout(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("Workout plan ready", recommendations.workoutPlan(userId)));
    }
}
