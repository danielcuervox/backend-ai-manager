package com.cuervo.erp_personal.controllers;
import com.cuervo.erp_personal.services.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("weekly-progress") //http://localhost:8081/api/analytics/weekly-progress
    public Map<String, Double> getWeeklyProgress() {
        // Usamos hoy como fecha de referencia
        return analyticsService.getWeeklyPartialResults(LocalDate.now());
    }
}
