package com.fitness.monitor.controller;

import com.fitness.monitor.dto.ApiResponse;
import com.fitness.monitor.dto.WaterRequest;
import com.fitness.monitor.service.WaterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/water")
public class WaterController {

    private final WaterService waterService;

    public WaterController(WaterService waterService) {
        this.waterService = waterService;
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<Map<String, Object>>> today(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("Water loaded", waterService.today(userId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> add(@RequestAttribute("userId") Long userId,
                                                                 @Valid @RequestBody WaterRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Water added", waterService.add(userId, request)));
    }

    @DeleteMapping("/last")
    public ResponseEntity<ApiResponse<Map<String, Object>>> undo(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("Last entry removed", waterService.removeLast(userId)));
    }
}
