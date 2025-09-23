package com.example.moodjournal.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        try {
            // Check if user already exists
            Optional<User> existingUser = userRepo.findByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            User saved = userRepo.save(user);
            saved.setPassword(null); // Don't return password in the response
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User loginRequest) {
        try {
            Optional<User> user = userRepo.findByEmail(loginRequest.getEmail());
            if (user.isPresent() && user.get().getPassword().equals(loginRequest.getPassword())) {
                User loggedInUser = user.get();
                loggedInUser.setPassword(null); // Don't return password
                return ResponseEntity.ok(loggedInUser);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }   
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestParam Long userId) {
        try {
            Optional<User> user = userRepo.findById(userId);
            if (user.isPresent()) {
                User currentUser = user.get();
                currentUser.setPassword(null); // Don't return password
                return ResponseEntity.ok(currentUser);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
