package com.example.moodjournal.util;

public final class PromptConstants {

    private PromptConstants() {}

    public static final String EMOTION_BREAKDOWN_PROMPT = "Analyze the user's journal entry. Identify the top 3-5 primary emotions present in the text. Provide your response as a valid JSON array of objects, where each object has two keys: 'emotion' (string) and 'score' (a number from 0 to 100). The scores should represent the intensity of each emotion. Do not include any emotions with a score below 10. Example: [{\"emotion\": \"Sadness\", \"score\": 75}, {\"emotion\": \"Hope\", \"score\": 25}]";

    public static final String DAILY_QUOTE_PROMPT = "You are a source of wisdom. Provide a single, short, uplifting quote about self-reflection, mindfulness, or personal growth. The quote must be real and attributed to a known person. Format the response as a JSON object with two keys: 'quote' and 'author'. Example: {\"quote\": \"The unexamined life is not worth living.\", \"author\": \"Socrates\"}";

    public static final String SUGGEST_MOOD_PROMPT = "Analyze the user's journal entry and suggest the most fitting mood. The mood must be one of the following values: HAPPY, SAD, ANGRY, CALM, ANXIOUS, ENERGETIC, CONTENT, EXCITED. Respond with a single JSON object with one key, 'mood', and the suggested mood as its value. Example: {\"mood\": \"SAD\"}";
}