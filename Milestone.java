package com.learningpath.model;

import java.util.ArrayList;
import java.util.List;

public class Milestone {
    private String milestoneId;
    private String topicId;
    private String topicName;
    private String category;
    private String difficulty;
    private String description;
    private List<String> prerequisites;
    private int estimatedHours;
    private int startWeek;
    private int endWeek;
    private String status; // Not Started, Fast-Tracked, Completed, Needs Remediation, Mastered (Honors)
    private Double quizScore;
    private List<Course> recommendedResources;
    private boolean remedialAdded;
    private String notes;

    public Milestone(String milestoneId, String topicId, String topicName, String category,
                     String difficulty, String description, List<String> prerequisites,
                     int estimatedHours, int startWeek, int endWeek, String status,
                     List<Course> recommendedResources) {
        this.milestoneId = milestoneId;
        this.topicId = topicId;
        this.topicName = topicName;
        this.category = category;
        this.difficulty = difficulty;
        this.description = description;
        this.prerequisites = prerequisites != null ? prerequisites : new ArrayList<>();
        this.estimatedHours = estimatedHours;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.status = status;
        this.quizScore = null;
        this.recommendedResources = recommendedResources != null ? recommendedResources : new ArrayList<>();
        this.remedialAdded = false;
        this.notes = "";
    }

    public String getMilestoneId() { return milestoneId; }
    public String getTopicId() { return topicId; }
    public String getTopicName() { return topicName; }
    public String getCategory() { return category; }
    public String getDifficulty() { return difficulty; }
    public String getDescription() { return description; }
    public List<String> getPrerequisites() { return prerequisites; }
    public int getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(int estimatedHours) { this.estimatedHours = estimatedHours; }
    public int getStartWeek() { return startWeek; }
    public int getEndWeek() { return endWeek; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getQuizScore() { return quizScore; }
    public void setQuizScore(Double quizScore) { this.quizScore = quizScore; }
    public List<Course> getRecommendedResources() { return recommendedResources; }
    public boolean isRemedialAdded() { return remedialAdded; }
    public void setRemedialAdded(boolean remedialAdded) { this.remedialAdded = remedialAdded; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
