// Add this new service: moodjournal/src/main/java/com/example/moodjournal/service/SentimentAnalysisService.java

package com.example.moodjournal.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.moodjournal.model.Mood;

@Service
public class SentimentAnalysisService {
    
    // Simple word-based sentiment analysis
    private static final Map<String, Mood> POSITIVE_WORDS = new HashMap<>();
    private static final Map<String, Mood> NEGATIVE_WORDS = new HashMap<>();
    private static final Map<String, Mood> NEUTRAL_WORDS = new HashMap<>();
    private static final Map<String, String> EMPATHY_WORDS = new HashMap<>();
    private static final Map<String, String> OPTIMISM_WORDS = new HashMap<>();
    private static final Map<String, String> REFLECTION_WORDS = new HashMap<>();

    static {
        // Populate the new maps inside the static block

        // Happy words
        String[] happyWords = {"happy", "joy", "excited", "amazing", "wonderful", "fantastic", 
                              "great", "awesome", "love", "perfect", "brilliant", "excellent", 
                              "cheerful", "delighted", "thrilled", "ecstatic", "blissful", "good", "nice", "pleasant"};
        for (String word : happyWords) {
            POSITIVE_WORDS.put(word, Mood.HAPPY);
        }
        
        // Calm/Chill words
        String[] calmWords = {"calm", "peaceful", "relaxed", "serene", "tranquil", "content", 
                             "satisfied", "comfortable", "easy", "chill", "zen", "balanced", "quiet", "still"};
        for (String word : calmWords) {
            POSITIVE_WORDS.put(word, Mood.CALM);
        }
        
        // Sad words
        String[] sadWords = {"sad", "depressed", "unhappy", "miserable", "down", "blue", 
                            "heartbroken", "disappointed", "gloomy", "melancholy", "crying", 
                            "tears", "lonely", "empty", "hopeless", "hurt", "pain", "sorrow"};
        for (String word : sadWords) {
            NEGATIVE_WORDS.put(word, Mood.SAD);
        }
        
        // Anxious words
        String[] anxiousWords = {"anxious", "worried", "nervous", "stressed", "panic", "fear", 
                                "scared", "overwhelmed", "tension", "uneasy", "restless", 
                                "troubled", "concerned", "frightened", "worry", "stress"};
        for (String word : anxiousWords) {
            NEGATIVE_WORDS.put(word, Mood.ANXIOUS);
        }
        
        // Angry words
        String[] angryWords = {"angry", "mad", "furious", "rage", "hate", "annoyed", "irritated", 
                              "frustrated", "pissed", "outraged", "livid", "enraged", "bitter", "upset"};
        for (String word : angryWords) {
            NEGATIVE_WORDS.put(word, Mood.ANGRY);
        }
        
        // Neutral words
        String[] neutralWords = {"okay", "fine", "normal", "regular", "usual", "average", "meh", 
                                "nothing", "same", "routine", "typical", "ordinary", "alright"};
        for (String word : neutralWords) {
            NEUTRAL_WORDS.put(word, Mood.NEUTRAL);
        }

        String[] empathyWords = {"understand", "share", "connect", "feel for", "sympathize", "relate"};
        for (String word : empathyWords) EMPATHY_WORDS.put(word, "Empathy");

        String[] optimismWords = {"hopeful", "future", "tomorrow", "believe", "opportunity", "growth", "positive"};
        for (String word : optimismWords) OPTIMISM_WORDS.put(word, "Optimism");

        String[] reflectionWords = {"realized", "thought", "remember", "wonder", "reflect", "myself", "re-evaluate"};
        for (String word : reflectionWords) REFLECTION_WORDS.put(word, "Reflection");
    }
    
    public Mood analyzeSentiment(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Mood.NEUTRAL;
        }
        
        // Convert to lowercase and remove punctuation
        String cleanText = text.toLowerCase().replaceAll("[^a-zA-Z\\s]", " ");
        String[] words = cleanText.split("\\s+");
        
        Map<Mood, Integer> moodScores = new HashMap<>();
        for (Mood mood : Mood.values()) {
            moodScores.put(mood, 0);
        }
        
        // Score based on word matches
        for (String word : words) {
            if (POSITIVE_WORDS.containsKey(word)) {
                Mood mood = POSITIVE_WORDS.get(word);
                moodScores.put(mood, moodScores.get(mood) + 2);
            } else if (NEGATIVE_WORDS.containsKey(word)) {
                Mood mood = NEGATIVE_WORDS.get(word);
                moodScores.put(mood, moodScores.get(mood) + 2);
            } else if (NEUTRAL_WORDS.containsKey(word)) {
                moodScores.put(Mood.NEUTRAL, moodScores.get(Mood.NEUTRAL) + 1);
            }
        }
        
        // Find the mood with highest score
        Mood detectedMood = moodScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Mood.NEUTRAL);
        
        // If no mood detected or tie, return NEUTRAL
        int maxScore = moodScores.get(detectedMood);
        if (maxScore == 0) {
            return Mood.NEUTRAL;
        }
        
        return detectedMood;
    }
    
    public double getSentimentConfidence(String text, Mood detectedMood) {
        if (text == null || text.trim().isEmpty()) {
            return 0.0;
        }
        
        String cleanText = text.toLowerCase().replaceAll("[^a-zA-Z\\s]", " ");
        String[] words = cleanText.split("\\s+");
        
        int moodWordCount = 0;
        int totalWords = words.length;
        
        for (String word : words) {
            if (POSITIVE_WORDS.containsKey(word) || NEGATIVE_WORDS.containsKey(word) || NEUTRAL_WORDS.containsKey(word)) {
                moodWordCount++;
            }
        }
        
        return totalWords > 0 ? (double) moodWordCount / totalWords : 0.0;
    }

    public java.util.List<String> analyzeAdditionalSigns(String text) {
        java.util.Set<String> detectedSigns = new java.util.HashSet<>();
        if (text == null || text.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }

        String cleanText = text.toLowerCase().replaceAll("[^a-zA-Z\\s]", " ");
        String[] words = cleanText.split("\\s+");

        for (String word : words) {
            if (EMPATHY_WORDS.containsKey(word)) {
                detectedSigns.add(EMPATHY_WORDS.get(word));
            }
            if (OPTIMISM_WORDS.containsKey(word)) {
                detectedSigns.add(OPTIMISM_WORDS.get(word));
            }
            if (REFLECTION_WORDS.containsKey(word)) {
                detectedSigns.add(REFLECTION_WORDS.get(word));
            }
        }
        return new java.util.ArrayList<>(detectedSigns);
    }
}