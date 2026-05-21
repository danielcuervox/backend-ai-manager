package com.cuervo.erp_personal.repositories;

import com.cuervo.erp_personal.models.Activity;
import com.cuervo.erp_personal.models.WeeklyGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long>{

    List<Activity> findByDateBetween(LocalDate weekStartDate, LocalDate today);

    List<Activity> findByDate(LocalDate date);
}
