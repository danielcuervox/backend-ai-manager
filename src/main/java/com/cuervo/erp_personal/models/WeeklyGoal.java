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

    private String weeklyTargetName;// Ej: "Programming", "Meetings", "German" target
    private int weeklyTargetHours; // Ej: 30, 5, 3
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
