package com.learningpath.model;

import java.util.List;

public class Roadmap {
    private String domain;
    private String userName;
    private int hoursPerWeek;
    private int totalEstimatedHours;
    private int totalWeeks;
    private double overallProgress;
    private List<Milestone> milestones;
    private DiagnosticResult diagnosticResult;

    public Roadmap(String domain, String userName, int hoursPerWeek, int totalEstimatedHours,
                   int totalWeeks, List<Milestone> milestones, DiagnosticResult diagnosticResult) {
        this.domain = domain;
        this.userName = userName;
        this.hoursPerWeek = hoursPerWeek;
        this.totalEstimatedHours = totalEstimatedHours;
        this.totalWeeks = totalWeeks;
        this.overallProgress = 0.0;
        this.milestones = milestones;
        this.diagnosticResult = diagnosticResult;
    }

    public String getDomain() { return domain; }
    public String getUserName() { return userName; }
    public int getHoursPerWeek() { return hoursPerWeek; }
    public int getTotalEstimatedHours() { return totalEstimatedHours; }
    public void setTotalEstimatedHours(int totalEstimatedHours) { this.totalEstimatedHours = totalEstimatedHours; }
    public int getTotalWeeks() { return totalWeeks; }
    public double getOverallProgress() { return overallProgress; }
    public void setOverallProgress(double overallProgress) { this.overallProgress = overallProgress; }
    public List<Milestone> getMilestones() { return milestones; }
    public DiagnosticResult getDiagnosticResult() { return diagnosticResult; }
}
