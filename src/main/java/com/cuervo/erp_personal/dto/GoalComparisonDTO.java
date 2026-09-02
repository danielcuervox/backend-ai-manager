package com.cuervo.erp_personal.dto;

public record GoalComparisonDTO(
        String name,
        double targetHours,
        double completedHours
) {}