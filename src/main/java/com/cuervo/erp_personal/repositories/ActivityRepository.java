package com.cuervo.erp_personal.repositories;

import com.cuervo.erp_personal.models.Activity;
import com.cuervo.erp_personal.models.User;
import com.cuervo.erp_personal.models.WeeklyGoal;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long>{

    List<Activity> findByDateBetween(LocalDate weekStartDate, LocalDate today);

    List<Activity> findByDate(LocalDate date);

    List<Activity> findByDateOrderByTimeAsc(LocalDate date);
    List<Activity> findByDateAndUserIdOrderByTimeAsc(LocalDate date, Long userId);
    List<Activity> findByUserEmail(String email);
    List<Activity> findByDateAndUser(LocalDate date, User user);

    @Query("SELECT a FROM Activity a WHERE a.date IN :dates AND a.user = :user")
    List<Activity> findByDateIn(java.util.List<LocalDate> dates, User user);

    List<Activity> findByUserAndDateAndDoneTrue(User user, LocalDate date);

    List<Activity> findByUserAndDateBetweenAndDoneTrue(User user, LocalDate startOfWeek, LocalDate endOfWeek);
}
