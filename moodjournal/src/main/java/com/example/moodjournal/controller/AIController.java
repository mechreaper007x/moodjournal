package com.example.moodjournal.controller;

import java.util.Map; // <-- Make sure to import the new DTO

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.moodjournal.dto.SuggestMoodRequest;
import com.example.moodjournal.model.Mood;
import com.example.moodjournal.service.JournalEntryService;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final JournalEntryService journalEntryService;

    public AIController(JournalEntryService journalEntryService) {
        this.journalEntryService = journalEntryService;
    }

    @PostMapping("/suggest-mood")
    public Map<String, String> suggestMood(@RequestBody SuggestMoodRequest payload) {
        String content = payload.getContent();
        Mood suggestedMood = journalEntryService.suggestMood(content);
        return Map.of("mood", suggestedMood.name());
    }
}