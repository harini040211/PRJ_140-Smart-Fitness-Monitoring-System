package com.fitness.monitor.controller;

import com.fitness.monitor.dto.ApiResponse;
import com.fitness.monitor.service.AnalyticsService;
import com.fitness.monitor.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final DashboardService dashboardService;
    private final AnalyticsService analyticsService;

    public DashboardController(DashboardService dashboardService, AnalyticsService analyticsService) {
        this.dashboardService = dashboardService;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> dashboard(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("Dashboard loaded", dashboardService.summary(userId)));
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> analytics(@RequestAttribute("userId") Long userId,
                                                                       @RequestParam(defaultValue = "30") int days) {
        int window = Math.max(7, Math.min(180, days));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("days", analyticsService.lastDays(userId, window));
        body.putAll(analyticsService.breakdowns(userId, window));
        return ResponseEntity.ok(ApiResponse.ok("Analytics loaded", body));
    }
}
