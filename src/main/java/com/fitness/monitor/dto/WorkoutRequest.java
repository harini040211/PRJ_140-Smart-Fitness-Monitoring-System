package com.fitness.monitor.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class WorkoutRequest {

    @NotBlank(message = "Please choose an exercise")
    private String exerciseId;

    @Pattern(regexp = "Low|Medium|High", message = "Intensity must be Low, Medium or High")
    private String intensity = "Medium";

    @Min(value = 1, message = "Duration must be between 1 and 600 minutes")
    @Max(value = 600, message = "Duration must be between 1 and 600 minutes")
    private int durationMinutes;

    private LocalDate date;

    public String getExerciseId() { return exerciseId; }
    public void setExerciseId(String exerciseId) { this.exerciseId = exerciseId; }
    public String getIntensity() { return intensity; }
    public void setIntensity(String intensity) { this.intensity = intensity; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
