package com.fitness.monitor.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class MealRequest {

    @NotBlank(message = "Please choose a food")
    private String foodId;

    @Pattern(regexp = "Breakfast|Lunch|Dinner|Snack", message = "Meal type must be Breakfast, Lunch, Dinner or Snack")
    private String mealType;

    @DecimalMin(value = "0.25", message = "Servings must be between 0.25 and 20")
    @DecimalMax(value = "20", message = "Servings must be between 0.25 and 20")
    private double servings = 1;

    private LocalDate date;

    public String getFoodId() { return foodId; }
    public void setFoodId(String foodId) { this.foodId = foodId; }
    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    public double getServings() { return servings; }
    public void setServings(double servings) { this.servings = servings; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
