package com.fitness.monitor.controller;

import com.fitness.monitor.dto.ApiResponse;
import com.fitness.monitor.dto.WorkoutRequest;
import com.fitness.monitor.model.Workout;
import com.fitness.monitor.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Workout>> log(@RequestAttribute("userId") Long userId,
                                                     @Valid @RequestBody WorkoutRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Workout saved", workoutService.log(userId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Workout>>> history(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("Workouts loaded", workoutService.history(userId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        workoutService.delete(userId, id);
        return ResponseEntity.ok(ApiResponse.ok("Workout removed", null));
    }
}
