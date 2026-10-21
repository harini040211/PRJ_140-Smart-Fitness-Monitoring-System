package com.fitness.monitor.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NutritionCalculatorTest {

    @Test
    void bmiIsWeightOverHeightSquared() {
        assertEquals(22.9, NutritionCalculator.bmi(70, 175));
    }

    @Test
    void bmiCategoriesFollowWhoRanges() {
        assertEquals("Underweight", NutritionCalculator.bmiCategory(18.4));
        assertEquals("Normal", NutritionCalculator.bmiCategory(22.9));
        assertEquals("Overweight", NutritionCalculator.bmiCategory(27.0));
        assertEquals("Obese", NutritionCalculator.bmiCategory(31.0));
    }

    @Test
    void bmrUsesMifflinStJeorForBothGenders() {
        assertEquals(1673.75, NutritionCalculator.bmr(70, 175, 25, "Male"), 0.001);
        assertEquals(1507.75, NutritionCalculator.bmr(70, 175, 25, "Female"), 0.001);
    }

    @Test
    void tdeeAppliesActivityMultiplier() {
        assertEquals(1500 * 1.55, NutritionCalculator.tdee(1500, "Moderately Active"), 0.001);
        assertEquals(1500 * 1.375, NutritionCalculator.tdee(1500, "Unknown"), 0.001);
    }

    @Test
    void calorieTargetFollowsTheGoal() {
        assertEquals(2000, NutritionCalculator.calorieTarget(2500, "Weight Loss"), 0.001);
        assertEquals(2900, NutritionCalculator.calorieTarget(2500, "Weight Gain"), 0.001);
        assertEquals(2750, NutritionCalculator.calorieTarget(2500, "Muscle Gain"), 0.001);
        assertEquals(2500, NutritionCalculator.calorieTarget(2500, "Maintenance"), 0.001);
    }

    @Test
    void weightLossTargetNeverDropsBelowTwelveHundred() {
        assertEquals(1200, NutritionCalculator.calorieTarget(1500, "Weight Loss"), 0.001);
    }

    @Test
    void workoutCaloriesUseMetWeightTimeAndIntensity() {
        assertEquals(560.0, NutritionCalculator.workoutCalories(8.0, 70, 60, "Medium"));
        assertEquals(672.0, NutritionCalculator.workoutCalories(8.0, 70, 60, "High"));
        assertEquals(476.0, NutritionCalculator.workoutCalories(8.0, 70, 60, "Low"));
    }
}
