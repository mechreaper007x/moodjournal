package com.example.moodjournal.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.moodjournal.dto.UpdateJournalEntryRequest;
import com.example.moodjournal.model.JournalEntry;
import com.example.moodjournal.model.Mood;
import com.example.moodjournal.model.Visibility;
import com.example.moodjournal.repository.JournalEntryRepository;
import com.example.moodjournal.repository.UserRepository;

@Service
public class JournalEntryService {
    private final JournalEntryRepository entryRepo;
    private final UserRepository userRepo;

    public JournalEntryService(JournalEntryRepository entryRepo, UserRepository userRepo) {
        this.entryRepo = entryRepo;
        this.userRepo = userRepo;
    }

    public JournalEntry create(Long userId, JournalEntry entry) {
        userRepo.findById(userId).ifPresent(entry::setUser);
        return entryRepo.save(entry);
    }

    // Overloaded create method that accepts only the JournalEntry.
    // If the entry contains a user with an id, we set it; otherwise save as-is.
    public JournalEntry create(JournalEntry entry) {
        if (entry.getUser() != null && entry.getUser().getId() != null) {
            Long userId = entry.getUser().getId();
            // If user exists, set the managed user; otherwise clear the reference so JPA doesn't try to persist a transient user
            var optUser = userRepo.findById(userId);
            if (optUser.isPresent()) {
                entry.setUser(optUser.get());
            } else {
                entry.setUser(null);
            }
        }

        return entryRepo.save(entry);
    }

    public List<JournalEntry> getByUser(Long userId) {
        return entryRepo.findByUserId(userId);
    }

    public List<JournalEntry> getPublicEntries(String mood) {
        if (mood != null && !mood.isEmpty()) {
            try {
                Mood moodEnum = Mood.valueOf(mood.toUpperCase());
                return entryRepo.findByMoodAndVisibility(moodEnum, Visibility.PUBLIC_ANON);
            } catch (IllegalArgumentException e) {
                // Handle invalid mood string
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
            // parse mood/visibility from strings safely
            if (updated.getMood() != null && !updated.getMood().isBlank()) {
                try {
                    e.setMood(Mood.valueOf(updated.getMood().toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    // ignore invalid mood and leave existing value
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

    // Accept a full JournalEntry payload and apply changes to the existing entry
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