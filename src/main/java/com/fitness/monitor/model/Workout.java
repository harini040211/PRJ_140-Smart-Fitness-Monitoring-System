package com.fitness.monitor.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String exerciseType;
    private String intensity;
    private int durationMinutes;
    private double caloriesBurned;
    private LocalDate date = LocalDate.now();

    public Workout() {}

    public Workout(Long userId, String exerciseType, String intensity, int durationMinutes) {
        this.userId = userId;
        this.exerciseType = exerciseType;
        this.intensity = intensity;
        this.durationMinutes = durationMinutes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getExerciseType() { return exerciseType; }
    public void setExerciseType(String exerciseType) { this.exerciseType = exerciseType; }

    public String getIntensity() { return intensity; }
    public void setIntensity(String intensity) { this.intensity = intensity; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public double getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(double caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
