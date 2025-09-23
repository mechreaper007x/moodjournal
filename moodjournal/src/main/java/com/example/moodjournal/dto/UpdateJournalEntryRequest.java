package com.example.moodjournal.dto;

import lombok.Data;

@Data
public class UpdateJournalEntryRequest {
    private String title;
    private String content;
    // use String here to avoid Jackson enum binding errors; service will parse
    private String mood;
    private String visibility;

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getMood() {
 return mood; }
    public String getVisibility() { return visibility; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setMood(String mood) { this.mood = mood; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
}