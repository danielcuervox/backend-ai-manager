package com.cuervo.erp_personal.services;

import com.cuervo.erp_personal.models.ReportStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import java.util.Random;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();


    public String generateDailySummary(String activitiesJson, String strGoals, String strDailyGoals) {
        //GOOGLE
        //String modelName = "gemini-2.0-flash-lite";
        //String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;

        //GROG
        String url = "https://api.groq.com/openai/v1/chat/completions";

        String[] styleManager = {
                "Military General: Heinz Guderian with Blitzkrieg Style",
                "Military General: Erwin Rommel with Bold Style",
                "Military General: Erich von Manstein with Operational Style",
                "Military General: Carl von Clausewitz with Theoretical Style",
                "Military General: George Patton with Aggressive Style",
                "Military Leader: Gaius Julius Cesar with Charismatic Style",
                "Military General: Napoleon Bonaparte with Strategic Style",
                "Military King: Alexander the Great with Visionary Style",
                "Military King: Leonidas I with Defiant Style",
                "Military Leader: Erik the Red with Exploratory Style",
                "Military Thinker: Sun Tzu with Subtle-Philosophical Style",
                "Military Strategist: Hannibal Barca with Tactical-Innovative Style",
                "Philosopher-Strategist: Marcus Aurelius with Reflective Style",
                "Sports Coach with Performance-Oriented Style",
                "Psychologist with Cognitive-Analytical Style",
                "Professional Manager with Systemic-Organizational Style"};

        ReportStyle style = ReportStyle.getRandomStyle();
        String styleDescription = style.getDescription();

        // Construimos el prompt estructurado para la IA
        String prompt = "Act as my professional Productivity Coach with the persona of: " + styleDescription + ". " +
                "### INSTRUCTIONS FOR YOUR PERSONA:\n" +
                "- Use the second person ('you').\n" +
                "- Adopt the vocabulary, mannerisms, and strategic mindset of this person.\n" +
                "- Use analogies related to warfare, high-performance strategy, or leadership characteristic of your persona.\n" +
                "- Start by introducing yourself and your tactical philosophy, followed by an evocative, motivating quote of your own.\n" +
                "- Be blunt, direct, and authoritative. Do not use 'AI-like' filler phrases. If I failed, tell me I retreated; if I succeeded, tell me I held the line.\n\n" +

                "### DATA SOURCES\n" +
                "- Daily Activities (JSON): " + activitiesJson + "\n" +
                "- Weekly Goals (Strategy): " + strGoals + "\n" +
                "- Daily Habits (Consistency): " + strDailyGoals + "\n\n" +

                "### YOUR TASK: Generate a report covering these sections:\n" +
                "1) Executive Summary: An ultra-condensed paragraph optimized for monthly archives.\n" +
                "2) Global Performance & Alignment: Evaluate how your daily work aligns with your Weekly Goals and Daily Habits. Be direct about where you are failing or succeeding.\n" +
                "3) Insights: Identify major strengths and specific distractions or bottlenecks.\n" +
                "4) Feedback on Goals: Compare today's work against the Weekly and Daily targets. Tell me what needs to change to get back on track.\n" +
                "5) Direct Feedback: Tell me what I must change tomorrow to ensure total dominance over my schedule.\n" +
                "6) Date of Analysis: " + LocalDate.now() + "\n\n" +

                "### FINAL OUTPUT & CLOSING:\n" +
                "- Provide a summary table: [Category] | [Total Hours] (Only for this date).\n" +
                "- End your report with a grand, majestic, and evocative call to arms, inspiring me to double my efforts. Include one famous, powerful quote from a different legendary historical figure about discipline, victory, or indomitable will.\n";

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        try {

            Map<String, Object> response = restClient.post()
                    .uri(url)
                    .header("Authorization", "Bearer " + apiKey) // Groq usa Bearer Token
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            // Extraer el texto de la respuesta de Groq
            List<?> choices = (List<?>) response.get("choices");
            Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
            Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            return "Error al conectar con Gemini API: " + e.getMessage();
        }
    }
}
