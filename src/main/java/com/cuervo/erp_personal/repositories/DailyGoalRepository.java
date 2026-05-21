package com.cuervo.erp_personal.repositories;


import com.cuervo.erp_personal.models.DailyGoal;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyGoalRepository extends JpaRepository<DailyGoal, Long> {
//    List<DailyGoal> findAll();
}
