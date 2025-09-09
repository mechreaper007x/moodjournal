package com.example.moodjournal.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

import com.example.moodjournal.dto.CreateJournalEntryRequest;
import com.example.moodjournal.dto.UpdateJournalEntryRequest;
import com.example.moodjournal.model.JournalEntry;
import com.example.moodjournal.model.Mood;
import com.example.moodjournal.model.Visibility;
import com.example.moodjournal.service.JournalEntryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {
    private final JournalEntryService service;
    private static final Logger log = LoggerFactory.getLogger(JournalEntryController.class);
    public JournalEntryController(JournalEntryService service) { this.service = service; }

    // POST /journal - create using request body only (user may be omitted)
    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@Valid @RequestBody CreateJournalEntryRequest req) {
        JournalEntry entry = new JournalEntry();
        entry.setTitle(req.getTitle());
        entry.setContent(req.getContent());
        if (req.getMood() != null && !req.getMood().isBlank()) {
            entry.setMood(Mood.valueOf(req.getMood().toUpperCase()));
        }
        if (req.getVisibility() != null && !req.getVisibility().isBlank()) {
            entry.setVisibility(Visibility.valueOf(req.getVisibility().toUpperCase()));
        }

        JournalEntry created;
        if (req.getUserId() != null) {
            created = service.create(req.getUserId(), entry);
        } else {
            created = service.create(entry);
        }

        URI location = URI.create(String.format("/journal/%d", created.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(created);
    }

    @GetMapping("/me")
    public ResponseEntity<List<JournalEntry>> myEntries(@RequestParam Long userId){
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
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateJournalEntryRequest updated){
        try {
            log.info("Update request for id={} payload={{}}", id, updated);
            JournalEntry result = service.update(id, updated);
            return ResponseEntity.ok(Map.of("id", result.getId(), "message", "updated"));
        } catch (NoSuchElementException e) {
            log.info("JournalEntry not found: id={}", id);
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for id={}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating JournalEntry id={}", id, e);
            String msg = e.getMessage() == null ? "Internal server error" : e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", msg));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<JournalEntry>> getAllEntries(){
        return ResponseEntity.ok(service.getAll());
    }
}