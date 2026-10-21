package com.fitness.monitor.repository;

import com.fitness.monitor.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByUserIdOrderByDateDescIdDesc(Long userId);
}
