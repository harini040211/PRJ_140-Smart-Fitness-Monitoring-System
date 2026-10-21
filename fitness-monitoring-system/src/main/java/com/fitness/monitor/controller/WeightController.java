package com.fitness.monitor.controller;

import com.fitness.monitor.dto.ApiResponse;
import com.fitness.monitor.dto.WeightRequest;
import com.fitness.monitor.model.WeightRecord;
import com.fitness.monitor.service.WeightService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weight")
public class WeightController {

    private final WeightService weightService;

    public WeightController(WeightService weightService) {
        this.weightService = weightService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WeightRecord>> log(@RequestAttribute("userId") Long userId,
                                                          @Valid @RequestBody WeightRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Weight recorded", weightService.log(userId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WeightRecord>>> history(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("Weight history loaded", weightService.history(userId)));
    }
}
