package com.example.moodjournal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.moodjournal.model.User;
import com.example.moodjournal.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserRepository userRepo;
    public UserController(UserRepository userRepo) { this.userRepo = userRepo; }
 
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        // NOTE: simple save for now. Add validation and password hashing later.
        User saved = userRepo.save(user);
        saved.setPassword(null);
        return ResponseEntity.ok(saved);
    }
}
