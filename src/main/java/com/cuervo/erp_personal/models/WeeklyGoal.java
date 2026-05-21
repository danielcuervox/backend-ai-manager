package com.cuervo.erp_personal.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "weekly_goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private LocalDate weekStartDate; // El lunes de la semana

    private String targetName;// Ej: "Programming", "Meetings", "German"
    private int targetHours; // Ej: 30, 5, 3
}
