package com.learningpath.model;

import java.util.List;

public class Question {
    private String id;
    private String domain;
    private String topicId;
    private String difficulty;
    private String questionText;
    private List<String> options;
    private int correctIndex;
    private String explanation;

    public Question(String id, String domain, String topicId, String difficulty,
                    String questionText, List<String> options, int correctIndex, String explanation) {
        this.id = id;
        this.domain = domain;
        this.topicId = topicId;
        this.difficulty = difficulty;
        this.questionText = questionText;
        this.options = options;
        this.correctIndex = correctIndex;
        this.explanation = explanation;
    }

    public String getId() { return id; }
    public String getDomain() { return domain; }
    public String getTopicId() { return topicId; }
    public String getDifficulty() { return difficulty; }
    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public int getCorrectIndex() { return correctIndex; }
    public String getExplanation() { return explanation; }
}
