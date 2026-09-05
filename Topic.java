package com.learningpath.model;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    private String id;
    private String name;
    private String domain;
    private String category;
    private String difficulty;
    private int estimatedHours;
    private List<String> prerequisites;
    private String description;

    public Topic(String id, String name, String domain, String category, String difficulty, int estimatedHours, List<String> prerequisites, String description) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.category = category;
        this.difficulty = difficulty;
        this.estimatedHours = estimatedHours;
        this.prerequisites = prerequisites != null ? prerequisites : new ArrayList<>();
        this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDomain() { return domain; }
    public String getCategory() { return category; }
    public String getDifficulty() { return difficulty; }
    public int getEstimatedHours() { return estimatedHours; }
    public List<String> getPrerequisites() { return prerequisites; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return name + " (" + difficulty + ", " + estimatedHours + "h)";
    }
}
