package com.fitness.monitor.service;

import com.fitness.monitor.dto.FoodItem;
import com.fitness.monitor.dto.MealRequest;
import com.fitness.monitor.model.Meal;
import com.fitness.monitor.repository.MealRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MealService {

    private final MealRepository mealRepository;
    private final CatalogService catalog;

    public MealService(MealRepository mealRepository, CatalogService catalog) {
        this.mealRepository = mealRepository;
        this.catalog = catalog;
    }

    public Meal log(Long userId, MealRequest request) {
        FoodItem food = catalog.food(request.getFoodId());
        double servings = request.getServings();
        Meal meal = new Meal(userId, food.name(), request.getMealType(), servings,
                NutritionCalculator.round(food.calories() * servings),
                NutritionCalculator.round(food.protein() * servings),
                NutritionCalculator.round(food.carbs() * servings),
                NutritionCalculator.round(food.fat() * servings));
        meal.setDate(DateRules.resolve(request.getDate()));
        return mealRepository.save(meal);
    }

    public List<Meal> history(Long userId) {
        return mealRepository.findByUserIdOrderByDateDescIdDesc(userId);
    }

    public void delete(Long userId, Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .filter(m -> m.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("That meal could not be found"));
        mealRepository.delete(meal);
    }
}
