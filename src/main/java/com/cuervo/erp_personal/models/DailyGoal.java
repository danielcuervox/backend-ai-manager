package com.cuervo.erp_personal.models;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "daily_goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dailyTargetName;// Ej: "Programming", "Meetings", "German"
    private double dailyTargetHours; // Ej: 1, 0.5, 2

}
