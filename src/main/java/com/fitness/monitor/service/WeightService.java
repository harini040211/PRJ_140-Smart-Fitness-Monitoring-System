package com.fitness.monitor.service;

import com.fitness.monitor.dto.WeightRequest;
import com.fitness.monitor.model.WeightRecord;
import com.fitness.monitor.repository.WeightRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeightService {

    private final WeightRecordRepository repository;
    private final ProfileService profileService;

    public WeightService(WeightRecordRepository repository, ProfileService profileService) {
        this.repository = repository;
        this.profileService = profileService;
    }

    public WeightRecord log(Long userId, WeightRequest request) {
        profileService.updateWeight(userId, request.getWeightKg());
        return repository.save(new WeightRecord(userId, request.getWeightKg()));
    }

    public List<WeightRecord> history(Long userId) {
        return repository.findByUserIdOrderByDateAscIdAsc(userId);
    }
}
