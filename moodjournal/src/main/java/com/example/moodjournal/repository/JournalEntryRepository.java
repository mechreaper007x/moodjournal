package com.example.moodjournal.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.moodjournal.model.JournalEntry;
import com.example.moodjournal.model.Mood;
import com.example.moodjournal.model.Visibility;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    List<JournalEntry> findByUserId(Long userId);
    List<JournalEntry> findByVisibility(Visibility visibility);
    List<JournalEntry> findByMoodAndVisibility(Mood mood, Visibility visibility);
}