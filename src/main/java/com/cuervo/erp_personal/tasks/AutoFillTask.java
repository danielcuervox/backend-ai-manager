package com.cuervo.erp_personal.tasks;

import com.cuervo.erp_personal.models.Activity;
import com.cuervo.erp_personal.repositories.ActivityRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalTime;
public class AutoFillTask {

    private final ActivityRepository activityRepository;

    public AutoFillTask(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Scheduled(cron = "29,59 * * * * *")
    public void autoFillEmptyBlock() {
        LocalDate today = LocalDate.now();
        // Lógica: Buscar si hay algo registrado en los últimos 30 min
        // Si no hay, hacer:
        Activity emptyActivity = new Activity();
        emptyActivity.setDate(today);
        emptyActivity.setTime(LocalTime.now().toString());
        emptyActivity.setDescription("Sin actividad");
        emptyActivity.setResult(0);

        activityRepository.save(emptyActivity);
        System.out.println("Auto-registro vacío creado.");
    }
}
