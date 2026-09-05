package com.learningpath.model;

import java.util.Map;

public class DiagnosticResult {
    private int score;
    private int total;
    private double percentage;
    private String suggestedLevel;
    private Map<String, Boolean> topicProficiency;

    public DiagnosticResult(int score, int total, double percentage, String suggestedLevel, Map<String, Boolean> topicProficiency) {
        this.score = score;
        this.total = total;
        this.percentage = percentage;
        this.suggestedLevel = suggestedLevel;
        this.topicProficiency = topicProficiency;
    }

    public int getScore() { return score; }
    public int getTotal() { return total; }
    public double getPercentage() { return percentage; }
    public String getSuggestedLevel() { return suggestedLevel; }
    public Map<String, Boolean> getTopicProficiency() { return topicProficiency; }
}
