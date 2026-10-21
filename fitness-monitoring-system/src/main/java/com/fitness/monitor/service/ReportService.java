package com.fitness.monitor.service;

import com.fitness.monitor.model.Meal;
import com.fitness.monitor.model.Profile;
import com.fitness.monitor.model.User;
import com.fitness.monitor.model.WeightRecord;
import com.fitness.monitor.model.Workout;
import com.fitness.monitor.repository.MealRepository;
import com.fitness.monitor.repository.WeightRecordRepository;
import com.fitness.monitor.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final UserService userService;
    private final ProfileService profileService;
    private final AnalyticsService analytics;
    private final WorkoutRepository workoutRepository;
    private final MealRepository mealRepository;
    private final WeightRecordRepository weightRepository;

    public ReportService(UserService userService, ProfileService profileService, AnalyticsService analytics,
                         WorkoutRepository workoutRepository, MealRepository mealRepository,
                         WeightRecordRepository weightRepository) {
        this.userService = userService;
        this.profileService = profileService;
        this.analytics = analytics;
        this.workoutRepository = workoutRepository;
        this.mealRepository = mealRepository;
        this.weightRepository = weightRepository;
    }

    public Map<String, Object> weekly(Long userId, int weeksAgo) {
        User user = userService.findById(userId);
        Profile profile = profileService.get(userId);
        LocalDate thisMonday = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1L);
        LocalDate from = thisMonday.minusWeeks(Math.max(0, weeksAgo));
        LocalDate to = from.plusDays(6);
        List<Map<String, Object>> days = analytics.range(userId, from, to);

        double burned = sum(days, "caloriesBurned");
        double eaten = sum(days, "caloriesEaten");
        int loggedFoodDays = (int) days.stream().filter(d -> (double) d.get("caloriesEaten") > 0).count();
        int workoutDays = (int) days.stream().filter(d -> (int) d.get("workouts") > 0).count();

        List<Workout> weekWorkouts = workoutRepository.findByUserIdOrderByDateDescIdDesc(userId).stream()
                .filter(w -> !w.getDate().isBefore(from) && !w.getDate().isAfter(to)).toList();
        Map<String, Integer> minutesByExercise = weekWorkouts.stream()
                .collect(Collectors.groupingBy(Workout::getExerciseType, Collectors.summingInt(Workout::getDurationMinutes)));
        String topExercise = minutesByExercise.entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);

        List<WeightRecord> weights = weightRepository.findByUserIdOrderByDateAscIdAsc(userId).stream()
                .filter(w -> !w.getDate().isBefore(from) && !w.getDate().isAfter(to)).toList();

        Map<String, Object> totals = new LinkedHashMap<>();
        totals.put("workouts", weekWorkouts.size());
        totals.put("workoutDays", workoutDays);
        totals.put("workoutMinutes", (int) sum(days, "workoutMinutes"));
        totals.put("caloriesBurned", NutritionCalculator.round(burned));
        totals.put("caloriesEaten", NutritionCalculator.round(eaten));
        totals.put("averageCaloriesEaten", loggedFoodDays == 0 ? 0 : NutritionCalculator.round(eaten / loggedFoodDays));
        totals.put("averageWaterMl", Math.round(sum(days, "waterMl") / 7));
        totals.put("totalSteps", (int) sum(days, "steps"));
        totals.put("topExercise", topExercise);
        totals.put("goalMet", workoutDays >= profile.getWeeklyWorkoutGoal());

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("name", user.getFullName());
        report.put("email", user.getEmail());
        report.put("from", from.toString());
        report.put("to", to.toString());
        report.put("generatedOn", LocalDate.now().toString());
        report.put("calorieTarget", profile.getCalorieTarget());
        report.put("workoutGoal", profile.getWeeklyWorkoutGoal());
        report.put("waterGoalMl", profile.getDailyWaterGoalMl());
        report.put("stepGoal", profile.getDailyStepGoal());
        report.put("bmi", profile.getBmi());
        report.put("bmiCategory", profile.getBmiCategory());
        report.put("fitnessGoal", profile.getFitnessGoal());
        report.put("weightStart", weights.isEmpty() ? null : weights.get(0).getWeightKg());
        report.put("weightEnd", weights.isEmpty() ? null : weights.get(weights.size() - 1).getWeightKg());
        report.put("days", days);
        report.put("totals", totals);
        return report;
    }

    public String csv(Long userId, String type) {
        StringBuilder out = new StringBuilder();
        switch (type) {
            case "workouts" -> {
                out.append("Date,Exercise,Intensity,Duration (min),Calories burned\n");
                for (Workout w : workoutRepository.findByUserIdOrderByDateDescIdDesc(userId)) {
                    row(out, w.getDate(), w.getExerciseType(), w.getIntensity(), w.getDurationMinutes(), w.getCaloriesBurned());
                }
            }
            case "meals" -> {
                out.append("Date,Meal,Food,Servings,Calories,Protein (g),Carbs (g),Fat (g)\n");
                for (Meal m : mealRepository.findByUserIdOrderByDateDescIdDesc(userId)) {
                    row(out, m.getDate(), m.getMealType(), m.getFoodName(), m.getServings(), m.getCalories(),
                            m.getProtein(), m.getCarbs(), m.getFat());
                }
            }
            case "weight" -> {
                out.append("Date,Weight (kg)\n");
                for (WeightRecord w : weightRepository.findByUserIdOrderByDateAscIdAsc(userId)) {
                    row(out, w.getDate(), w.getWeightKg());
                }
            }
            default -> throw new IllegalArgumentException("Choose workouts, meals or weight to export");
        }
        return out.toString();
    }

    private static void row(StringBuilder out, Object... cells) {
        for (int i = 0; i < cells.length; i++) {
            if (i > 0) out.append(',');
            String value = String.valueOf(cells[i]);
            if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                value = "\"" + value.replace("\"", "\"\"") + "\"";
            }
            out.append(value);
        }
        out.append('\n');
    }

    private static double sum(List<Map<String, Object>> days, String key) {
        return days.stream().mapToDouble(d -> ((Number) d.get(key)).doubleValue()).sum();
    }
}
