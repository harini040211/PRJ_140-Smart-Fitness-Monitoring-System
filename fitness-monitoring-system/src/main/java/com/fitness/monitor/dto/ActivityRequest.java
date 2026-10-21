package com.fitness.monitor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

public class ActivityRequest {

    @Min(value = 0, message = "Steps must be between 0 and 100,000")
    @Max(value = 100000, message = "Steps must be between 0 and 100,000")
    private int steps;

    @Min(value = 0, message = "Active minutes must be between 0 and 1,440")
    @Max(value = 1440, message = "Active minutes must be between 0 and 1,440")
    private int activeMinutes;

    private LocalDate date;

    public int getSteps() { return steps; }
    public void setSteps(int steps) { this.steps = steps; }
    public int getActiveMinutes() { return activeMinutes; }
    public void setActiveMinutes(int activeMinutes) { this.activeMinutes = activeMinutes; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
