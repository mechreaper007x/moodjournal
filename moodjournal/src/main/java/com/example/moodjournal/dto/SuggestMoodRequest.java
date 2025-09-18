package com.example.moodjournal.dto;

import lombok.Data;

// You can add Lombok annotations if you have it in your project
// import lombok.Data;

@Data
public class SuggestMoodRequest {
    private String content;

    // --- Standard Getters and Setters ---
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}