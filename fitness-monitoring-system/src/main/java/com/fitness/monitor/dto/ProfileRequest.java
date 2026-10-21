package com.fitness.monitor.dto;

import jakarta.validation.constraints.*;

public class ProfileRequest {

    @Min(value = 10, message = "Age must be between 10 and 100")
    @Max(value = 100, message = "Age must be between 10 and 100")
    private int age;

    @NotBlank(message = "Please select your gender")
    private String gender;

    @DecimalMin(value = "100", message = "Height must be between 100 and 250 cm")
    @DecimalMax(value = "250", message = "Height must be between 100 and 250 cm")
    private double heightCm;

    @DecimalMin(value = "25", message = "Weight must be between 25 and 300 kg")
    @DecimalMax(value = "300", message = "Weight must be between 25 and 300 kg")
    private double weightKg;

    @NotBlank(message = "Please select your activity level")
    private String activityLevel;

    @NotBlank(message = "Please select a fitness goal")
    private String fitnessGoal;

    private String dietaryPreference;

    @DecimalMin(value = "25", message = "Target weight must be between 25 and 300 kg")
    @DecimalMax(value = "300", message = "Target weight must be between 25 and 300 kg")
    private Double targetWeightKg;

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public double getHeightCm() { return heightCm; }
    public void setHeightCm(double heightCm) { this.heightCm = heightCm; }
    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    public String getActivityLevel() { return activityLevel; }
    public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }
    public String getFitnessGoal() { return fitnessGoal; }
    public void setFitnessGoal(String fitnessGoal) { this.fitnessGoal = fitnessGoal; }
    public String getDietaryPreference() { return dietaryPreference; }
    public void setDietaryPreference(String dietaryPreference) { this.dietaryPreference = dietaryPreference; }
    public Double getTargetWeightKg() { return targetWeightKg; }
    public void setTargetWeightKg(Double targetWeightKg) { this.targetWeightKg = targetWeightKg; }
}
