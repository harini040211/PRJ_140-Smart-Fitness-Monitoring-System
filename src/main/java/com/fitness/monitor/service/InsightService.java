package com.fitness.monitor.service;

import com.fitness.monitor.model.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class InsightService {

    public List<Map<String, String>> build(Profile profile, Map<String, Object> s) {
        List<Map<String, String>> out = new ArrayList<>();

        double eaten = num(s.get("caloriesEatenToday"));
        double burned = num(s.get("caloriesBurnedToday"));
        double target = profile.getCalorieTarget();
        double protein = num(s.get("proteinToday"));
        double proteinTarget = num(s.get("proteinTargetG"));
        int water = (int) num(s.get("waterTodayMl"));
        int waterGoal = profile.getDailyWaterGoalMl();
        int weekCount = (int) num(s.get("weeklyWorkoutCount"));
        int weekGoal = profile.getWeeklyWorkoutGoal();
        int daysLeft = 7 - LocalDate.now().getDayOfWeek().getValue();
        @SuppressWarnings("unchecked")
        Map<String, Integer> streaks = (Map<String, Integer>) s.get("streaks");

        if (eaten == 0) {
            add(out, "tip", "Nothing logged yet today",
                    "Log your first meal so your calorie balance and macros stay accurate.");
        } else if (eaten > target + burned + 100) {
            add(out, "warn", "Over today's target",
                    "You are " + Math.round(eaten - target - burned) + " kcal above your target for today. A walk or a lighter dinner would balance it.");
        } else if (eaten < target * 0.5 && burned > 0) {
            add(out, "warn", "Fuel up",
                    "You have trained today but eaten less than half your target. Add a proper meal to support recovery.");
        } else {
            add(out, "good", "Calories on track",
                    "You have " + Math.round(target - eaten + burned) + " kcal left in today's budget.");
        }

        if (eaten > 0 && proteinTarget > 0 && protein < proteinTarget * 0.6) {
            add(out, "tip", "Protein is low",
                    "You have had " + Math.round(protein) + " g of your " + Math.round(proteinTarget)
                            + " g protein target. Eggs, dal, paneer, curd or chicken will help close the gap.");
        }

        if (water < waterGoal * 0.5) {
            add(out, "tip", "Drink more water",
                    "You are at " + water + " ml of " + waterGoal + " ml. Have a glass now and keep a bottle nearby.");
        } else if (water >= waterGoal) {
            add(out, "good", "Hydration goal met", "You reached your " + waterGoal + " ml water goal today.");
        }

        if (weekCount >= weekGoal) {
            add(out, "good", "Weekly workout goal reached",
                    "That is " + weekCount + " sessions this week against a goal of " + weekGoal + ".");
        } else if (weekGoal - weekCount > daysLeft + 1) {
            add(out, "warn", "Weekly goal at risk",
                    "You need " + (weekGoal - weekCount) + " more sessions with only " + (daysLeft + 1)
                            + " days left. Consider lowering the goal or adding short sessions.");
        } else {
            add(out, "tip", "Keep the momentum",
                    (weekGoal - weekCount) + " more workout" + (weekGoal - weekCount == 1 ? "" : "s")
                            + " to hit this week's goal.");
        }

        int workoutStreak = streaks == null ? 0 : streaks.getOrDefault("workout", 0);
        if (workoutStreak >= 3) {
            add(out, "good", workoutStreak + "-day workout streak", "Nice consistency. Keep it going.");
        }

        Double progress = (Double) s.get("weightProgressPct");
        if (progress != null && profile.getTargetWeightKg() != null) {
            add(out, progress >= 50 ? "good" : "tip", "Weight goal progress",
                    "You are " + Math.round(progress) + "% of the way from your starting weight to "
                            + profile.getTargetWeightKg() + " kg.");
        }

        if ("Overweight".equals(profile.getBmiCategory()) || "Obese".equals(profile.getBmiCategory())) {
            add(out, "tip", "BMI note",
                    "Your BMI is " + profile.getBmi() + ". Regular cardio plus a modest calorie deficit is the usual starting point.");
        } else if ("Underweight".equals(profile.getBmiCategory())) {
            add(out, "tip", "BMI note",
                    "Your BMI is " + profile.getBmi() + ". Focus on strength training and enough calories and protein.");
        }
        return out;
    }

    private static void add(List<Map<String, String>> list, String tone, String title, String text) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("tone", tone);
        m.put("title", title);
        m.put("text", text);
        list.add(m);
    }

    private static double num(Object value) {
        return value instanceof Number n ? n.doubleValue() : 0;
    }
}
