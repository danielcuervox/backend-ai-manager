package com.cuervo.erp_personal.controllers;
import com.cuervo.erp_personal.dto.ChatRequest;
import com.cuervo.erp_personal.dto.GoalComparisonDTO;
import com.cuervo.erp_personal.models.*;
import com.cuervo.erp_personal.repositories.*;
import com.cuervo.erp_personal.services.AnalyticsService;
import com.cuervo.erp_personal.services.GeminiService;
import com.cuervo.erp_personal.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private DailyReportRepository dailyReportRepository;

    @Autowired
    private DailyGoalRepository dailyGoalRepository;

    @Autowired
    private WeeklyGoalRepository weeklyGoalRepository;
    @Autowired
    private GeminiService geminiService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;


   /* @GetMapping("weekly-progress") //http://localhost:8081/api/analytics/weekly-progress
    public Map<String, Double> getWeeklyProgress() {
        // Usamos hoy como fecha de referencia
        return analyticsService.getWeeklyPartialResults(LocalDate.now());
    }*/

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chatWithManager(@RequestBody ChatRequest request) {

        // 1- REPORTE de la fecha especificada para recuperar el contexto
        LocalDate date = LocalDate.parse(request.getDate());
        DailyReport report = dailyReportRepository.findByDate(date)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado para la fecha: " + request.getDate()));

        // 2- LLAMADA al servicio IA pasándole el estilo, el resumen del reporte y la conversación
        String reply = geminiService.chatWithContext(
                report.getUsedStyle(),
                report.getSummary(),
                request.getHistory(),
                request.getMessage(),
                request.getLanguage()
        );

        //3 - OBTIENE Usuario
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        //3. agregar puntos al usuario chat según el número de palabras del mensaje
        int chatPoints = userService.calculateMessageLength(request.getMessage());
        if (chatPoints > 0) {
            userService.addPointsUser(user, chatPoints);
        }

        //4. suma monedas
        int chatCoins = userService.calculateMessageCoins(request.getMessage());
        if (chatCoins > 0) {
            userService.addCoinsUser(user, chatCoins);
        }

        Map<String, String> response = new HashMap<>();
        response.put("reply", reply);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/goals-comparison")
    public ResponseEntity<List<GoalComparisonDTO>> getGoalsComparison() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<DailyGoal> dailyGoals = dailyGoalRepository.findByUser(user);

        List<Activity> activities = activityRepository.findByUserAndDateAndDoneTrue(user, LocalDate.now());

        List<GoalComparisonDTO> comparisonList = new ArrayList<>();

        for (DailyGoal goal : dailyGoals) {
            // Sumar las horas de las actividades completadas que coincidan con este objetivo
            double completedHours = activities.stream()
                    .filter(a -> a.getCategory() != null &&
                            a.getCategory().getCategoryName() != null &&
                            a.getCategory().getCategoryName().equalsIgnoreCase(goal.getDailyTargetName()))
                    .mapToDouble(a -> {
                        return 1.0; // Cambia esto por a.getHours() si añades el campo a la entidad Activity
                    })
                    .sum();

            comparisonList.add(new GoalComparisonDTO(
                    goal.getDailyTargetName(),
                    goal.getDailyTargetHours(),
                    completedHours
            ));
        }

        return ResponseEntity.ok(comparisonList);
    }

    @GetMapping("/weekly-goals-comparison")
    public ResponseEntity<List<GoalComparisonDTO>> getWeeklyComparisson(){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<WeeklyGoal> weeklyGoals = weeklyGoalRepository.findByUser(user);

        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = now.with(java.time.DayOfWeek.SUNDAY);

        List<Activity> weeklyActivities = activityRepository.findByUserAndDateBetweenAndDoneTrue(user, startOfWeek, endOfWeek);

        List<GoalComparisonDTO> comparisonList = new ArrayList<>();

        for (WeeklyGoal weeklyGoal : weeklyGoals) {
            double completedHours = weeklyActivities.stream()
                    .filter(a -> a.getCategory() != null &&
                            a.getCategory().getCategoryName() != null &&
                            a.getCategory().getCategoryName().equalsIgnoreCase(weeklyGoal.getWeeklyTargetName()))
                    .mapToDouble(a -> {
                        return 1.0; // Cambia esto por a.getHours() si añades el campo a la entidad Activity
                    })
                    .sum();

            comparisonList.add(new GoalComparisonDTO(
                    weeklyGoal.getWeeklyTargetName(),
                    weeklyGoal.getWeeklyTargetHours(),
                    completedHours
            ));
        }
        return ResponseEntity.ok(comparisonList);
        
    }

}
