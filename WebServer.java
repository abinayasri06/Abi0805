package com.learningpath.web;

import com.learningpath.core.AdaptiveEngine;
import com.learningpath.core.ContentRecommender;
import com.learningpath.core.KnowledgeGraph;
import com.learningpath.data.DataLoader;
import com.learningpath.model.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WebServer {
    private int port;
    private HttpServer server;
    private KnowledgeGraph kg;
    private ContentRecommender recommender;
    private AdaptiveEngine adaptiveEngine;

    // Active session state
    private UserProfile activeProfile;
    private DiagnosticResult activeDiagnostic;
    private Roadmap activeRoadmap;

    public WebServer(int port) {
        this.port = port;
        this.kg = new KnowledgeGraph(DataLoader.getAllTopics());
        this.recommender = new ContentRecommender(DataLoader.getAllCourses());
        this.adaptiveEngine = new AdaptiveEngine(this.kg, this.recommender);

        this.activeProfile = new UserProfile(
            "Alex Smith",
            "Artificial Intelligence & Machine Learning",
            "Beginner",
            10,
            "Video Course",
            "PyTorch, deep neural networks, transformer models, LLMs"
        );
        this.activeDiagnostic = null;
        this.activeRoadmap = adaptiveEngine.generateRoadmap(activeProfile, null);
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", new RootHandler());
        server.createContext("/api/diagnostic-questions", new DiagnosticQuestionsHandler());
        server.createContext("/api/evaluate-diagnostic", new EvaluateDiagnosticHandler());
        server.createContext("/api/generate-roadmap", new GenerateRoadmapHandler());
        server.createContext("/api/adapt-milestone", new AdaptMilestoneHandler());
        server.createContext("/api/analytics", new AnalyticsHandler());
        server.createContext("/api/export", new ExportHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("=================================================================");
        System.out.println("🚀 AI Learning Path Recommender Web Server running successfully!");
        System.out.println("👉 Access the interactive dashboard at: http://localhost:" + port);
        System.out.println("=================================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    // Handlers
    private class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            byte[] bytes = WebAssets.getIndexHtml().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private class DiagnosticQuestionsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String domain = activeProfile.getDomain();
            if (query != null && query.contains("domain=")) {
                for (String param : query.split("&")) {
                    if (param.startsWith("domain=")) {
                        domain = URLDecoder.decode(param.substring(7), StandardCharsets.UTF_8.name());
                    }
                }
            }

            List<Question> questions = DataLoader.getDiagnosticQuestions(domain);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                sb.append("{")
                  .append("\"id\":\"").append(escape(q.getId())).append("\",")
                  .append("\"topicId\":\"").append(escape(q.getTopicId())).append("\",")
                  .append("\"difficulty\":\"").append(escape(q.getDifficulty())).append("\",")
                  .append("\"questionText\":\"").append(escape(q.getQuestionText())).append("\",")
                  .append("\"options\":[");
                for (int j = 0; j < q.getOptions().size(); j++) {
                    sb.append("\"").append(escape(q.getOptions().get(j))).append("\"");
                    if (j < q.getOptions().size() - 1) sb.append(",");
                }
                sb.append("]}");
                if (i < questions.size() - 1) sb.append(",");
            }
            sb.append("]");

            sendJsonResponse(exchange, sb.toString());
        }
    }

    private class EvaluateDiagnosticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                String domain = extractString(body, "domain", activeProfile.getDomain());

                List<Question> questions = DataLoader.getDiagnosticQuestions(domain);
                Map<String, Integer> answers = new HashMap<>();

                for (Question q : questions) {
                    Integer ans = extractIntKey(body, q.getId());
                    if (ans != null) {
                        answers.put(q.getId(), ans);
                    }
                }

                activeDiagnostic = adaptiveEngine.evaluateDiagnostic(domain, answers, questions);
                activeRoadmap = adaptiveEngine.generateRoadmap(activeProfile, activeDiagnostic);

                StringBuilder sb = new StringBuilder("{");
                sb.append("\"score\":").append(activeDiagnostic.getScore()).append(",")
                  .append("\"total\":").append(activeDiagnostic.getTotal()).append(",")
                  .append("\"percentage\":").append(activeDiagnostic.getPercentage()).append(",")
                  .append("\"suggestedLevel\":\"").append(escape(activeDiagnostic.getSuggestedLevel())).append("\"")
                  .append("}");

                sendJsonResponse(exchange, sb.toString());
            }
        }
    }

    private class GenerateRoadmapHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);

                String name = extractNestedString(body, "profile", "name", activeProfile.getName());
                String domain = extractNestedString(body, "profile", "domain", activeProfile.getDomain());
                int hours = extractNestedInt(body, "profile", "hoursPerWeek", activeProfile.getHoursPerWeek());
                String media = extractNestedString(body, "profile", "preferredMedia", activeProfile.getPreferredMedia());
                String level = extractNestedString(body, "profile", "experienceLevel", activeProfile.getExperienceLevel());
                String interests = extractNestedString(body, "profile", "interests", activeProfile.getInterests());

                activeProfile = new UserProfile(name, domain, level, hours, media, interests);
                activeRoadmap = adaptiveEngine.generateRoadmap(activeProfile, activeDiagnostic);

                sendJsonResponse(exchange, serializeRoadmap(activeRoadmap));
            } else {
                sendJsonResponse(exchange, serializeRoadmap(activeRoadmap));
            }
        }
    }

    private class AdaptMilestoneHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                String milestoneId = extractString(body, "milestoneId", "m_1");
                double score = extractDouble(body, "score", 75.0);

                if (activeRoadmap != null) {
                    activeRoadmap = adaptiveEngine.adaptMilestone(activeRoadmap, milestoneId, score);
                }

                sendJsonResponse(exchange, serializeRoadmap(activeRoadmap));
            }
        }
    }

    private class AnalyticsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, Double> mastery = (activeRoadmap != null)
                ? adaptiveEngine.calculateCategoryMastery(activeRoadmap)
                : Collections.emptyMap();

            StringBuilder sb = new StringBuilder("{");
            int count = 0;
            for (Map.Entry<String, Double> e : mastery.entrySet()) {
                sb.append("\"").append(escape(e.getKey())).append("\":").append(e.getValue());
                if (++count < mastery.size()) sb.append(",");
            }
            sb.append("}");

            sendJsonResponse(exchange, sb.toString());
        }
    }

    private class ExportHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder md = new StringBuilder();
            md.append("# Personalized Learning Path Report: ").append(activeProfile.getDomain()).append("\n")
              .append("**Learner:** ").append(activeProfile.getName()).append("\n")
              .append("**Commitment:** ").append(activeProfile.getHoursPerWeek()).append(" hrs/week | ")
              .append("**Estimated Duration:** ").append(activeRoadmap != null ? activeRoadmap.getTotalWeeks() : 0).append(" Weeks\n")
              .append("**Overall Progress:** ").append(activeRoadmap != null ? activeRoadmap.getOverallProgress() : 0.0).append("%\n\n")
              .append("---\n\n## Structured Milestone Roadmap\n\n");

            if (activeRoadmap != null) {
                for (Milestone m : activeRoadmap.getMilestones()) {
                    md.append("### Milestone ").append(m.getMilestoneId().toUpperCase())
                      .append(": ").append(m.getTopicName()).append(" [").append(m.getStatus()).append("]\n")
                      .append("- Schedule: Weeks ").append(m.getStartWeek()).append("-").append(m.getEndWeek())
                      .append(" (").append(m.getEstimatedHours()).append(" Hours)\n")
                      .append("- Category & Difficulty: ").append(m.getCategory()).append(" | ").append(m.getDifficulty()).append("\n")
                      .append("- Description: ").append(m.getDescription()).append("\n\n")
                      .append("**Recommended Resources:**\n");
                    for (Course c : m.getRecommendedResources()) {
                        md.append("  - [").append(c.getTitle()).append("](").append(c.getUrl()).append(") (")
                          .append(c.getProvider()).append(", ").append(c.getMediaType()).append(", ⭐ ").append(c.getRating()).append(")\n");
                    }
                    md.append("\n---\n\n");
                }
            }

            byte[] bytes = md.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/markdown; charset=UTF-8");
            exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"learning_path.md\"");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    // Helper JSON and String serializers
    private String serializeRoadmap(Roadmap rm) {
        if (rm == null) return "{}";
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"domain\":\"").append(escape(rm.getDomain())).append("\",")
          .append("\"userName\":\"").append(escape(rm.getUserName())).append("\",")
          .append("\"hoursPerWeek\":").append(rm.getHoursPerWeek()).append(",")
          .append("\"totalEstimatedHours\":").append(rm.getTotalEstimatedHours()).append(",")
          .append("\"totalWeeks\":").append(rm.getTotalWeeks()).append(",")
          .append("\"overallProgress\":").append(rm.getOverallProgress()).append(",")
          .append("\"milestones\":[");

        List<Milestone> ms = rm.getMilestones();
        for (int i = 0; i < ms.size(); i++) {
            Milestone m = ms.get(i);
            sb.append("{")
              .append("\"milestoneId\":\"").append(escape(m.getMilestoneId())).append("\",")
              .append("\"topicId\":\"").append(escape(m.getTopicId())).append("\",")
              .append("\"topicName\":\"").append(escape(m.getTopicName())).append("\",")
              .append("\"category\":\"").append(escape(m.getCategory())).append("\",")
              .append("\"difficulty\":\"").append(escape(m.getDifficulty())).append("\",")
              .append("\"description\":\"").append(escape(m.getDescription())).append("\",")
              .append("\"estimatedHours\":").append(m.getEstimatedHours()).append(",")
              .append("\"startWeek\":").append(m.getStartWeek()).append(",")
              .append("\"endWeek\":").append(m.getEndWeek()).append(",")
              .append("\"status\":\"").append(escape(m.getStatus())).append("\",")
              .append("\"quizScore\":").append(m.getQuizScore() != null ? m.getQuizScore() : "null").append(",")
              .append("\"notes\":\"").append(escape(m.getNotes())).append("\",")
              .append("\"prerequisites\":[");

            for (int p = 0; p < m.getPrerequisites().size(); p++) {
                sb.append("\"").append(escape(m.getPrerequisites().get(p))).append("\"");
                if (p < m.getPrerequisites().size() - 1) sb.append(",");
            }
            sb.append("],\"recommendedResources\":[");

            for (int c = 0; c < m.getRecommendedResources().size(); c++) {
                Course cr = m.getRecommendedResources().get(c);
                sb.append("{")
                  .append("\"courseId\":\"").append(escape(cr.getCourseId())).append("\",")
                  .append("\"title\":\"").append(escape(cr.getTitle())).append("\",")
                  .append("\"provider\":\"").append(escape(cr.getProvider())).append("\",")
                  .append("\"mediaType\":\"").append(escape(cr.getMediaType())).append("\",")
                  .append("\"difficulty\":\"").append(escape(cr.getDifficulty())).append("\",")
                  .append("\"durationHours\":").append(cr.getDurationHours()).append(",")
                  .append("\"rating\":").append(cr.getRating()).append(",")
                  .append("\"summary\":\"").append(escape(cr.getSummary())).append("\",")
                  .append("\"url\":\"").append(escape(cr.getUrl())).append("\",")
                  .append("\"matchScore\":").append(cr.getMatchScore())
                  .append("}");
                if (c < m.getRecommendedResources().size() - 1) sb.append(",");
            }
            sb.append("]}");
            if (i < ms.size() - 1) sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }

    private void sendJsonResponse(HttpExchange exchange, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int n;
            while ((n = is.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
            return baos.toString(StandardCharsets.UTF_8.name());
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private String extractString(String json, String key, String def) {
        try {
            int idx = json.indexOf("\"" + key + "\"");
            if (idx == -1) return def;
            int colon = json.indexOf(":", idx);
            int quote1 = json.indexOf("\"", colon);
            int quote2 = json.indexOf("\"", quote1 + 1);
            return json.substring(quote1 + 1, quote2);
        } catch (Exception e) {
            return def;
        }
    }

    private int extractNestedInt(String json, String parent, String key, int def) {
        try {
            int pIdx = json.indexOf("\"" + parent + "\"");
            if (pIdx == -1) return def;
            int idx = json.indexOf("\"" + key + "\"", pIdx);
            if (idx == -1) return def;
            int colon = json.indexOf(":", idx);
            int end = colon + 1;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == ' ' || json.charAt(end) == '-')) {
                end++;
            }
            return Integer.parseInt(json.substring(colon + 1, end).trim());
        } catch (Exception e) {
            return def;
        }
    }

    private String extractNestedString(String json, String parent, String key, String def) {
        try {
            int pIdx = json.indexOf("\"" + parent + "\"");
            if (pIdx == -1) return def;
            int idx = json.indexOf("\"" + key + "\"", pIdx);
            if (idx == -1) return def;
            int colon = json.indexOf(":", idx);
            int quote1 = json.indexOf("\"", colon);
            int quote2 = json.indexOf("\"", quote1 + 1);
            return json.substring(quote1 + 1, quote2);
        } catch (Exception e) {
            return def;
        }
    }

    private double extractDouble(String json, String key, double def) {
        try {
            int idx = json.indexOf("\"" + key + "\"");
            if (idx == -1) return def;
            int colon = json.indexOf(":", idx);
            int end = colon + 1;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.' || json.charAt(end) == ' ')) {
                end++;
            }
            return Double.parseDouble(json.substring(colon + 1, end).trim());
        } catch (Exception e) {
            return def;
        }
    }

    private Integer extractIntKey(String json, String key) {
        try {
            int idx = json.indexOf("\"" + key + "\"");
            if (idx == -1) return null;
            int colon = json.indexOf(":", idx);
            int end = colon + 1;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == ' ')) {
                end++;
            }
            return Integer.parseInt(json.substring(colon + 1, end).trim());
        } catch (Exception e) {
            return null;
        }
    }
}
