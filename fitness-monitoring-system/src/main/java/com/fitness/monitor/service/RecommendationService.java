package com.fitness.monitor.service;

import com.fitness.monitor.dto.ExerciseItem;
import com.fitness.monitor.dto.FoodItem;
import com.fitness.monitor.model.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private static final Map<String, List<String>> GOAL_EXERCISES = Map.of(
            "Weight Loss", List.of("running", "cycling", "jumping-jacks", "burpees", "jogging", "mountain-climbers", "walking", "squats"),
            "Weight Gain", List.of("bench-press", "squats", "leg-press", "shoulder-press", "bicep-curl", "tricep-extension", "push-ups", "lunges"),
            "Muscle Gain", List.of("bench-press", "squats", "shoulder-press", "leg-press", "bicep-curl", "tricep-extension", "push-ups", "plank"),
            "Maintenance", List.of("jogging", "squats", "push-ups", "plank", "cycling", "lunges", "yoga", "walking"),
            "General Fitness", List.of("walking", "squats", "push-ups", "plank", "jogging", "yoga", "lunges", "cycling"));

    private static final Map<String, Double> MEAL_SHARE = new LinkedHashMap<>();
    private static final Map<String, List<String>> MEAL_CATEGORIES = new LinkedHashMap<>();

    static {
        MEAL_SHARE.put("Breakfast", 0.25);
        MEAL_SHARE.put("Lunch", 0.35);
        MEAL_SHARE.put("Snack", 0.10);
        MEAL_SHARE.put("Dinner", 0.30);
        MEAL_CATEGORIES.put("Breakfast", List.of("Breakfast", "Fruits"));
        MEAL_CATEGORIES.put("Lunch", List.of("Protein", "Grains", "Vegetables"));
        MEAL_CATEGORIES.put("Snack", List.of("Snacks", "Fruits"));
        MEAL_CATEGORIES.put("Dinner", List.of("Dinner", "Protein", "Vegetables"));
    }

    private final ProfileService profileService;
    private final CatalogService catalog;

    public RecommendationService(ProfileService profileService, CatalogService catalog) {
        this.profileService = profileService;
        this.catalog = catalog;
    }

    public Map<String, Object> dietPlan(Long userId) {
        Profile profile = profileService.get(userId);
        double target = profile.getCalorieTarget();
        List<FoodItem> allowed = catalog.foods().stream()
                .filter(f -> isAllowed(profile.getDietaryPreference(), f.diet()))
                .toList();
        int rotation = LocalDate.now().getDayOfYear();

        List<Map<String, Object>> meals = new ArrayList<>();
        double planCalories = 0;
        for (Map.Entry<String, Double> slot : MEAL_SHARE.entrySet()) {
            double budget = target * slot.getValue();
            List<FoodItem> picks = new ArrayList<>();
            for (String category : MEAL_CATEGORIES.get(slot.getKey())) {
                List<FoodItem> options = allowed.stream()
                        .filter(f -> f.category().equals(category))
                        .sorted(Comparator.comparingDouble((FoodItem f) -> f.protein() / Math.max(f.calories(), 1)).reversed())
                        .collect(Collectors.toList());
                if (!options.isEmpty() && picks.size() < 3) {
                    picks.add(options.get(rotation % options.size()));
                }
            }
            double perItem = picks.isEmpty() ? 0 : budget / picks.size();
            List<Map<String, Object>> items = new ArrayList<>();
            double slotCalories = 0;
            for (FoodItem food : picks) {
                double servings = Math.round(perItem / food.calories() * 2.0) / 2.0;
                servings = Math.max(0.5, Math.min(3.0, servings));
                double calories = NutritionCalculator.round(food.calories() * servings);
                slotCalories += calories;
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("foodId", food.id());
                item.put("name", food.name());
                item.put("icon", food.icon());
                item.put("serving", food.serving());
                item.put("servings", servings);
                item.put("calories", calories);
                item.put("protein", NutritionCalculator.round(food.protein() * servings));
                items.add(item);
            }
            planCalories += slotCalories;
            Map<String, Object> meal = new LinkedHashMap<>();
            meal.put("mealType", slot.getKey());
            meal.put("budget", NutritionCalculator.round(budget));
            meal.put("calories", NutritionCalculator.round(slotCalories));
            meal.put("items", items);
            meals.add(meal);
        }

        Map<String, Object> plan = new LinkedHashMap<>();
        plan.put("calorieTarget", target);
        plan.put("planCalories", NutritionCalculator.round(planCalories));
        plan.put("dietaryPreference", profile.getDietaryPreference());
        plan.put("goal", profile.getFitnessGoal());
        plan.put("meals", meals);
        return plan;
    }

    public Map<String, Object> workoutPlan(Long userId) {
        Profile profile = profileService.get(userId);
        List<String> pool = GOAL_EXERCISES.getOrDefault(profile.getFitnessGoal(), GOAL_EXERCISES.get("General Fitness"));
        int sessions = Math.max(1, Math.min(7, profile.getWeeklyWorkoutGoal()));
        String intensity = switch (profile.getActivityLevel() == null ? "" : profile.getActivityLevel()) {
            case "Sedentary" -> "Low";
            case "Very Active" -> "High";
            default -> "Medium";
        };

        Set<Integer> trainingDays = new HashSet<>();
        for (int i = 0; i < sessions; i++) {
            trainingDays.add((int) Math.floor(i * 7.0 / sessions));
        }

        List<Map<String, Object>> days = new ArrayList<>();
        int cursor = 0;
        double weeklyCalories = 0;
        for (int d = 0; d < 7; d++) {
            Map<String, Object> day = new LinkedHashMap<>();
            day.put("day", DAYS[d]);
            List<Map<String, Object>> items = new ArrayList<>();
            if (trainingDays.contains(d)) {
                for (int k = 0; k < 3; k++) {
                    ExerciseItem exercise = catalog.exercise(pool.get((cursor + k) % pool.size()));
                    int minutes = Math.max(10, Math.min(30, exercise.recommendedMin()));
                    double calories = NutritionCalculator.workoutCalories(exercise.met(), profile.getWeightKg(), minutes, intensity);
                    weeklyCalories += calories;
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("exerciseId", exercise.id());
                    item.put("name", exercise.name());
                    item.put("icon", exercise.icon());
                    item.put("minutes", minutes);
                    item.put("intensity", intensity);
                    item.put("calories", calories);
                    items.add(item);
                }
                cursor += 3;
            }
            day.put("rest", items.isEmpty());
            day.put("items", items);
            days.add(day);
        }

        Map<String, Object> plan = new LinkedHashMap<>();
        plan.put("goal", profile.getFitnessGoal());
        plan.put("sessionsPerWeek", sessions);
        plan.put("estimatedWeeklyCalories", NutritionCalculator.round(weeklyCalories));
        plan.put("days", days);
        return plan;
    }

    private static boolean isAllowed(String preference, String diet) {
        if ("Vegetarian".equals(preference)) return "veg".equals(diet);
        if ("Eggetarian".equals(preference)) return !"nonveg".equals(diet);
        return true;
    }
}
