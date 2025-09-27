package com.example.moodjournal.service;

import com.example.moodjournal.model.Mood;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class SentimentAnalysisService {

    private final StanfordCoreNLP pipeline;

    public SentimentAnalysisService() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        this.pipeline = new StanfordCoreNLP(props);
    }

    public Mood analyzeSentiment(String text) {
        if (text == null || text.isEmpty()) {
            return Mood.NEUTRAL;
        }

        Annotation annotation = pipeline.process(text);
        for (var sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            switch (sentiment) {
                case "Very positive":
                case "Positive":
                    return Mood.HAPPY;
                case "Neutral":
                    return Mood.NEUTRAL;
                case "Negative":
                    return Mood.SAD;
                case "Very negative":
                    return Mood.ANGRY;
                default:
                    return Mood.NEUTRAL;
            }
        }

        return Mood.NEUTRAL;
    }

    public double getSentimentConfidence(String text, Mood mood) {
        if (text == null || text.isEmpty()) {
            return 0.5;
        }

        Annotation annotation = pipeline.process(text);
        for (var sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            int sentimentScore = RNNCoreAnnotations.getPredictedClass(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
            // The sentiment score is an integer from 0 to 4.
            // We can normalize this to a 0.0 to 1.0 scale.
            return sentimentScore / 4.0;
        }

        return 0.5; // Default confidence
    }
}