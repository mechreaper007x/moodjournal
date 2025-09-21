package com.example.moodjournal.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.moodjournal.dto.SuggestMoodRequest;
import com.example.moodjournal.model.Mood;
import com.example.moodjournal.service.JournalEntryService;
import com.example.moodjournal.service.SentimentAnalysisService;

@RestController
@RequestMapping("/api/ai")
public class AIController {
 private final JournalEntryService journalEntryService;
 private final SentimentAnalysisService sentimentService;

 // The constructor MUST accept both services. This is the critical fix.
 public AIController(JournalEntryService journalEntryService, SentimentAnalysisService sentimentService) {
 this.journalEntryService = journalEntryService;
 this.sentimentService = sentimentService; // This line fixes the crash
 }

 @PostMapping("/suggest-mood")
 public Map<String, Object> suggestMood(@RequestBody SuggestMoodRequest payload) {
 try {
 String content = payload.getContent();
 if (content == null || content.isBlank()) {
 return Collections.emptyMap(); // Return empty if there's no content
 }

 // Now the controller can safely use both services without crashing
 Mood suggestedMood = journalEntryService.suggestMood(content);
 double confidence = journalEntryService.getMoodConfidence(content, suggestedMood);
 List<String> signs = sentimentService.analyzeAdditionalSigns(content);

 // This will now return the full analysis data successfully
 return Map.of(
 "mood", suggestedMood.name(),
 "confidence", String.format("%.2f", confidence * 100),
 "signs", signs
 );
 } catch (Exception e) {
 // Return error response
 return Map.of(
 "error", "Failed to analyze mood",
 "message", e.getMessage()
 );
 }
 }
}