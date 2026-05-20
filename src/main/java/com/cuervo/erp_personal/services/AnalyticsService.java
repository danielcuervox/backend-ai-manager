package com.cuervo.erp_personal.services;

import com.cuervo.erp_personal.models.Activity;
import com.cuervo.erp_personal.repositories.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AnalyticsService {

    @Autowired
    private ActivityRepository activityRepository;

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

}
