package com.fitness.monitor.repository;

import com.fitness.monitor.model.WaterLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface WaterLogRepository extends JpaRepository<WaterLog, Long> {
    List<WaterLog> findByUserIdAndDate(Long userId, LocalDate date);
    List<WaterLog> findByUserIdOrderByDateDesc(Long userId);
}
