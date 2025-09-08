package com.example.moodjournal.dto;

import com.example.moodjournal.model.Mood;
import com.example.moodjournal.model.Visibility;

import lombok.Data;

@Data
public class UpdateJournalEntryRequest {
    private String title;
    private String content;
    private Mood mood;
    private Visibility visibility;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Mood getMood() {
        return mood;
    }

    public Visibility getVisibility() {
        return visibility;
    }
}