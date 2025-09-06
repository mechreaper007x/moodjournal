package com.example.moodjournal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Mood Journal API is running 🚀. Try /api/auth/register or /api/entries";
    }
}
