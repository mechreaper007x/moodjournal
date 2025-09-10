// Add this new controller: moodjournal/src/main/java/com/example/moodjournal/controller/AIController.java

package com.example.moodjournal.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.moodjournal.model.Mood;
import com.example.moodjournal.service.JournalEntryService;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {
    
    private final JournalEntryService journalService;
    
    public AIController(JournalEntryService journalService) {
        this.journalService = journalService;
    }
    
    @PostMapping("/analyze-mood")
    public ResponseEntity<Map<String, Object>> analyzeMood(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Content is required for mood analysis"));
        }
        
        Mood suggestedMood = journalService.suggestMood(content);
        double confidence = journalService.getMoodConfidence(content, suggestedMood);
        
        return ResponseEntity.ok(Map.of(
            "suggestedMood", suggestedMood.toString(),
            "confidence", Math.round(confidence * 100), // Convert to percentage
            "message", getMoodMessage(suggestedMood, confidence)
        ));
    }
    
    private String getMoodMessage(Mood mood, double confidence) {
        String confidenceLevel = confidence > 0.3 ? "high" : confidence > 0.1 ? "medium" : "low";
        
        return switch (mood) {
            case HAPPY -> "🌟 Your text shows positive emotions! (" + confidenceLevel + " confidence)";
            case SAD -> "💙 I detect some sadness in your words. (" + confidenceLevel + " confidence)";
            case ANXIOUS -> "🌪️ Your text suggests some anxiety. (" + confidenceLevel + " confidence)";
            case ANGRY -> "🔥 There seems to be frustration in your message. (" + confidenceLevel + " confidence)";
            case CALM -> "🕊️ Your words reflect a peaceful state. (" + confidenceLevel + " confidence)";
            case CHILL -> "😎 You seem relaxed and easy-going! (" + confidenceLevel + " confidence)";
            default -> "⚪ Your mood appears neutral. (" + confidenceLevel + " confidence)";
        };
    }
}