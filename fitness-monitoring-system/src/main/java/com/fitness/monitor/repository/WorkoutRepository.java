package com.fitness.monitor.repository;

import com.fitness.monitor.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByUserIdOrderByDateDescIdDesc(Long userId);
}
