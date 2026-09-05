package com.learningpath.model;

public class UserProfile {
    private String name;
    private String domain;
    private String experienceLevel;
    private int hoursPerWeek;
    private String preferredMedia;
    private String interests;

    public UserProfile(String name, String domain, String experienceLevel, int hoursPerWeek, String preferredMedia, String interests) {
        this.name = (name != null && !name.trim().isEmpty()) ? name : "Learner";
        this.domain = domain;
        this.experienceLevel = (experienceLevel != null) ? experienceLevel : "Beginner";
        this.hoursPerWeek = Math.max(2, hoursPerWeek);
        this.preferredMedia = (preferredMedia != null) ? preferredMedia : "Video Course";
        this.interests = (interests != null) ? interests : "";
    }

    public String getName() { return name; }
    public String getDomain() { return domain; }
    public String getExperienceLevel() { return experienceLevel; }
    public int getHoursPerWeek() { return hoursPerWeek; }
    public String getPreferredMedia() { return preferredMedia; }
    public String getInterests() { return interests; }

    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
}
