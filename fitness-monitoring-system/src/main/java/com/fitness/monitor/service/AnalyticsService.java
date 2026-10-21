package com.fitness.monitor.service;

import com.fitness.monitor.model.ActivityLog;
import com.fitness.monitor.model.Meal;
import com.fitness.monitor.model.WaterLog;
import com.fitness.monitor.model.Workout;
import com.fitness.monitor.repository.ActivityLogRepository;
import com.fitness.monitor.repository.MealRepository;
import com.fitness.monitor.repository.WaterLogRepository;
import com.fitness.monitor.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final WorkoutRepository workouts;
    private final MealRepository meals;
    private final WaterLogRepository water;
    private final ActivityLogRepository activity;

    public AnalyticsService(WorkoutRepository workouts, MealRepository meals,
                            WaterLogRepository water, ActivityLogRepository activity) {
        this.workouts = workouts;
        this.meals = meals;
        this.water = water;
        this.activity = activity;
    }

    public List<Map<String, Object>> lastDays(Long userId, int days) {
        LocalDate today = LocalDate.now();
        return range(userId, today.minusDays(days - 1L), today);
    }

    public List<Map<String, Object>> range(Long userId, LocalDate from, LocalDate to) {
        Map<LocalDate, List<Workout>> workoutsByDay = workouts.findByUserIdOrderByDateDescIdDesc(userId).stream()
                .collect(Collectors.groupingBy(Workout::getDate));
        Map<LocalDate, List<Meal>> mealsByDay = meals.findByUserIdOrderByDateDescIdDesc(userId).stream()
                .collect(Collectors.groupingBy(Meal::getDate));
        Map<LocalDate, Integer> waterByDay = water.findByUserIdOrderByDateDesc(userId).stream()
                .collect(Collectors.groupingBy(WaterLog::getDate, Collectors.summingInt(WaterLog::getAmountMl)));
        Map<LocalDate, ActivityLog> activityByDay = activity.findByUserIdOrderByDateDesc(userId).stream()
                .collect(Collectors.toMap(ActivityLog::getDate, a -> a, (a, b) -> a));

        List<Map<String, Object>> rows = new ArrayList<>();
        for (LocalDate day = from; !day.isAfter(to); day = day.plusDays(1)) {
            List<Workout> dayWorkouts = workoutsByDay.getOrDefault(day, List.of());
            List<Meal> dayMeals = mealsByDay.getOrDefault(day, List.of());
            ActivityLog act = activityByDay.get(day);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", day.toString());
            row.put("workouts", dayWorkouts.size());
            row.put("workoutMinutes", dayWorkouts.stream().mapToInt(Workout::getDurationMinutes).sum());
            row.put("caloriesBurned", NutritionCalculator.round(dayWorkouts.stream().mapToDouble(Workout::getCaloriesBurned).sum()));
            row.put("caloriesEaten", NutritionCalculator.round(dayMeals.stream().mapToDouble(Meal::getCalories).sum()));
            row.put("protein", NutritionCalculator.round(dayMeals.stream().mapToDouble(Meal::getProtein).sum()));
            row.put("carbs", NutritionCalculator.round(dayMeals.stream().mapToDouble(Meal::getCarbs).sum()));
            row.put("fat", NutritionCalculator.round(dayMeals.stream().mapToDouble(Meal::getFat).sum()));
            row.put("waterMl", waterByDay.getOrDefault(day, 0));
            row.put("steps", act == null ? 0 : act.getSteps());
            row.put("activeMinutes", act == null ? 0 : act.getActiveMinutes());
            rows.add(row);
        }
        return rows;
    }

    public Map<String, Object> breakdowns(Long userId, int days) {
        LocalDate from = LocalDate.now().minusDays(days - 1L);
        List<Workout> recent = workouts.findByUserIdOrderByDateDescIdDesc(userId).stream()
                .filter(w -> !w.getDate().isBefore(from)).toList();

        Map<String, Double> byExercise = new LinkedHashMap<>();
        Map<String, Integer> byIntensity = new LinkedHashMap<>();
        byIntensity.put("Low", 0);
        byIntensity.put("Medium", 0);
        byIntensity.put("High", 0);
        for (Workout w : recent) {
            byExercise.merge(w.getExerciseType(), w.getCaloriesBurned(), Double::sum);
            byIntensity.merge(w.getIntensity(), 1, Integer::sum);
        }
        List<Map<String, Object>> exerciseRows = byExercise.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(e -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("exercise", e.getKey());
                    row.put("calories", NutritionCalculator.round(e.getValue()));
                    return row;
                }).toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("byExercise", exerciseRows);
        result.put("byIntensity", byIntensity);
        return result;
    }
}
