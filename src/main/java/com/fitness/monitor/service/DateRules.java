package com.fitness.monitor.service;

import java.time.LocalDate;

public final class DateRules {

    private static final int MAX_BACKDATE_DAYS = 365;

    private DateRules() {}

    public static LocalDate resolve(LocalDate requested) {
        LocalDate today = LocalDate.now();
        if (requested == null) {
            return today;
        }
        if (requested.isAfter(today)) {
            throw new IllegalArgumentException("The date cannot be in the future");
        }
        if (requested.isBefore(today.minusDays(MAX_BACKDATE_DAYS))) {
            throw new IllegalArgumentException("The date is more than a year old");
        }
        return requested;
    }
}
