package com.cuervo.erp_personal.dto;

public record UserProfileDTO(
        String username,
        int points,
        int coins,
        int level,
        int streak,
        String avatar
) {}
