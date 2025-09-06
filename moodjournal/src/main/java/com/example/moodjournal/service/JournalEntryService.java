package com.example.moodjournal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.moodjournal.model.JournalEntry;
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

    public List<JournalEntry> getByUser(Long userId) {
        return entryRepo.findByUserId(userId);
    }

    public List<JournalEntry> getPublicEntries() {
        return entryRepo.findByVisibility(Visibility.PUBLIC_ANON);
    }

    public Optional<JournalEntry> getById(Long id) {
        return entryRepo.findById(id);
    }

    public JournalEntry update(Long id, JournalEntry updated) {
        return entryRepo.findById(id).map(e -> {
            e.setTitle(updated.getTitle());
            e.setContent(updated.getContent());
            e.setMood(updated.getMood());
            e.setVisibility(updated.getVisibility());
            return entryRepo.save(e);
        }).orElseThrow(() -> new RuntimeException("Not found"));
    }

    public void delete(Long id) {
        entryRepo.deleteById(id);
    }
}
