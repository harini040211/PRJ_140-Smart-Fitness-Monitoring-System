package com.fitness.monitor.service;

import com.fitness.monitor.model.Meal;
import com.fitness.monitor.model.Profile;
import com.fitness.monitor.model.WaterLog;
import com.fitness.monitor.model.WeightRecord;
import com.fitness.monitor.model.Workout;
import com.fitness.monitor.repository.MealRepository;
import com.fitness.monitor.repository.WaterLogRepository;
import com.fitness.monitor.repository.WeightRecordRepository;
import com.fitness.monitor.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ProfileService profileService;
    private final AnalyticsService analytics;
    private final InsightService insightService;
    private final WorkoutRepository workoutRepository;
    private final MealRepository mealRepository;
    private final WeightRecordRepository weightRepository;
    private final WaterLogRepository waterRepository;

    public DashboardService(ProfileService profileService, AnalyticsService analytics, InsightService insightService,
                            WorkoutRepository workoutRepository, MealRepository mealRepository,
                            WeightRecordRepository weightRepository, WaterLogRepository waterRepository) {
        this.profileService = profileService;
        this.analytics = analytics;
        this.insightService = insightService;
        this.workoutRepository = workoutRepository;
        this.mealRepository = mealRepository;
        this.weightRepository = weightRepository;
        this.waterRepository = waterRepository;
    }

    public Map<String, Object> summary(Long userId) {
        Profile profile = profileService.get(userId);
        LocalDate today = LocalDate.now();
        List<Workout> workouts = workoutRepository.findByUserIdOrderByDateDescIdDesc(userId);
        List<Meal> meals = mealRepository.findByUserIdOrderByDateDescIdDesc(userId);
        List<WeightRecord> weights = weightRepository.findByUserIdOrderByDateAscIdAsc(userId);
        List<WaterLog> waterLogs = waterRepository.findByUserIdOrderByDateDesc(userId);

        List<Map<String, Object>> week = analytics.lastDays(userId, 7);
        Map<String, Object> todayRow = week.get(week.size() - 1);

        double burnedToday = (double) todayRow.get("caloriesBurned");
        double eatenToday = (double) todayRow.get("caloriesEaten");
        double target = profile.getCalorieTarget();

        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1L);
        List<Workout> thisWeek = workouts.stream().filter(w -> !w.getDate().isBefore(weekStart)).toList();
        long activeDaysThisWeek = thisWeek.stream().map(Workout::getDate).distinct().count();

        Map<LocalDate, Integer> waterByDay = waterLogs.stream()
                .collect(Collectors.groupingBy(WaterLog::getDate, Collectors.summingInt(WaterLog::getAmountMl)));
        Set<LocalDate> waterGoalDays = waterByDay.entrySet().stream()
                .filter(e -> e.getValue() >= profile.getDailyWaterGoalMl())
                .map(Map.Entry::getKey).collect(Collectors.toSet());

        int workoutStreak = streak(workouts.stream().map(Workout::getDate).collect(Collectors.toSet()));
        int mealStreak = streak(meals.stream().map(Meal::getDate).collect(Collectors.toSet()));
        int waterStreak = streak(waterGoalDays);

        Double startingWeight = weights.isEmpty() ? null : weights.get(0).getWeightKg();
        double currentWeight = weights.isEmpty() ? profile.getWeightKg() : weights.get(weights.size() - 1).getWeightKg();
        Double weightProgress = null;
        if (startingWeight != null && profile.getTargetWeightKg() != null
                && Math.abs(startingWeight - profile.getTargetWeightKg()) > 0.05) {
            double total = startingWeight - profile.getTargetWeightKg();
            double done = startingWeight - currentWeight;
            weightProgress = Math.max(0, Math.min(100, NutritionCalculator.round(done / total * 100)));
        }

        List<Map<String, Object>> weightHistory = weights.stream()
                .map(w -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("date", w.getDate().toString());
                    row.put("weightKg", w.getWeightKg());
                    return row;
                }).toList();

        double totalBurned = workouts.stream().mapToDouble(Workout::getCaloriesBurned).sum();
        int stepsToday = (int) todayRow.get("steps");

        Map<String, Object> s = new LinkedHashMap<>();
        s.put("bmi", profile.getBmi());
        s.put("bmiCategory", profile.getBmiCategory());
        s.put("bmr", profile.getBmr());
        s.put("tdee", profile.getTdee());
        s.put("calorieTarget", target);
        s.put("caloriesBurnedToday", burnedToday);
        s.put("caloriesEatenToday", eatenToday);
        s.put("remainingCalories", NutritionCalculator.round(target - eatenToday + burnedToday));
        s.put("proteinToday", todayRow.get("protein"));
        s.put("carbsToday", todayRow.get("carbs"));
        s.put("fatToday", todayRow.get("fat"));
        s.put("proteinTargetG", NutritionCalculator.round(target * 0.30 / 4.0));
        s.put("carbsTargetG", NutritionCalculator.round(target * 0.45 / 4.0));
        s.put("fatTargetG", NutritionCalculator.round(target * 0.25 / 9.0));
        s.put("workoutMinutesToday", todayRow.get("workoutMinutes"));
        s.put("stepsToday", stepsToday);
        s.put("stepGoal", profile.getDailyStepGoal());
        s.put("activeMinutesToday", todayRow.get("activeMinutes"));
        s.put("waterTodayMl", todayRow.get("waterMl"));
        s.put("waterGoalMl", profile.getDailyWaterGoalMl());
        s.put("weeklyWorkoutGoal", profile.getWeeklyWorkoutGoal());
        s.put("weeklyWorkoutCount", thisWeek.size());
        s.put("weeklyActiveDays", activeDaysThisWeek);
        s.put("weeklyMinutes", thisWeek.stream().mapToInt(Workout::getDurationMinutes).sum());
        s.put("weeklyCaloriesBurned", NutritionCalculator.round(thisWeek.stream().mapToDouble(Workout::getCaloriesBurned).sum()));
        s.put("totalWorkouts", workouts.size());
        s.put("totalMeals", meals.size());
        s.put("startingWeight", startingWeight);
        s.put("currentWeight", currentWeight);
        s.put("targetWeight", profile.getTargetWeightKg());
        s.put("weightProgressPct", weightProgress);
        s.put("weightHistory", weightHistory);
        s.put("week", week);
        s.put("recentWorkouts", workouts.stream().limit(4).toList());
        s.put("recentMeals", meals.stream().limit(4).toList());
        s.put("streaks", Map.of("workout", workoutStreak, "meals", mealStreak, "water", waterStreak));
        s.put("achievements", achievements(workouts.size(), meals.size(), totalBurned, workoutStreak,
                waterStreak, weightProgress, stepsToday >= profile.getDailyStepGoal(), weights.size()));
        s.put("insights", insightService.build(profile, s));
        return s;
    }

    static int streak(Set<LocalDate> days) {
        LocalDate cursor = LocalDate.now();
        if (!days.contains(cursor)) {
            cursor = cursor.minusDays(1);
        }
        int count = 0;
        while (days.contains(cursor)) {
            count++;
            cursor = cursor.minusDays(1);
        }
        return count;
    }

    private List<Map<String, Object>> achievements(int workouts, int meals, double burned, int workoutStreak,
                                                   int waterStreak, Double weightProgress, boolean stepGoalHit,
                                                   int weighIns) {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(badge("first-workout", "First Workout", "Log your first workout", workouts >= 1));
        list.add(badge("five-workouts", "Getting Consistent", "Log 5 workouts", workouts >= 5));
        list.add(badge("twenty-five-workouts", "Regular", "Log 25 workouts", workouts >= 25));
        list.add(badge("thousand-kcal", "1,000 kcal Burned", "Burn 1,000 kcal through workouts", burned >= 1000));
        list.add(badge("streak-3", "3-Day Streak", "Work out three days in a row", workoutStreak >= 3));
        list.add(badge("streak-7", "Week Warrior", "Work out seven days in a row", workoutStreak >= 7));
        list.add(badge("first-meal", "First Meal Logged", "Log your first meal", meals >= 1));
        list.add(badge("ten-meals", "Mindful Eater", "Log 10 meals", meals >= 10));
        list.add(badge("hydration", "Hydration Habit", "Hit your water goal three days in a row", waterStreak >= 3));
        list.add(badge("step-goal", "Step Goal Reached", "Reach your daily step goal", stepGoalHit));
        list.add(badge("weigh-in", "Tracking Progress", "Record three weigh-ins", weighIns >= 3));
        list.add(badge("halfway", "Halfway There", "Get halfway to your target weight",
                weightProgress != null && weightProgress >= 50));
        return list;
    }

    private Map<String, Object> badge(String key, String name, String description, boolean unlocked) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("key", key);
        m.put("name", name);
        m.put("description", description);
        m.put("unlocked", unlocked);
        return m;
    }
}
