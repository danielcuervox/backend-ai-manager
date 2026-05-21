package com.cuervo.erp_personal.services;

import com.cuervo.erp_personal.models.Activity;
import com.cuervo.erp_personal.models.WeeklyGoal;
import com.cuervo.erp_personal.repositories.ActivityRepository;
import com.cuervo.erp_personal.repositories.WeeklyGoalRepository;
import org.apache.poi.ss.formula.functions.Today;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private WeeklyGoalRepository weeklyGoalRepository;

    public double calculateGeneralAverage() {
        List<Activity> activities = activityRepository.findAll();

        if (activities.isEmpty()) {
            return 0.0;
        }

        // Streams suma todos los 'result' y calcular el promedio
        return activities.stream()
                .mapToInt(Activity::getResult)
                .average()
                .orElse(0.0);
    }

    public double calculateAverageByCategory(String category) {
        List<Activity> activities = activityRepository.findAll();

        if (activities.isEmpty()) {
            return 0.0;
        }

        return activities.stream()
                .filter(activity -> activity.getCategory() != null && activity.getCategory().equalsIgnoreCase(category))
                .mapToInt(Activity::getResult)
                .average()
                .orElse(0.0);
    }

    public Map<String, Double> getWeeklyPartialResults(LocalDate today){

        LocalDate weekStartDate = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        List<WeeklyGoal> goals = weeklyGoalRepository.findAllByWeekStartDate(weekStartDate);
        List<Activity> activities = activityRepository.findByDateBetween(weekStartDate, today);

        Map<String, Double> results = new HashMap<>();

        // 3. Lógica: Sumar horas reales por categoría y comparar con objetivos
        for (WeeklyGoal goal : goals) {
            // Contamos cuántas filas coinciden con la categoría
            long count = activities.stream()
                    .filter(a -> a.getCategory() != null && a.getCategory().equalsIgnoreCase(goal.getTargetName()))
                    .count();

            // Multiplicamos el número de bloques por 0.5 para obtener las horas totales
            double totalHours = count * 0.5;

            results.put(goal.getTargetName(), totalHours);
        }

        return results;
    }


}
