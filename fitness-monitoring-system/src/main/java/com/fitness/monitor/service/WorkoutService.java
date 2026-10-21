package com.fitness.monitor.service;

import com.fitness.monitor.dto.ExerciseItem;
import com.fitness.monitor.dto.WorkoutRequest;
import com.fitness.monitor.model.Profile;
import com.fitness.monitor.model.Workout;
import com.fitness.monitor.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final ProfileService profileService;
    private final CatalogService catalog;

    public WorkoutService(WorkoutRepository workoutRepository, ProfileService profileService, CatalogService catalog) {
        this.workoutRepository = workoutRepository;
        this.profileService = profileService;
        this.catalog = catalog;
    }

    public Workout log(Long userId, WorkoutRequest request) {
        Profile profile = profileService.get(userId);
        ExerciseItem exercise = catalog.exercise(request.getExerciseId());
        Workout workout = new Workout(userId, exercise.name(), request.getIntensity(), request.getDurationMinutes());
        workout.setCaloriesBurned(NutritionCalculator.workoutCalories(
                exercise.met(), profile.getWeightKg(), request.getDurationMinutes(), request.getIntensity()));
        workout.setDate(DateRules.resolve(request.getDate()));
        return workoutRepository.save(workout);
    }

    public List<Workout> history(Long userId) {
        return workoutRepository.findByUserIdOrderByDateDescIdDesc(userId);
    }

    public void delete(Long userId, Long workoutId) {
        Workout workout = workoutRepository.findById(workoutId)
                .filter(w -> w.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("That workout could not be found"));
        workoutRepository.delete(workout);
    }
}
