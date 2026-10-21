package com.fitness.monitor.controller;

import com.fitness.monitor.dto.ApiResponse;
import com.fitness.monitor.dto.MealRequest;
import com.fitness.monitor.model.Meal;
import com.fitness.monitor.service.MealService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Meal>> log(@RequestAttribute("userId") Long userId,
                                                  @Valid @RequestBody MealRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Meal saved", mealService.log(userId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Meal>>> history(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("Meals loaded", mealService.history(userId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        mealService.delete(userId, id);
        return ResponseEntity.ok(ApiResponse.ok("Meal removed", null));
    }
}
