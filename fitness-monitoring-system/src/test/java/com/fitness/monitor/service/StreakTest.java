package com.fitness.monitor.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StreakTest {

    private final LocalDate today = LocalDate.now();

    @Test
    void countsConsecutiveDaysEndingToday() {
        assertEquals(3, DashboardService.streak(Set.of(today, today.minusDays(1), today.minusDays(2))));
    }

    @Test
    void streakSurvivesUntilTheDayIsOver() {
        assertEquals(2, DashboardService.streak(Set.of(today.minusDays(1), today.minusDays(2))));
    }

    @Test
    void gapBreaksTheStreak() {
        assertEquals(1, DashboardService.streak(Set.of(today, today.minusDays(2))));
    }

    @Test
    void noRecentActivityMeansNoStreak() {
        assertEquals(0, DashboardService.streak(Set.of(today.minusDays(3))));
        assertEquals(0, DashboardService.streak(Set.of()));
    }
}
