package com.fitness.monitor.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class GoalsRequest {

    @Min(value = 1, message = "Weekly workouts must be between 1 and 14")
    @Max(value = 14, message = "Weekly workouts must be between 1 and 14")
    private int weeklyWorkoutGoal;

    @Min(value = 500, message = "Water goal must be between 500 and 6000 ml")
    @Max(value = 6000, message = "Water goal must be between 500 and 6000 ml")
    private int dailyWaterGoalMl;

    @Min(value = 1000, message = "Step goal must be between 1,000 and 50,000")
    @Max(value = 50000, message = "Step goal must be between 1,000 and 50,000")
    private int dailyStepGoal;

    @DecimalMin(value = "25", message = "Target weight must be between 25 and 300 kg")
    @DecimalMax(value = "300", message = "Target weight must be between 25 and 300 kg")
    private Double targetWeightKg;

    public int getWeeklyWorkoutGoal() { return weeklyWorkoutGoal; }
    public void setWeeklyWorkoutGoal(int weeklyWorkoutGoal) { this.weeklyWorkoutGoal = weeklyWorkoutGoal; }
    public int getDailyWaterGoalMl() { return dailyWaterGoalMl; }
    public void setDailyWaterGoalMl(int dailyWaterGoalMl) { this.dailyWaterGoalMl = dailyWaterGoalMl; }
    public int getDailyStepGoal() { return dailyStepGoal; }
    public void setDailyStepGoal(int dailyStepGoal) { this.dailyStepGoal = dailyStepGoal; }
    public Double getTargetWeightKg() { return targetWeightKg; }
    public void setTargetWeightKg(Double targetWeightKg) { this.targetWeightKg = targetWeightKg; }
}
