package com.learningpath.core;

import com.learningpath.model.Course;
import com.learningpath.model.UserProfile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentRecommender {
    private List<Course> allCourses;
    private Set<String> stopWords;
    private Map<String, Double> idfMap;
    private Map<String, Map<String, Double>> courseTfidfVectors;

    public ContentRecommender(List<Course> courses) {
        this.allCourses = courses;
        initStopWords();
        buildTfidfIndex();
    }

    private void initStopWords() {
        stopWords = new HashSet<>(Arrays.asList(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and",
            "any", "are", "as", "at", "be", "because", "been", "before", "being", "below",
            "between", "both", "but", "by", "can", "did", "do", "does", "doing", "don",
            "down", "during", "each", "few", "for", "from", "further", "had", "has", "have",
            "having", "he", "her", "here", "hers", "herself", "him", "himself", "his",
            "how", "i", "if", "in", "into", "is", "it", "its", "itself", "just", "me",
            "more", "most", "my", "myself", "no", "nor", "not", "now", "of", "off", "on",
            "once", "only", "or", "other", "our", "ours", "ourselves", "out", "over",
            "own", "s", "same", "she", "should", "so", "some", "such", "t", "than", "that",
            "the", "their", "theirs", "them", "themselves", "then", "there", "these", "they",
            "this", "those", "through", "to", "too", "under", "until", "up", "very", "was",
            "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why",
            "will", "with", "you", "your", "yours", "yourself", "yourselves"
        ));
    }

    private List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) return tokens;

        Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");
        Matcher matcher = pattern.matcher(text.toLowerCase());
        while (matcher.find()) {
            String token = matcher.group();
            if (!stopWords.contains(token) && token.length() > 1) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private String createCourseCorpusString(Course c) {
        StringBuilder sb = new StringBuilder();
        sb.append(c.getTitle()).append(" ")
          .append(c.getSummary()).append(" ")
          .append(c.getProvider()).append(" ")
          .append(c.getMediaType()).append(" ")
          .append(c.getDifficulty()).append(" ");
        for (String tag : c.getTags()) {
            sb.append(tag).append(" ");
        }
        return sb.toString();
    }

    private void buildTfidfIndex() {
        idfMap = new HashMap<>();
        courseTfidfVectors = new HashMap<>();
        int totalDocs = allCourses.size();

        Map<String, Integer> docFreq = new HashMap<>();
        Map<String, List<String>> courseTokens = new HashMap<>();

        for (Course c : allCourses) {
            List<String> tokens = tokenize(createCourseCorpusString(c));
            courseTokens.put(c.getCourseId(), tokens);

            Set<String> uniqueTokens = new HashSet<>(tokens);
            for (String t : uniqueTokens) {
                docFreq.put(t, docFreq.getOrDefault(t, 0) + 1);
            }
        }

        // Calculate IDF
        for (Map.Entry<String, Integer> entry : docFreq.entrySet()) {
            double idf = Math.log(1.0 + ((double) totalDocs / entry.getValue()));
            idfMap.put(entry.getKey(), idf);
        }

        // Calculate TF-IDF vectors for all courses
        for (Course c : allCourses) {
            List<String> tokens = courseTokens.get(c.getCourseId());
            Map<String, Double> vec = new HashMap<>();

            Map<String, Integer> tfCount = new HashMap<>();
            for (String t : tokens) {
                tfCount.put(t, tfCount.getOrDefault(t, 0) + 1);
            }

            int docSize = Math.max(1, tokens.size());
            for (Map.Entry<String, Integer> entry : tfCount.entrySet()) {
                double tf = (double) entry.getValue() / docSize;
                double idf = idfMap.getOrDefault(entry.getKey(), 1.0);
                vec.put(entry.getKey(), tf * idf);
            }
            courseTfidfVectors.put(c.getCourseId(), vec);
        }
    }

    private Map<String, Double> vectorizeUser(UserProfile profile) {
        String userText = (profile.getDomain() + " " +
                           profile.getInterests() + " " +
                           profile.getPreferredMedia() + " " +
                           profile.getExperienceLevel()).trim();

        List<String> tokens = tokenize(userText);
        Map<String, Double> vec = new HashMap<>();
        if (tokens.isEmpty()) return vec;

        Map<String, Integer> tfCount = new HashMap<>();
        for (String t : tokens) {
            tfCount.put(t, tfCount.getOrDefault(t, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : tfCount.entrySet()) {
            double tf = (double) entry.getValue() / tokens.size();
            double idf = idfMap.getOrDefault(entry.getKey(), 1.0);
            vec.put(entry.getKey(), tf * idf);
        }
        return vec;
    }

    private double calculateCosineSimilarity(Map<String, Double> vecA, Map<String, Double> vecB) {
        if (vecA.isEmpty() || vecB.isEmpty()) return 0.0;

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (Map.Entry<String, Double> e : vecA.entrySet()) {
            double a = e.getValue();
            normA += a * a;
            if (vecB.containsKey(e.getKey())) {
                dotProduct += a * vecB.get(e.getKey());
            }
        }

        for (double b : vecB.values()) {
            normB += b * b;
        }

        double denominator = Math.sqrt(normA) * Math.sqrt(normB);
        return (denominator < 1e-9) ? 0.0 : (dotProduct / denominator);
    }

    public List<Course> recommendForTopic(String topicId, UserProfile profile, int topK) {
        List<Course> candidates = new ArrayList<>();
        for (Course c : allCourses) {
            if (c.getTopicId().equalsIgnoreCase(topicId)) {
                candidates.add(c);
            }
        }

        if (candidates.isEmpty()) {
            // fallback: return any courses tagged with topic
            for (Course c : allCourses) {
                if (c.getTags().contains(topicId)) {
                    candidates.add(c);
                }
            }
        }

        Map<String, Double> userVec = vectorizeUser(profile);

        for (Course c : candidates) {
            Map<String, Double> courseVec = courseTfidfVectors.getOrDefault(c.getCourseId(), Collections.emptyMap());
            double baseSim = calculateCosineSimilarity(userVec, courseVec);

            // Media type match bonus
            double mediaBonus = c.getMediaType().equalsIgnoreCase(profile.getPreferredMedia()) ? 0.25 : 0.0;

            // Difficulty match bonus
            double diffBonus = c.getDifficulty().equalsIgnoreCase(profile.getExperienceLevel()) ? 0.15 : 0.0;

            // Rating bonus
            double ratingBonus = Math.max(0.0, (c.getRating() - 4.0) * 0.2);

            double finalScore = Math.round((baseSim + mediaBonus + diffBonus + ratingBonus) * 1000.0) / 1000.0;
            c.setMatchScore(finalScore);
        }

        candidates.sort((c1, c2) -> Double.compare(c2.getMatchScore(), c1.getMatchScore()));
        return candidates.subList(0, Math.min(topK, candidates.size()));
    }
}
