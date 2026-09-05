package com.learningpath.model;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String courseId;
    private String topicId;
    private String title;
    private String provider;
    private String mediaType;
    private String difficulty;
    private int durationHours;
    private double rating;
    private String summary;
    private String url;
    private List<String> tags;
    private double matchScore;

    public Course(String courseId, String topicId, String title, String provider, String mediaType,
                  String difficulty, int durationHours, double rating, String summary, String url, List<String> tags) {
        this.courseId = courseId;
        this.topicId = topicId;
        this.title = title;
        this.provider = provider;
        this.mediaType = mediaType;
        this.difficulty = difficulty;
        this.durationHours = durationHours;
        this.rating = rating;
        this.summary = summary;
        this.url = url;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.matchScore = 0.0;
    }

    public String getCourseId() { return courseId; }
    public String getTopicId() { return topicId; }
    public String getTitle() { return title; }
    public String getProvider() { return provider; }
    public String getMediaType() { return mediaType; }
    public String getDifficulty() { return difficulty; }
    public int getDurationHours() { return durationHours; }
    public double getRating() { return rating; }
    public String getSummary() { return summary; }
    public String getUrl() { return url; }
    public List<String> getTags() { return tags; }
    public double getMatchScore() { return matchScore; }
    public void setMatchScore(double matchScore) { this.matchScore = matchScore; }

    @Override
    public String toString() {
        return title + " [" + provider + " | " + mediaType + " | " + rating + "★]";
    }
}
