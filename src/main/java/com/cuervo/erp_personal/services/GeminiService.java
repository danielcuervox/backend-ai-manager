package com.cuervo.erp_personal.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import java.util.Map;
import java.util.List;
@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();


    public String generateDailySummary(String activitiesJson) {
        //GOOGLE
        //String modelName = "gemini-2.0-flash-lite";
        //String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;

        //GROG
        String url = "https://api.groq.com/openai/v1/chat/completions";

        // Construimos el prompt estructurado para la IA
        String prompt = "Act as an expert productivity analyst. Review the following list of daily activities provided in JSON format. " +
                "Generate a comprehensive report that includes the following sections:\n\n" +
                "1) Global Performance Analysis: Evaluate the overall efficiency and goal alignment.\n" +
                "2) Insights: Identify major strengths and specific distractions or bottlenecks observed.\n" +
                "3) Executive Summary: Provide an ultra-condensed summary in a single paragraph, optimized for storage and use in future monthly/quarterly reports.\n\n" +
                "Activities Data:\n" + activitiesJson;

        // Estructura del Body requerida por la API de Gemini
        /*Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );*/

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        try {
            // Petición POST síncrona a Google
            /*Map<String, Object> response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            // Extrayemos el texto plano de la respuesta JSON jerárquica de Google
            List<?> candidates = (List<?>) response.get("candidates");
            Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
            Map<?, ?> content = (Map<?, ?>) firstCandidate.get("content");
            List<?> parts = (List<?>) content.get("parts");
            Map<?, ?> firstPart = (Map<?, ?>) parts.get(0);

            return (String) firstPart.get("text");*/

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
