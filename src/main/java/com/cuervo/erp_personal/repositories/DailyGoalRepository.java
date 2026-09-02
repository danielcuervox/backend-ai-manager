package com.cuervo.erp_personal.repositories;


import com.cuervo.erp_personal.models.DailyGoal;
import com.cuervo.erp_personal.models.User;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DailyGoalRepository extends JpaRepository<DailyGoal, Long> {
//    List<DailyGoal> findAll();
        List<DailyGoal> findByUser(User user);
}
