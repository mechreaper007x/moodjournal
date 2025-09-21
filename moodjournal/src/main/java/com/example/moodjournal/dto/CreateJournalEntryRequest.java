package com.example.moodjournal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateJournalEntryRequest {
    @NotBlank(message = "Title cannot be empty")
    @Size(max = 100, message = "Title must be less than 100 characters")
    private String title;

    @NotBlank(message = "Content cannot be empty")
    private String content;

    @NotNull(message = "Mood is required")
    private String mood;

    @NotNull(message = "Visibility is required")
    private String visibility;

    private Long userId;

    public String getTitle() {
 return title;
    }
    public void setTitle(String title) {
 this.title = title;
    }

    public void setContent(String content) { this.content = content; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}