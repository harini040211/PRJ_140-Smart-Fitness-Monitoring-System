package com.fitness.monitor.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "water_logs")
public class WaterLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private int amountMl;
    private LocalDate date = LocalDate.now();

    public WaterLog() {}

    public WaterLog(Long userId, int amountMl) {
        this.userId = userId;
        this.amountMl = amountMl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public int getAmountMl() { return amountMl; }
    public void setAmountMl(int amountMl) { this.amountMl = amountMl; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
