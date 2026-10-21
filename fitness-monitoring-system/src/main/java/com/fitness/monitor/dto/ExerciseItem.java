package com.fitness.monitor.dto;

public record ExerciseItem(String id, String name, String icon, String muscle, String difficulty,
                           String equipment, double met, int recommendedMin) {
}
