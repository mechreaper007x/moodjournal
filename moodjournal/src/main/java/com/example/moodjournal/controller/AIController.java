package com.example.moodjournal.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/ai")
public class AIController {

 private final RestTemplate restTemplate = new RestTemplate();

 @Value("${google.ai.apiKey}")
 private String apiKey;

 private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-05-20:generateContent?key=";

    @PostMapping("/emotion-breakdown")
    public ResponseEntity<String> getEmotionBreakdown(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Text cannot be empty\"}");
        }

        String systemPrompt = "Analyze the user's journal entry. Identify the top 3-5 primary emotions present in the text. Provide your response as a valid JSON array of objects, where each object has two keys: 'emotion' (string) and 'score' (a number from 0 to 100). The scores should represent the intensity of each emotion. Do not include any emotions with a score below 10. Example: [{\"emotion\": \"Sadness\", \"score\": 75}, {\"emotion\": \"Hope\", \"score\": 25}]";
        
        return callGeminiApi(systemPrompt, "Analyze this entry: " + text);
 }

    @PostMapping("/daily-quote")
    public ResponseEntity<String> getDailyQuote() {
        String systemPrompt = "You are a source of wisdom. Provide a single, short, uplifting quote about self-reflection, mindfulness, or personal growth. The quote must be real and attributed to a known person. Format the response as a JSON object with two keys: 'quote' and 'author'. Example: {\"quote\": \"The unexamined life is not worth living.\", \"author\": \"Socrates\"}";

        return callGeminiApi(systemPrompt, "Give me a quote of the day.");
    }
 
    private ResponseEntity<String> callGeminiApi(String systemPrompt, String userQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("""
                {
                    "contents": [{"parts": [{"text": "%s"}]}],
                    "systemInstruction": {"parts": [{"text": "%s"}]},
                    "generationConfig": {"responseMimeType": "application/json"}
                }
                """, userQuery.replace("\"", "\\\""), systemPrompt.replace("\"", "\\\""));
 
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL + apiKey, entity, String.class);
            // We need to extract the actual JSON content from the candidate part of the response
            // A more robust solution would use DTOs and a JSON parser like Jackson
            String responseBody = response.getBody();
            return ResponseEntity.ok(extractJsonFromResponse(responseBody));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Failed to call AI service: " + e.getMessage() + "\"}");
        }
 }

    private String extractJsonFromResponse(String responseBody) {
        // A simple but effective way to extract the nested JSON string
        try {
            // Find the start of the text content
            String textMarker = "\"text\": \"";
            int startIndex = responseBody.indexOf(textMarker);
            if (startIndex == -1) return "{}";

            startIndex += textMarker.length();

            // Find the end of the text content
            int endIndex = responseBody.indexOf("\"\n        }\n      ]\n    }", startIndex);
            if (endIndex == -1) return "{}";
            
            String jsonText = responseBody.substring(startIndex, endIndex);
            
            // Unescape the JSON string
            return jsonText.replace("\\\"", "\"").replace("\\n", "\n");

        } catch (Exception e) {
            return "{\"error\": \"Failed to parse AI response\"}";
        }
 }
}