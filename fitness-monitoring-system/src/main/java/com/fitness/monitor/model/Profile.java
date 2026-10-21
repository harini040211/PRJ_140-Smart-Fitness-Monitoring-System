package com.fitness.monitor.model;

import jakarta.persistence.*;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long userId;

    private int age;
    private String gender;
    private double heightCm;
    private double weightKg;
    private String activityLevel;
    private String fitnessGoal;
    private String dietaryPreference = "Non-Vegetarian";
    private Double targetWeightKg;
    private int weeklyWorkoutGoal = 4;
    private int dailyWaterGoalMl = 2000;
    private int dailyStepGoal = 8000;

    private double bmi;
    private String bmiCategory;
    private double bmr;
    private double tdee;
    private double calorieTarget;

    public Profile() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
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
    public int getWeeklyWorkoutGoal() { return weeklyWorkoutGoal; }
    public void setWeeklyWorkoutGoal(int weeklyWorkoutGoal) { this.weeklyWorkoutGoal = weeklyWorkoutGoal; }
    public int getDailyWaterGoalMl() { return dailyWaterGoalMl; }
    public void setDailyWaterGoalMl(int dailyWaterGoalMl) { this.dailyWaterGoalMl = dailyWaterGoalMl; }
    public int getDailyStepGoal() { return dailyStepGoal; }
    public void setDailyStepGoal(int dailyStepGoal) { this.dailyStepGoal = dailyStepGoal; }
    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }
    public String getBmiCategory() { return bmiCategory; }
    public void setBmiCategory(String bmiCategory) { this.bmiCategory = bmiCategory; }
    public double getBmr() { return bmr; }
    public void setBmr(double bmr) { this.bmr = bmr; }
    public double getTdee() { return tdee; }
    public void setTdee(double tdee) { this.tdee = tdee; }
    public double getCalorieTarget() { return calorieTarget; }
    public void setCalorieTarget(double calorieTarget) { this.calorieTarget = calorieTarget; }
}
