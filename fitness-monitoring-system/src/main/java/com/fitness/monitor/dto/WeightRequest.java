package com.fitness.monitor.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public class WeightRequest {

    @DecimalMin(value = "25", message = "Weight must be between 25 and 300 kg")
    @DecimalMax(value = "300", message = "Weight must be between 25 and 300 kg")
    private double weightKg;

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
}
