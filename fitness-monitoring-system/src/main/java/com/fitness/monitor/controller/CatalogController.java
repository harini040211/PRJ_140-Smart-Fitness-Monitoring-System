package com.fitness.monitor.controller;

import com.fitness.monitor.dto.ApiResponse;
import com.fitness.monitor.dto.ExerciseItem;
import com.fitness.monitor.dto.FoodItem;
import com.fitness.monitor.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService catalog;

    public CatalogController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/exercises")
    public ResponseEntity<ApiResponse<List<ExerciseItem>>> exercises() {
        return ResponseEntity.ok(ApiResponse.ok("Exercises loaded", catalog.exercises()));
    }

    @GetMapping("/foods")
    public ResponseEntity<ApiResponse<List<FoodItem>>> foods() {
        return ResponseEntity.ok(ApiResponse.ok("Foods loaded", catalog.foods()));
    }
}
