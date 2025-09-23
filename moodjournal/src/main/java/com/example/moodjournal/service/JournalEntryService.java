package com.example.moodjournal.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.moodjournal.dto.UpdateJournalEntryRequest;
import com.example.moodjournal.model.JournalEntry;
import com.example.moodjournal.model.Mood;
import com.example.moodjournal.model.User;
import com.example.moodjournal.model.Visibility;
import com.example.moodjournal.repository.JournalEntryRepository;
import com.example.moodjournal.repository.UserRepository;

@Service
public class JournalEntryService {
    private final JournalEntryRepository entryRepo;
    private final UserRepository userRepo;
    private final SentimentAnalysisService sentimentService;

    public JournalEntryService(JournalEntryRepository entryRepo, UserRepository userRepo,
                              SentimentAnalysisService sentimentService) {
        this.entryRepo = entryRepo;
        this.userRepo = userRepo;
        this.sentimentService = sentimentService;
    }

    public JournalEntry create(Long userId, JournalEntry entry) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        entry.setUser(user);

        // If the user did NOT select a mood, use the AI to detect it.
        if (entry.getMood() == null) {
            Mood detectedMood = sentimentService.analyzeSentiment(entry.getContent());
            entry.setMood(detectedMood);
        }
        // If the user DID select a mood, it will already be set on the entry object.

        return entryRepo.save(entry);
    }

    // Enhanced create method that accepts only the JournalEntry with AI analysis
    public JournalEntry create(JournalEntry entry) {
        if (entry.getUser() != null && entry.getUser().getId() != null) {
            Long userId = entry.getUser().getId();
            var optUser = userRepo.findById(userId);
            if (optUser.isPresent()) { 
                entry.setUser(optUser.get());
            } else {
                throw new NoSuchElementException("User not found with id: " + userId);
            }
        } else {
            throw new IllegalArgumentException("User ID must be provided to create a journal entry.");
        }


        // Auto-detect mood if not provided
        if (entry.getMood() == null) {
            Mood detectedMood = sentimentService.analyzeSentiment(entry.getContent());
            entry.setMood(detectedMood);
        }

        return entryRepo.save(entry);
    }

    // Add method to get mood suggestions
    public Mood suggestMood(String content) {
        return sentimentService.analyzeSentiment(content);
    }

    // Add method to get confidence score
    public double getMoodConfidence(String content, Mood mood) {
        return sentimentService.getSentimentConfidence(content, mood);
    }

    // ... keep all your existing methods unchanged ...

    public List<JournalEntry> getByUser(Long userId) {
        return entryRepo.findByUserId(userId);
    }

    public List<JournalEntry> getPublicEntries(String mood) {
        if (mood != null && !mood.isEmpty()) {
            try {
                Mood moodEnum = Mood.valueOf(mood.toUpperCase());
                return entryRepo.findByMoodAndVisibility(moodEnum, Visibility.PUBLIC_ANON);
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        }
        return entryRepo.findByVisibility(Visibility.PUBLIC_ANON);
    }

    public Optional<JournalEntry> getById(Long id) {
        return entryRepo.findById(id);
    }

    public List<JournalEntry> getAll() {
        return entryRepo.findAll();
    }

    public JournalEntry update(Long id, UpdateJournalEntryRequest updated) {
        return entryRepo.findById(id).map(e -> {
            e.setTitle(updated.getTitle());
            e.setContent(updated.getContent());

            // Re-analyze mood if content changed
            if (updated.getContent() != null && !updated.getContent().equals(e.getContent())) {
                Mood suggestedMood = sentimentService.analyzeSentiment(updated.getContent());
                // Use suggested mood if no explicit mood provided
                if (updated.getMood() == null || updated.getMood().isBlank() || e.getMood() == null) {
                    e.setMood(suggestedMood);
                }
            }

            // Parse mood/visibility from strings safely
            if (updated.getMood() != null && !updated.getMood().isBlank()) {
                try {
                    e.setMood(Mood.valueOf(updated.getMood().toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    // ignore invalid mood and keep existing or suggested value
                }
            }
            if (updated.getVisibility() != null && !updated.getVisibility().isBlank()) {
                try {
                    e.setVisibility(Visibility.valueOf(updated.getVisibility().toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    // ignore invalid visibility
                }
            }
            return entryRepo.save(e);
        }).orElseThrow(() -> new NoSuchElementException("JournalEntry not found"));
    }

    public JournalEntry updateJournal(Long id, JournalEntry updatedEntry) {
        return entryRepo.findById(id).map(e -> {
            e.setTitle(updatedEntry.getTitle());
            e.setContent(updatedEntry.getContent());
            e.setMood(updatedEntry.getMood());
            e.setVisibility(updatedEntry.getVisibility());
            return entryRepo.save(e);
        }).orElseThrow(() -> new NoSuchElementException("JournalEntry not found"));
    }

    public void delete(Long id) {
        entryRepo.deleteById(id);
    }
}