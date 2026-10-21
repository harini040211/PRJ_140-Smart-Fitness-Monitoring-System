package com.fitness.monitor.dto;

public record FoodItem(String id, String name, String icon, String category, String serving,
                       double calories, double protein, double carbs, double fat, String diet) {
}
