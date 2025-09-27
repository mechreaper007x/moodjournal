package com.example.moodjournal.service;

import com.example.moodjournal.dto.GeminiResponse;
import com.example.moodjournal.util.PromptConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class GeminiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${google.ai.apiKey}")
    private String apiKey;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro-001:generateContent?key=";

    public String getEmotionBreakdown(String text) {
        return callGeminiApi(PromptConstants.EMOTION_BREAKDOWN_PROMPT, "Analyze this entry: " + text);
    }

    public String getDailyQuote() {
        return callGeminiApi(PromptConstants.DAILY_QUOTE_PROMPT, "Give me a quote of the day.");
    }

    public String suggestMood(String text) {
        return callGeminiApi(PromptConstants.SUGGEST_MOOD_PROMPT, "Suggest a mood for this entry: " + text);
    }

    private String callGeminiApi(String systemPrompt, String userQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{\"contents\": [{\"parts\": [{\"text\": \"%s\"}]}], \"systemInstruction\": {\"parts\": [{\"text\": \"%s\"}]}, \"generationConfig\": {\"responseMimeType\": \"application/json\"}}", userQuery.replace("\"", "\\\""), systemPrompt.replace("\"", "\\\""));

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL + apiKey, entity, String.class);
            String responseBody = response.getBody();
            return extractJsonFromResponse(responseBody);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to call AI service: " + e.getMessage());
            try {
                return objectMapper.writeValueAsString(errorResponse);
            } catch (JsonProcessingException jsonEx) {
                return "{\"error\": \"Failed to serialize error message\"}";
            }
        }
    }

    private String extractJsonFromResponse(String responseBody) {
        try {
            GeminiResponse geminiResponse = objectMapper.readValue(responseBody, GeminiResponse.class);
            if (geminiResponse != null && geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()) {
                GeminiResponse.Candidate candidate = geminiResponse.getCandidates().get(0);
                if (candidate != null && candidate.getContent() != null && candidate.getContent().getParts() != null && !candidate.getContent().getParts().isEmpty()) {
                    String rawJson = candidate.getContent().getParts().get(0).getText();
                    System.out.println("---
--- RAW AI RESPONSE ---\
---");
                    System.out.println(rawJson);
                    String cleanedJson = cleanJson(rawJson);
                    System.out.println("---
--- CLEANED JSON ---\
---");
                    System.out.println(cleanedJson);
                    return cleanedJson;
                }
            }
            return "{}";
        } catch (Exception e) {
            return "{\"error\": \"Failed to parse AI response\"}";
        }
    }

    private String cleanJson(String json) {
        // Find the start of the JSON object or array
        int firstBracket = json.indexOf('{');
        int firstSquare = json.indexOf('[');
        int start = -1;

        if (firstBracket == -1) {
            start = firstSquare;
        } else if (firstSquare == -1) {
            start = firstBracket;
        } else {
            start = Math.min(firstBracket, firstSquare);
        }

        if (start == -1) {
            return "{}"; // Or handle as an error
        }

        // Find the end of the JSON object or array
        int lastBracket = json.lastIndexOf('}');
        int lastSquare = json.lastIndexOf(']');
        int end = Math.max(lastBracket, lastSquare);

        if (end == -1) {
            return "{}"; // Or handle as an error
        }

        return json.substring(start, end + 1);
    }
}