package com.example.moodjournal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.moodjournal.dto.UpdateJournalEntryRequest;
import com.example.moodjournal.model.JournalEntry;
import com.example.moodjournal.service.JournalEntryService;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {
    private final JournalEntryService service;
    public JournalEntryController(JournalEntryService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<JournalEntry> create(@RequestParam Long userId, @RequestBody JournalEntry entry){
        // In a real application, you would get the user from the security context
        // For example: User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JournalEntry created = service.create(userId, entry);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/me")
    public ResponseEntity<List<JournalEntry>> myEntries(@RequestParam Long userId){
        // In a real application, you would get the user from the security context
        return ResponseEntity.ok(service.getByUser(userId));
    }

    @GetMapping("/community")
    public ResponseEntity<List<JournalEntry>> community(@RequestParam(required = false) String mood){
        return ResponseEntity.ok(service.getPublicEntries(mood));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JournalEntry> get(@PathVariable Long id){
        return service.getById(id).map(ResponseEntity::ok)
               .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<JournalEntry> update(@PathVariable Long id, @RequestBody UpdateJournalEntryRequest updated){
        return ResponseEntity.ok(service.update(id, updated));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}