package com.fitness.monitor.repository;

import com.fitness.monitor.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    Optional<ActivityLog> findByUserIdAndDate(Long userId, LocalDate date);
    List<ActivityLog> findByUserIdOrderByDateDesc(Long userId);
}
