package com.cuervo.erp_personal.controllers;

import com.cuervo.erp_personal.dto.ReportRequest;
import com.cuervo.erp_personal.models.*;
import com.cuervo.erp_personal.repositories.*;
import com.cuervo.erp_personal.services.AnalyticsService;
import com.cuervo.erp_personal.services.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;


import java.time.LocalDate;
import java.util.List;
@RestController
public class ProductivityController {

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
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
    @PostMapping("/api/activities/import")
    //http://localhost:8081/api/activities/import?path=C:/Users/Lezam/ReactNative/ProductivityApp/ProductivityExcel.xlsx
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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return activityRepository.findByUserEmail(email);
    }

    @GetMapping("/api/activities/today")
    public ResponseEntity<List<Activity>> getActivitiesByDate() {
        // identificar al usuario logueado desde el token
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LocalDate today = LocalDate.now();
        System.out.println("Buscando actividades para fecha: " + today + " y user: " + user.getId());

        // buscar actividades filtrando por FECHA Y por USUARIO
        //return ResponseEntity.ok(activityRepository.findByDateAndUser(today, user));

        List<Activity> activities = activityRepository.findByDateIn(java.util.List.of(today, today.minusDays(1)), user);

        System.out.println("DEBUG: Buscando para hoy/ayer. Encontradas: " + activities.size());
        return ResponseEntity.ok(activities);
    }

    @PutMapping("/api/activities/{id}")
    public Activity editActivityById(@PathVariable Long id, @RequestBody Activity activityDetails){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        //busca la actividad
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada con ID: " + id));

        //actualiza con los datos de la actividad enviada
        activity.setDate(activityDetails.getDate());
        activity.setTime(activityDetails.getTime());
        activity.setDescription(activityDetails.getDescription());
        activity.setComment(activityDetails.getComment());
        activity.setResult(activityDetails.getResult());
        activity.setDone(activityDetails.getDone());

        if (activityDetails.getCategory() != null && activityDetails.getCategory().getId() != null) {
            Category category = categoryRepository.findById(activityDetails.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + activityDetails.getCategory().getId()));
            activity.setCategory(category);
        } else {
            activity.setCategory(null);
        }

        activity.setUser(user);

        return activityRepository.save(activity);
    }

    @DeleteMapping("/api/activities/{activityId}")
    public Boolean deleteActivityById(@PathVariable Long activityId) {
        if (activityRepository.existsById(activityId)) {
            activityRepository.deleteById(activityId);
            return true;
        }
        return false;
    }

    @PostMapping("api/activities")
    public Activity saveActivity(@RequestBody Activity activity) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (activity.getCategory() != null && activity.getCategory().getId() != null) {
            Category category = categoryRepository.findById(activity.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            activity.setCategory(category);
        }

        activity.setUser(user);
        return activityRepository.save(activity);
    }

    @GetMapping("/api/analytics/average")
    public Double getAverage(){
        return analyticsService.calculateGeneralAverage();
    }

   /* @GetMapping("/api/analytics/average-by-category") //http://localhost:8081/api/analytics/average-by-category?category=Study
    public double getAverageByCategory(@RequestParam("category") String category) {
        return analyticsService.calculateAverageByCategory(category);
    }*/

    @PostMapping("/api/analytics/daily-report") //http://localhost:8081/api/analytics/daily-report?date=2026-05-20
    public DailyReport generateAndSaveDailyReport(@RequestBody ReportRequest request) {
        LocalDate date = LocalDate.parse(request.getDate());
        String lang = request.getLanguage() != null ? request.getLanguage() : "en";

        List<Activity> dailyActivities = activityRepository.findAll();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if (dailyActivities.isEmpty()) {
            throw new RuntimeException("No se encontraron Actividades Diarias: - fecha enviada" + request.getDate() + "fecha formateda " + date);
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
            throw new RuntimeException("There are no daily goals" + dailyGoalList);
        }

        StringBuilder jbuildDailyGoals = new StringBuilder("Daily Habits/Goals:\n");
        for (DailyGoal goal : dailyGoalList) {
            jbuildDailyGoals.append("- ")
                    .append(goal.getDailyTargetName())
                    .append(": ")
                    .append(goal.getDailyTargetHours())
                    .append(" hour(s)\n");
        }

        /*OBJETIVOS SEMANALES*/
        List<WeeklyGoal> weeklyGoalList = weeklyGoalRepository.findAll();

        if(weeklyGoalList.isEmpty()){
            throw new RuntimeException("There are no weekly goals");
        }

        String strGoalsBuilder = prepareGoalSummary(weeklyGoalList);

        // 3. Llamamos a Gemini para que haga la magia del análisis
        ReportStyle coachPersonality = ReportStyle.getRandomStyle();
        String aiSummary = geminiService.generateDailySummary(
                jsonBuilder.toString(),
                strGoalsBuilder,
                jbuildDailyGoals.toString(),
                coachPersonality.toString(),
                lang
        );

        // 4. Guardamos o actualizamos el reporte diario en Supabase
        DailyReport report = dailyReportRepository.findByDate(date).orElse(new DailyReport());

        //5. obtiene el usuario
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        report.setDate(date);
        report.setSummary(aiSummary);
        report.setUsedStyle(coachPersonality);
        report.setUser(user);

        return dailyReportRepository.save(report);
    }

    //------CREAR OBJETIVO SEMANAL
    @PostMapping("/api/weekly-goal")
    public WeeklyGoal saveWeeklyGoal(@RequestBody WeeklyGoal weeklyGoal) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        weeklyGoal.setUser(user);
        return weeklyGoalRepository.save(weeklyGoal);
    }

    @GetMapping("/api/get-weekly-goals")
    public ResponseEntity<List<WeeklyGoal>> getWeeklyGoals() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        // buscar actividades filtrando por FECHA Y por USUARIO
        //return ResponseEntity.ok(activityRepository.findByDateAndUser(today, user));

        List<WeeklyGoal> activities = weeklyGoalRepository.findAll();

        System.out.println("DEBUG: Buscando para hoy/ayer. Encontradas: " + activities.size());
        return ResponseEntity.ok(activities);
    }

    @PutMapping("/api/weekly-goal/{weeklyGoalId}")
    public WeeklyGoal editWeeklyGoalById(@PathVariable Long weeklyGoalId, @RequestBody WeeklyGoal weeklyGoalDetails){

        //mostrar el objeto que llega
        System.out.println("llega: " + weeklyGoalDetails);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        //busca la actividad
        WeeklyGoal weeklyGoal = weeklyGoalRepository.findById(weeklyGoalId)
                .orElseThrow(() -> new RuntimeException("WeeklyGoal no encontrada con ID: " + weeklyGoalId));

        //actualiza con los datos de la actividad enviada
        weeklyGoal.setWeeklyTargetName(weeklyGoalDetails.getWeeklyTargetName());
        weeklyGoal.setWeeklyTargetHours(weeklyGoalDetails.getWeeklyTargetHours());

        weeklyGoal.setUser(user);

        return weeklyGoalRepository.save(weeklyGoal);
    }


    @DeleteMapping("/api/weekly-goal/{weeklyGoalId}")
    public Boolean deleteWeeklyGoalById(@PathVariable Long weeklyGoalId) {
        if (weeklyGoalRepository.existsById(weeklyGoalId)) {
            weeklyGoalRepository.deleteById(weeklyGoalId);
            return true;
        }
        return false;
    }

    //------CREAR OBJETIVO DIARIO
    @PostMapping("/api/daily-goal")
    public DailyGoal saveDailyGoal(@RequestBody DailyGoal dailyGoal){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        dailyGoal.setUser(user);
        return dailyGoalRepository.save(dailyGoal);
    }

    @GetMapping("/api/get-daily-goals")
    public ResponseEntity<List<DailyGoal>> getDailyGoals() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        // buscar actividades filtrando por FECHA Y por USUARIO
        //return ResponseEntity.ok(activityRepository.findByDateAndUser(today, user));

        List<DailyGoal> dailyGoals = dailyGoalRepository.findAll();

        System.out.println("DEBUG: Buscando para hoy/ayer. Encontradas: " + dailyGoals.size());
        return ResponseEntity.ok(dailyGoals);
    }

    @DeleteMapping("/api/daily-goal/{dailyGoalId}")
    public Boolean deleteDailyGoalById(@PathVariable Long dailyGoalId) {
        if (dailyGoalRepository.existsById(dailyGoalId)) {
            dailyGoalRepository.deleteById(dailyGoalId);
            return true;
        }
        return false;
    }

    @PutMapping("/api/daily-goal/{dailyGoalId}")
    public DailyGoal editDailyGoalById(@PathVariable Long dailyGoalId, @RequestBody DailyGoal dailyGoalDetails){

        //mostrar el objeto que llega
        System.out.println("llega: " + dailyGoalDetails);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        //busca la actividad
        DailyGoal dailyGoal = dailyGoalRepository.findById(dailyGoalId)
                .orElseThrow(() -> new RuntimeException("WeeklyGoal no encontrada con ID: " + dailyGoalId));

        //actualiza con los datos de la actividad enviada
        dailyGoal.setDailyTargetName(dailyGoalDetails.getDailyTargetName());
        dailyGoal.setDailyTargetHours(dailyGoalDetails.getDailyTargetHours());

        dailyGoal.setUser(user);

        return dailyGoalRepository.save(dailyGoal);
    }


    //------CATEGORÍAS
    @PostMapping("/api/categories")
    public Category saveNewCategory(@RequestBody Category category){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        category.setUser(user);
        return categoryRepository.save(category);
    }

    @GetMapping("/api/get-categories")
    public ResponseEntity<List<Category>> getAllCategories() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Category> listOfCategories = categoryRepository.findAll();

        System.out.println("DEBUG: Buscando CATEGORIAS. Encontradas: " + listOfCategories.size());
        return ResponseEntity.ok(listOfCategories);
    }

    @PutMapping("/api/categories/{categoryId}")
    public Category editCategoryById(@PathVariable Long categoryId, @RequestBody Category categoryDetails){

        //mostrar el objeto que llega
        System.out.println("llega PARA CATEGORÍA: " + categoryDetails);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        //busca la actividad
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category no encontrada con ID: " + categoryId));

        //actualiza con los datos de la actividad enviada
        category.setCategoryName(categoryDetails.getCategoryName());

        category.setUser(user);

        return categoryRepository.save(category);
    }

    @DeleteMapping("/api/categories/{categoryId}")
    public Boolean deleteCategoryById(@PathVariable Long categoryId) {
        if (categoryRepository.existsById(categoryId)) {
            categoryRepository.deleteById(categoryId);
            return true;
        }
        return false;
    }



    public String prepareGoalSummary(List<WeeklyGoal> goals) {
        StringBuilder sb = new StringBuilder("Weekly Goals Status:\n");
        for (WeeklyGoal goal : goals) {
            sb.append("- Target: ")
                    .append(goal.getWeeklyTargetName())
                    .append(" | Goal: ")
                    .append(goal.getWeeklyTargetHours())
                    .append(" hours\n");
        }
        return sb.toString();
    }

}
