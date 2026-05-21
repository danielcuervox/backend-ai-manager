package com.cuervo.erp_personal.controllers;

import com.cuervo.erp_personal.models.Activity;
import com.cuervo.erp_personal.models.DailyGoal;
import com.cuervo.erp_personal.models.DailyReport;
import com.cuervo.erp_personal.models.WeeklyGoal;
import com.cuervo.erp_personal.repositories.ActivityRepository;
import com.cuervo.erp_personal.repositories.DailyGoalRepository;
import com.cuervo.erp_personal.repositories.WeeklyGoalRepository;
import com.cuervo.erp_personal.services.AnalyticsService;
import com.cuervo.erp_personal.services.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;
@RestController
public class ProductivityController {

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ExcelService excelService;
    @Autowired
    private AnalyticsService analyticsService;
    @Autowired
    private DailyGoalRepository dailyGoalRepository;
    @Autowired
    private WeeklyGoalRepository weeklyGoalRepository;
    @Autowired
    private com.cuervo.erp_personal.repositories.DailyReportRepository dailyReportRepository;

    @Autowired
    private com.cuervo.erp_personal.services.GeminiService geminiService;

    //Método para importar (POST)
    @PostMapping("/api/activities/import") //http://localhost:8081/api/activities/import?path=C:/Users/Lezam/ReactNative/ProductivityApp/ProductivityExcel.xlsx
    public List<Activity> importExcelData(@RequestParam("path") String filePath) {
        try {
            // Llamamos al servicio pasando la ruta del archivo que recibimos por la URL
            return excelService.importExcel(filePath);
        } catch (Exception e) {
            // Si algo falla (archivo no encontrado, formato incorrecto...), lanzamos el error
            throw new RuntimeException("Error al importar el archivo Excel: " + e.getMessage());
        }
    }

    @DeleteMapping("/api/activities/clear")
    public String clearDatabase() {
        activityRepository.deleteAll();
        return "Base de datos limpia, lista para una nueva importación.";
    }

    //método para consultar (GET)
    @GetMapping("/api/activities")
    public List<Activity> getAllActivities() {
        // Esto ejecutará un "SELECT * FROM activities"
        return activityRepository.findAll();
    }

    @GetMapping("/api/analytics/average")
    public Double getAverage(){
        return analyticsService.calculateGeneralAverage();
    }

    @GetMapping("/api/analytics/average-by-category") //http://localhost:8081/api/analytics/average-by-category?category=Study
    public double getAverageByCategory(@RequestParam("category") String category) {
        return analyticsService.calculateAverageByCategory(category);
    }



    @PostMapping("/api/analytics/daily-report") //http://localhost:8081/api/analytics/daily-report?date=2026-05-20
    public DailyReport generateAndSaveDailyReport(@RequestParam("date") String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);

        /*// OBTENER LAS ACTIVIDADES DE UN DÍA
        List<Activity> dailyActivities = activityRepository.findAll().stream()
                .filter(a -> a.getDate().equals(date))
                .toList();*/

        List<Activity> dailyActivities = activityRepository.findByDate(date);

        if (dailyActivities.isEmpty()) {
            throw new RuntimeException("No se encontraron Metas Diarias: " + dateStr);
        }

        // -Convertimos la lista a un texto simple para enviarlo al prompt
        StringBuilder jsonBuilder = new StringBuilder();
        for (Activity a : dailyActivities) {
            jsonBuilder.append(String.format("- Desc: %s, Cat: %s, Resultado: %d, Comentario: %s\n",
                    a.getDescription(), a.getCategory(), a.getResult(), a.getComment()));
        }

        //OBTENER LOS OBJETIVOS DIARIOS
        List<DailyGoal> dailyGoalList = dailyGoalRepository.findAll();

        if(dailyGoalList.isEmpty()){
            throw new RuntimeException("There are no goal for this week: " + dailyGoalList);
        }

        StringBuilder jbuildDailyGoals = new StringBuilder("Daily Habits/Goals:\n");
        for (DailyGoal goal : dailyGoalList) {
            jbuildDailyGoals.append("- ")
                    .append(goal.getDailyTargetName())
                    .append(": ")
                    .append(goal.getDailyTargetHours())
                    .append(" hour(s)\n");
        }

        //OBTENER LOS OBJETIVOS SEMANALES
        LocalDate today = LocalDate.now();
        LocalDate weekStartDate = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        List<WeeklyGoal> weeklyGoalList = weeklyGoalRepository.findAllByWeekStartDate(weekStartDate);

        if(weeklyGoalList.isEmpty()){
            throw new RuntimeException("There are no goal for this week: " + weekStartDate);
        }

        String strGoalsBuilder = prepareGoalSummary(weeklyGoalList);

        // 3. Llamamos a Gemini para que haga la magia del análisis
        String aiSummary = geminiService.generateDailySummary(jsonBuilder.toString(), strGoalsBuilder, jbuildDailyGoals.toString());

        // 4. Guardamos o actualizamos el reporte diario en Supabase
        DailyReport report = dailyReportRepository.findByDate(date).orElse(new DailyReport());
        report.setDate(date);
        report.setSummary(aiSummary);

        return dailyReportRepository.save(report);
    }



    public String prepareGoalSummary(List<WeeklyGoal> goals) {
        StringBuilder sb = new StringBuilder("Weekly Goals Status:\n");
        for (WeeklyGoal goal : goals) {
            sb.append("- Target: ")
                    .append(goal.getTargetName())
                    .append(" | Goal: ")
                    .append(goal.getTargetHours())
                    .append(" hours\n");
        }
        return sb.toString();
    }
}
