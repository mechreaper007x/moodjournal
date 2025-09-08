// moodjournal/src/main/java/com/example/moodjournal/controller/HomeController.java

package com.example.moodjournal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    // This method handles the request to the root URL "/"
    @GetMapping("/")
    public String home() {
        return "Welcome to the Mood Journal Application!";
    }

    // This new method handles the request to "/hello"
    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    // This method handles the request to "/journal"
    @GetMapping("/journal")
    public String journal() {
        return "Welcome to your Journal!";
    }
}