package com.cuervo.erp_personal.controllers;
import com.cuervo.erp_personal.dto.ChatRequest;
import com.cuervo.erp_personal.models.DailyReport;
import com.cuervo.erp_personal.models.User;
import com.cuervo.erp_personal.repositories.DailyGoalRepository;
import com.cuervo.erp_personal.repositories.DailyReportRepository;
import com.cuervo.erp_personal.repositories.UserRepository;
import com.cuervo.erp_personal.services.AnalyticsService;
import com.cuervo.erp_personal.services.GeminiService;
import com.cuervo.erp_personal.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;
    @Autowired
    private DailyReportRepository dailyReportRepository;
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
}
