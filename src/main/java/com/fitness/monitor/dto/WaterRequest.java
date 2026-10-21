package com.fitness.monitor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class WaterRequest {

    @Min(value = 50, message = "Water amount must be between 50 and 2000 ml")
    @Max(value = 2000, message = "Water amount must be between 50 and 2000 ml")
    private int amountMl;

    public int getAmountMl() { return amountMl; }
    public void setAmountMl(int amountMl) { this.amountMl = amountMl; }
}
