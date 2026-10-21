package com.fitness.monitor.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.monitor.dto.ExerciseItem;
import com.fitness.monitor.dto.FoodItem;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CatalogService {

    private final Map<String, ExerciseItem> exercises = new LinkedHashMap<>();
    private final Map<String, FoodItem> foods = new LinkedHashMap<>();

    @PostConstruct
    void load() throws IOException {
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try (InputStream in = new ClassPathResource("catalog/exercises.json").getInputStream()) {
            for (ExerciseItem item : mapper.readValue(in, new TypeReference<List<ExerciseItem>>() {})) {
                exercises.put(item.id(), item);
            }
        }
        try (InputStream in = new ClassPathResource("catalog/foods.json").getInputStream()) {
            for (FoodItem item : mapper.readValue(in, new TypeReference<List<FoodItem>>() {})) {
                foods.put(item.id(), item);
            }
        }
    }

    public List<ExerciseItem> exercises() {
        return List.copyOf(exercises.values());
    }

    public List<FoodItem> foods() {
        return List.copyOf(foods.values());
    }

    public ExerciseItem exercise(String id) {
        ExerciseItem item = exercises.get(id);
        if (item == null) {
            throw new IllegalArgumentException("That exercise is not in the library");
        }
        return item;
    }

    public FoodItem food(String id) {
        FoodItem item = foods.get(id);
        if (item == null) {
            throw new IllegalArgumentException("That food is not in the database");
        }
        return item;
    }

    public boolean isExercise(String id) {
        return exercises.containsKey(id);
    }

    public boolean isFood(String id) {
        return foods.containsKey(id);
    }
}
