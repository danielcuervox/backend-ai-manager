package com.cuervo.erp_personal.repositories;

import com.cuervo.erp_personal.models.User;
import com.cuervo.erp_personal.models.WeeklyGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeeklyGoalRepository extends JpaRepository<WeeklyGoal, Long> {
    //List<WeeklyGoal> findAllByWeekStartDate(LocalDate date);
    List<WeeklyGoal> findByUser(User user);
}
