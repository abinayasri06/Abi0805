package com.learningpath.core;

import com.learningpath.model.*;

import java.util.*;

public class AdaptiveEngine {
    private KnowledgeGraph knowledgeGraph;
    private ContentRecommender recommender;

    public AdaptiveEngine(KnowledgeGraph kg, ContentRecommender recommender) {
        this.knowledgeGraph = kg;
        this.recommender = recommender;
    }

    public DiagnosticResult evaluateDiagnostic(String domain, Map<String, Integer> userAnswers, List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            return new DiagnosticResult(0, 0, 0.0, "Beginner", Collections.emptyMap());
        }

        int score = 0;
        int total = questions.size();
        Map<String, Boolean> topicProficiency = new HashMap<>();

        for (Question q : questions) {
            Integer ans = userAnswers.get(q.getId());
            boolean isCorrect = (ans != null && ans == q.getCorrectIndex());
            if (isCorrect) {
                score++;
            }
            topicProficiency.put(q.getTopicId(), isCorrect);
        }

        double pct = Math.round(((double) score / total) * 1000.0) / 10.0;
        String level;
        if (pct >= 80.0) {
            level = "Advanced";
        } else if (pct >= 50.0) {
            level = "Intermediate";
        } else {
            level = "Beginner";
        }

        return new DiagnosticResult(score, total, pct, level, topicProficiency);
    }

    public Roadmap generateRoadmap(UserProfile profile, DiagnosticResult diagnostic) {
        String domain = profile.getDomain();
        int hoursPerWeek = Math.max(2, profile.getHoursPerWeek());

        List<Topic> sortedTopics = knowledgeGraph.getTopologicalSort(domain);
        List<Milestone> milestones = new ArrayList<>();

        Map<String, Boolean> diagProficiency = (diagnostic != null) ? diagnostic.getTopicProficiency() : Collections.emptyMap();

        int cumulativeHours = 0;
        int currentWeek = 1;

        for (int i = 0; i < sortedTopics.size(); i++) {
            Topic topic = sortedTopics.get(i);
            int baseHours = topic.getEstimatedHours();

            boolean masteredInDiag = diagProficiency.getOrDefault(topic.getId(), false);
            int adjustedHours;
            String initialStatus;

            if (masteredInDiag) {
                adjustedHours = Math.max(4, (int) (baseHours * 0.6));
                initialStatus = "Fast-Tracked";
            } else {
                adjustedHours = baseHours;
                initialStatus = "Not Started";
            }

            List<Course> recCourses = recommender.recommendForTopic(topic.getId(), profile, 2);

            int startWeek = currentWeek;
            int weeksNeeded = Math.max(1, (int) Math.ceil((double) adjustedHours / hoursPerWeek));
            int endWeek = startWeek + weeksNeeded - 1;

            Milestone milestone = new Milestone(
                "m_" + (i + 1),
                topic.getId(),
                topic.getName(),
                topic.getCategory(),
                topic.getDifficulty(),
                topic.getDescription(),
                topic.getPrerequisites(),
                adjustedHours,
                startWeek,
                endWeek,
                initialStatus,
                recCourses
            );

            milestones.add(milestone);
            cumulativeHours += adjustedHours;
            currentWeek = endWeek + 1;
        }

        int totalWeeks = currentWeek - 1;
        return new Roadmap(domain, profile.getName(), hoursPerWeek, cumulativeHours, totalWeeks, milestones, diagnostic);
    }

    public Roadmap adaptMilestone(Roadmap roadmap, String milestoneId, double quizScore) {
        for (Milestone m : roadmap.getMilestones()) {
            if (m.getMilestoneId().equalsIgnoreCase(milestoneId)) {
                m.setQuizScore(quizScore);

                if (quizScore < 60.0) {
                    m.setStatus("Needs Remediation");
                    if (!m.isRemedialAdded()) {
                        m.setRemedialAdded(true);
                        m.setEstimatedHours(m.getEstimatedHours() + 4);

                        Course remedialCourse = new Course(
                            "rem_" + m.getTopicId(),
                            m.getTopicId(),
                            "Targeted Concept Reinforcement & Drills: " + m.getTopicName(),
                            "Adaptive AI Remediation Lab",
                            "Interactive / Hands-on",
                            "Beginner",
                            4,
                            5.0,
                            "Step-by-step remedial exercises to master foundational concepts before advancing.",
                            "https://www.khanacademy.org/",
                            Arrays.asList("remedial", "practice", "reinforcement")
                        );
                        m.getRecommendedResources().add(0, remedialCourse);
                        m.setNotes("Remedial reinforcement injected due to score < 60%. Please review key prerequisites.");
                    }
                } else if (quizScore >= 85.0) {
                    m.setStatus("Mastered (Honors)");
                    m.setNotes("Outstanding mastery achieved! Prerequisites thoroughly understood.");
                } else {
                    m.setStatus("Completed");
                    m.setNotes("Milestone successfully verified and completed.");
                }
            }
        }

        // Recalculate progress
        List<Milestone> list = roadmap.getMilestones();
        if (!list.isEmpty()) {
            int completed = 0;
            int totalHours = 0;
            for (Milestone m : list) {
                totalHours += m.getEstimatedHours();
                if ("Completed".equals(m.getStatus()) ||
                    "Mastered (Honors)".equals(m.getStatus()) ||
                    "Fast-Tracked".equals(m.getStatus())) {
                    completed++;
                }
            }
            roadmap.setTotalEstimatedHours(totalHours);
            double progress = Math.round(((double) completed / list.size()) * 1000.0) / 10.0;
            roadmap.setOverallProgress(progress);
        }

        return roadmap;
    }

    public Map<String, Double> calculateCategoryMastery(Roadmap roadmap) {
        Map<String, List<Double>> catScores = new HashMap<>();

        for (Milestone m : roadmap.getMilestones()) {
            String cat = m.getCategory();
            catScores.putIfAbsent(cat, new ArrayList<>());

            String status = m.getStatus();
            Double score = m.getQuizScore();

            if ("Mastered (Honors)".equals(status)) {
                catScores.get(cat).add(score != null ? score : 95.0);
            } else if ("Completed".equals(status)) {
                catScores.get(cat).add(score != null ? score : 75.0);
            } else if ("Fast-Tracked".equals(status)) {
                catScores.get(cat).add(85.0);
            } else if ("Needs Remediation".equals(status)) {
                catScores.get(cat).add(score != null ? score : 50.0);
            } else {
                catScores.get(cat).add(15.0); // baseline familiarity
            }
        }

        Map<String, Double> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<Double>> entry : catScores.entrySet()) {
            double sum = 0.0;
            for (Double d : entry.getValue()) sum += d;
            double avg = Math.round((sum / entry.getValue().size()) * 10.0) / 10.0;
            result.put(entry.getKey(), avg);
        }
        return result;
    }
}
