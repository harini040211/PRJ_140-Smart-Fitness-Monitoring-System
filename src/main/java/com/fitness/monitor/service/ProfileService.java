package com.fitness.monitor.service;

import com.fitness.monitor.dto.GoalsRequest;
import com.fitness.monitor.dto.ProfileRequest;
import com.fitness.monitor.model.Profile;
import com.fitness.monitor.model.WeightRecord;
import com.fitness.monitor.repository.ProfileRepository;
import com.fitness.monitor.repository.WeightRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    private static final List<String> DIETS = List.of("Vegetarian", "Eggetarian", "Non-Vegetarian");

    private final ProfileRepository profileRepository;
    private final WeightRecordRepository weightRepository;

    public ProfileService(ProfileRepository profileRepository, WeightRecordRepository weightRepository) {
        this.profileRepository = profileRepository;
        this.weightRepository = weightRepository;
    }

    public Profile save(Long userId, ProfileRequest request) {
        Profile profile = profileRepository.findByUserId(userId).orElseGet(Profile::new);
        boolean firstSave = profile.getId() == null;
        profile.setUserId(userId);
        profile.setAge(request.getAge());
        profile.setGender(request.getGender());
        profile.setHeightCm(request.getHeightCm());
        profile.setWeightKg(request.getWeightKg());
        profile.setActivityLevel(request.getActivityLevel());
        profile.setFitnessGoal(request.getFitnessGoal());
        profile.setTargetWeightKg(request.getTargetWeightKg());
        if (request.getDietaryPreference() != null && DIETS.contains(request.getDietaryPreference())) {
            profile.setDietaryPreference(request.getDietaryPreference());
        }
        recalculate(profile);
        Profile saved = profileRepository.save(profile);
        if (firstSave) {
            WeightRecord first = new WeightRecord(userId, request.getWeightKg());
            weightRepository.save(first);
        }
        return saved;
    }

    public Profile updateGoals(Long userId, GoalsRequest request) {
        Profile profile = get(userId);
        profile.setWeeklyWorkoutGoal(request.getWeeklyWorkoutGoal());
        profile.setDailyWaterGoalMl(request.getDailyWaterGoalMl());
        profile.setDailyStepGoal(request.getDailyStepGoal());
        profile.setTargetWeightKg(request.getTargetWeightKg());
        return profileRepository.save(profile);
    }

    public Profile updateWeight(Long userId, double weightKg) {
        Profile profile = get(userId);
        profile.setWeightKg(weightKg);
        recalculate(profile);
        return profileRepository.save(profile);
    }

    public Profile get(Long userId) {
        return find(userId).orElseThrow(() ->
                new IllegalArgumentException("Please complete your profile first"));
    }

    public Optional<Profile> find(Long userId) {
        return profileRepository.findByUserId(userId);
    }

    private void recalculate(Profile profile) {
        double bmi = NutritionCalculator.bmi(profile.getWeightKg(), profile.getHeightCm());
        double bmr = NutritionCalculator.bmr(profile.getWeightKg(), profile.getHeightCm(),
                profile.getAge(), profile.getGender());
        double tdee = NutritionCalculator.tdee(bmr, profile.getActivityLevel());
        profile.setBmi(bmi);
        profile.setBmiCategory(NutritionCalculator.bmiCategory(bmi));
        profile.setBmr(NutritionCalculator.round(bmr));
        profile.setTdee(NutritionCalculator.round(tdee));
        profile.setCalorieTarget(NutritionCalculator.round(
                NutritionCalculator.calorieTarget(tdee, profile.getFitnessGoal())));
    }

    public static LocalDate today() {
        return LocalDate.now();
    }
}
