package com.fitness.monitor.service;

import com.fitness.monitor.dto.ActivityRequest;
import com.fitness.monitor.model.ActivityLog;
import com.fitness.monitor.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ActivityService {

    private final ActivityLogRepository repository;

    public ActivityService(ActivityLogRepository repository) {
        this.repository = repository;
    }

    public ActivityLog save(Long userId, ActivityRequest request) {
        LocalDate date = DateRules.resolve(request.getDate());
        ActivityLog log = repository.findByUserIdAndDate(userId, date)
                .orElseGet(() -> new ActivityLog(userId, date, 0, 0));
        log.setSteps(request.getSteps());
        log.setActiveMinutes(request.getActiveMinutes());
        return repository.save(log);
    }

    public List<ActivityLog> history(Long userId) {
        return repository.findByUserIdOrderByDateDesc(userId);
    }
}
