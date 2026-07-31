package com.cuervo.erp_personal.controllers;
import com.cuervo.erp_personal.dto.ChatRequest;
import com.cuervo.erp_personal.models.DailyReport;
import com.cuervo.erp_personal.repositories.DailyGoalRepository;
import com.cuervo.erp_personal.repositories.DailyReportRepository;
import com.cuervo.erp_personal.services.AnalyticsService;
import com.cuervo.erp_personal.services.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

        // 2- LLAMADA al servicio de Gemini pasándole el estilo, el resumen del reporte y la conversación
        String reply = geminiService.chatWithContext(
                report.getUsedStyle(),
                report.getSummary(),
                request.getHistory(),
                request.getMessage(),
                request.getLanguage()
        );

        Map<String, String> response = new HashMap<>();
        response.put("reply", reply);
        return ResponseEntity.ok(response);
    }
}
