package com.example.moodjournal.controller;

import com.example.moodjournal.dto.SuggestMoodRequest;
import com.example.moodjournal.service.GeminiService;
import com.example.moodjournal.service.SentimentAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final GeminiService geminiService;
    private final SentimentAnalysisService sentimentAnalysisService;

    public AIController(GeminiService geminiService, SentimentAnalysisService sentimentAnalysisService) {
        this.geminiService = geminiService;
        this.sentimentAnalysisService = sentimentAnalysisService;
    }

    @PostMapping(value = "/suggest-mood", produces = "application/json")
    public ResponseEntity<String> suggestMood(@RequestBody SuggestMoodRequest request) {
        ResponseEntity<String> validationError = validateTextRequest(request);
        if (validationError != null) {
            return validationError;
        }
        String response = geminiService.suggestMood(request.getContent());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/emotion-breakdown", produces = "application/json")
    public ResponseEntity<String> emotionBreakdown(@RequestBody SuggestMoodRequest request) {
        ResponseEntity<String> validationError = validateTextRequest(request);
        if (validationError != null) {
            return validationError;
        }
        String sentiment = sentimentAnalysisService.analyzeSentiment(request.getContent());
        // The frontend expects a JSON array of objects with "emotion" and "score".
        // We'll return a single object in an array.
        String jsonResponse = String.format("[{{\"emotion\": \"%s\", \"score\": 100}}]", sentiment);
        return ResponseEntity.ok(jsonResponse);
    }

    private ResponseEntity<String> validateTextRequest(SuggestMoodRequest request) {
        if (request.getContent() == null || request.getContent().isBlank()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Text cannot be empty\"}");
        }
        return null; // Validation passed
    }
    @CrossOrigin
    @GetMapping(value = "/daily-quote", produces = "application/json")
    public ResponseEntity<String> dailyQuote() {
        String response = geminiService.getDailyQuote();
        return ResponseEntity.ok(response);
    }
}