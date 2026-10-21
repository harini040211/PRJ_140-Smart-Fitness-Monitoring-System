package com.fitness.monitor.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "weight_records")
public class WeightRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private double weightKg;
    private LocalDate date = LocalDate.now();

    public WeightRecord() {}

    public WeightRecord(Long userId, double weightKg) {
        this.userId = userId;
        this.weightKg = weightKg;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
