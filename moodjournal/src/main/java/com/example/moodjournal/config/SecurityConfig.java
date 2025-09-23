package com.example.moodjournal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF protection, common for stateless APIs
            .csrf(csrf -> csrf.disable())
            
            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // **FIX:** Explicitly allow all OPTIONS requests for CORS pre-flight checks
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Allow unauthenticated access to static resources and auth endpoints
                .requestMatchers(
                    "/", 
                    "/*.html", 
                    "/css/**", 
                    "/js/**", 
                    "/api/auth/**"
                ).permitAll()
                
                // All other requests must be authenticated
                .anyRequest().authenticated()
            )
            
            // Configure session management to be stateless
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}

