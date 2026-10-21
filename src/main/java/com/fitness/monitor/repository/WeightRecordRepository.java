package com.fitness.monitor.repository;

import com.fitness.monitor.model.WeightRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WeightRecordRepository extends JpaRepository<WeightRecord, Long> {
    List<WeightRecord> findByUserIdOrderByDateAscIdAsc(Long userId);
    List<WeightRecord> findByUserIdOrderByDateDescIdDesc(Long userId);
}
