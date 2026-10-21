package com.fitness.monitor.service;

import java.util.Map;

public final class NutritionCalculator {

    private static final Map<String, Double> ACTIVITY_FACTOR = Map.of(
            "Sedentary", 1.2,
            "Lightly Active", 1.375,
            "Moderately Active", 1.55,
            "Very Active", 1.725);

    private static final Map<String, Double> INTENSITY_FACTOR = Map.of(
            "Low", 0.85,
            "Medium", 1.0,
            "High", 1.2);

    private NutritionCalculator() {}

    public static double bmi(double weightKg, double heightCm) {
        double heightM = heightCm / 100.0;
        return round(weightKg / (heightM * heightM));
    }

    public static String bmiCategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25) return "Normal";
        if (bmi < 30) return "Overweight";
        return "Obese";
    }

    public static double bmr(double weightKg, double heightCm, int age, String gender) {
        double base = (10 * weightKg) + (6.25 * heightCm) - (5.0 * age);
        return "Female".equalsIgnoreCase(gender) ? base - 161 : base + 5;
    }

    public static double tdee(double bmr, String activityLevel) {
        return bmr * ACTIVITY_FACTOR.getOrDefault(activityLevel, 1.375);
    }

    public static double calorieTarget(double tdee, String goal) {
        String g = goal == null ? "" : goal;
        return switch (g) {
            case "Weight Loss" -> Math.max(1200, tdee - 500);
            case "Weight Gain" -> tdee + 400;
            case "Muscle Gain" -> tdee + 250;
            default -> tdee;
        };
    }

    public static double workoutCalories(double met, double weightKg, int minutes, String intensity) {
        double factor = INTENSITY_FACTOR.getOrDefault(intensity, 1.0);
        return round(met * weightKg * (minutes / 60.0) * factor);
    }

    public static double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
