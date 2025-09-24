package com.example.moodjournal.controller;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.moodjournal.model.User;
import com.example.moodjournal.service.UserService;
import com.example.moodjournal.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);

            // Generate a token for the new user immediately
            final UserDetails userDetails = userDetailsService.loadUserByUsername(registeredUser.getEmail());
            final String jwt = jwtUtil.generateToken(userDetails);

            // Never send the password back
            registeredUser.setPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("token", jwt, "user", registeredUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.get("email"), credentials.get("password"))
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
        }

        final String email = credentials.get("email");
        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        final String jwt = jwtUtil.generateToken(userDetails);
        
        // The user is already authenticated, so we can fetch their details directly.
        // The redundant password check in `userService.login` is no longer needed.
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found after authentication")); // Changed to RuntimeException for broader handling

        user.setPassword(null); // Ensure password is not sent in the response
        return ResponseEntity.ok(Map.of("token", jwt, "user", user));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = userService.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new NoSuchElementException("User not found from security context"));
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }
}