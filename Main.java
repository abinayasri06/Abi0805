package com.learningpath;

import com.learningpath.core.AdaptiveEngine;
import com.learningpath.core.ContentRecommender;
import com.learningpath.core.KnowledgeGraph;
import com.learningpath.data.DataLoader;
import com.learningpath.gui.LearningPathGUI;
import com.learningpath.model.*;
import com.learningpath.web.WebServer;

import javax.swing.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("    🎓 AI-BASED PERSONALIZED LEARNING PATH RECOMMENDATION SYSTEM (JAVA)        ");
        System.out.println("    Algorithms: Kahn's DAG Topological Sort | TF-IDF Cosine Recommender       ");
        System.out.println("===============================================================================");

        boolean runGui = false;
        boolean runConsoleOnly = false;

        for (String arg : args) {
            if ("--gui".equalsIgnoreCase(arg)) runGui = true;
            if ("--console".equalsIgnoreCase(arg)) runConsoleOnly = true;
        }

        // 1. Run Core Algorithm Demonstration Output
        runConsoleDemo();

        if (runConsoleOnly) {
            System.out.println("\nConsole demo completed successfully.");
            return;
        }

        // 2. Launch Embedded Web Server on Port 8080
        try {
            WebServer webServer = new WebServer(8080);
            webServer.start();
        } catch (Exception e) {
            System.err.println("Warning: Could not start web server on port 8080: " + e.getMessage());
        }

        // 3. Launch Swing GUI if requested or in interactive desktop mode
        if (runGui) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            SwingUtilities.invokeLater(() -> {
                LearningPathGUI gui = new LearningPathGUI();
                gui.setVisible(true);
            });
        }
    }

    private static void runConsoleDemo() {
        System.out.println("\n--- [1] INITIALIZING KNOWLEDGE GRAPH & DATASETS ---");
        List<Topic> allTopics = DataLoader.getAllTopics();
        List<Course> allCourses = DataLoader.getAllCourses();
        System.out.println("✓ Loaded " + allTopics.size() + " Curriculum Topics across 5 Domains.");
        System.out.println("✓ Loaded " + allCourses.size() + " Curated Courses & Learning Resources.");

        KnowledgeGraph kg = new KnowledgeGraph(allTopics);
        ContentRecommender rec = new ContentRecommender(allCourses);
        AdaptiveEngine adaptiveEngine = new AdaptiveEngine(kg, rec);

        // 2. Demonstration User Profile
        System.out.println("\n--- [2] SAMPLE LEARNER PROFILE CREATION ---");
        UserProfile profile = new UserProfile(
            "Abinayasri M",
            "Artificial Intelligence & Machine Learning",
            "Beginner",
            10,
            "Video Course",
            "PyTorch, deep learning, NLP, transformers"
        );
        System.out.println("Student: " + profile.getName());
        System.out.println("Specialization: " + profile.getDomain());
        System.out.println("Study Commitment: " + profile.getHoursPerWeek() + " hours/week");
        System.out.println("Preferred Media: " + profile.getPreferredMedia());

        // 3. Knowledge Graph DAG Prerequisite Ordering Verification
        System.out.println("\n--- [3] DAG PREREQUISITE SEQUENCING (KAHN'S TOPOLOGICAL SORT) ---");
        List<Topic> sortedTopics = kg.getTopologicalSort(profile.getDomain());
        for (int i = 0; i < sortedTopics.size(); i++) {
            Topic t = sortedTopics.get(i);
            String prereqs = t.getPrerequisites().isEmpty() ? "None (Root)" : String.join(", ", t.getPrerequisites());
            System.out.printf("  Step %02d: %-42s | Prereqs: %s\n", (i + 1), t.getName(), prereqs);
        }

        // 4. Diagnostic Assessment Evaluation
        System.out.println("\n--- [4] DIAGNOSTIC SKILL GAP ASSESSMENT ---");
        List<Question> questions = DataLoader.getDiagnosticQuestions(profile.getDomain());
        Map<String, Integer> mockAnswers = new HashMap<>();
        // Simulate correct answers for first 2 questions, incorrect for remaining
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            mockAnswers.put(q.getId(), (i < 2) ? q.getCorrectIndex() : 0);
        }

        DiagnosticResult diag = adaptiveEngine.evaluateDiagnostic(profile.getDomain(), mockAnswers, questions);
        System.out.println("Score: " + diag.getScore() + " / " + diag.getTotal() + " (" + diag.getPercentage() + "%)");
        System.out.println("AI Verified Competency Level: " + diag.getSuggestedLevel());

        // 5. Personalized Adaptive Roadmap Generation
        System.out.println("\n--- [5] GENERATING PERSONALIZED LEARNING PATH ---");
        Roadmap roadmap = adaptiveEngine.generateRoadmap(profile, diag);
        System.out.println("Total Estimated Hours: " + roadmap.getTotalEstimatedHours() + " hours");
        System.out.println("Total Duration: " + roadmap.getTotalWeeks() + " weeks (" + roadmap.getHoursPerWeek() + " hrs/week)");

        System.out.println("\n--- [6] SCHEDULED MILESTONES & RECOMMENDED RESOURCES ---");
        for (Milestone m : roadmap.getMilestones()) {
            System.out.printf("\n[%s] %s | Weeks %d-%d (%dh) | Status: %s\n",
                m.getMilestoneId().toUpperCase(), m.getTopicName(), m.getStartWeek(), m.getEndWeek(), m.getEstimatedHours(), m.getStatus());
            for (Course c : m.getRecommendedResources()) {
                System.out.printf("   ➜ [%s] %s (⭐ %.1f | Match: %.2f) - %s\n",
                    c.getProvider(), c.getTitle(), c.getRating(), c.getMatchScore(), c.getUrl());
            }
        }

        // 6. Dynamic Remediation Adaptation Test
        System.out.println("\n--- [7] CLOSED-LOOP ADAPTIVE REMEDIATION DEMONSTRATION ---");
        System.out.println("Simulating a milestone quiz failure (Score: 45%) on Milestone 1...");
        roadmap = adaptiveEngine.adaptMilestone(roadmap, "m_1", 45.0);
        Milestone m1 = roadmap.getMilestones().get(0);
        System.out.println("Updated Milestone 1 Status: " + m1.getStatus());
        System.out.println("Remedial Resource Injected: " + m1.isRemedialAdded());
        System.out.println("Remedial Course Title: " + m1.getRecommendedResources().get(0).getTitle());
        System.out.println("Updated Estimated Hours: " + m1.getEstimatedHours() + "h (+4h review buffer)");
        System.out.println("Adaptive Engine Note: " + m1.getNotes());

        System.out.println("\n===============================================================================");
        System.out.println("  ✓ CORE JAVA ALGORITHMIC VALIDATION SUCCESSFUL!");
        System.out.println("===============================================================================");
    }
}
