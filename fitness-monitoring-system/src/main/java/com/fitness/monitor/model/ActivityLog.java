package com.fitness.monitor.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "activity_logs", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "date"}))
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private int steps;
    private int activeMinutes;

    @Column(nullable = false)
    private LocalDate date;

    public ActivityLog() {}

    public ActivityLog(Long userId, LocalDate date, int steps, int activeMinutes) {
        this.userId = userId;
        this.date = date;
        this.steps = steps;
        this.activeMinutes = activeMinutes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public int getSteps() { return steps; }
    public void setSteps(int steps) { this.steps = steps; }
    public int getActiveMinutes() { return activeMinutes; }
    public void setActiveMinutes(int activeMinutes) { this.activeMinutes = activeMinutes; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
