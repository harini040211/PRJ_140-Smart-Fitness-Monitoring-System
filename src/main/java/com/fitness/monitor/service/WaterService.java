package com.fitness.monitor.service;

import com.fitness.monitor.dto.WaterRequest;
import com.fitness.monitor.model.WaterLog;
import com.fitness.monitor.repository.WaterLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class WaterService {

    private final WaterLogRepository repository;
    private final ProfileService profileService;

    public WaterService(WaterLogRepository repository, ProfileService profileService) {
        this.repository = repository;
        this.profileService = profileService;
    }

    public Map<String, Object> add(Long userId, WaterRequest request) {
        repository.save(new WaterLog(userId, request.getAmountMl()));
        return today(userId);
    }

    public Map<String, Object> removeLast(Long userId) {
        var entries = repository.findByUserIdAndDate(userId, LocalDate.now());
        if (!entries.isEmpty()) {
            entries.stream().max(java.util.Comparator.comparing(WaterLog::getId)).ifPresent(repository::delete);
        }
        return today(userId);
    }

    public Map<String, Object> today(Long userId) {
        int total = repository.findByUserIdAndDate(userId, LocalDate.now())
                .stream().mapToInt(WaterLog::getAmountMl).sum();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", LocalDate.now().toString());
        result.put("totalMl", total);
        result.put("goalMl", profileService.find(userId).map(p -> p.getDailyWaterGoalMl()).orElse(2000));
        return result;
    }
}
