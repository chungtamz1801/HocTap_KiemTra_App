package com.example.chatapp.model;

import java.util.Map;

public class SingleQuestion {

    private String id;
    private String content;
    private Map<String, String> answers;
    private String correctAnswer;

    public SingleQuestion() {}

    public SingleQuestion(String content, Map<String, String> answers, String correctAnswer) {
        this.content = content;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
    }

    public SingleQuestion(String id, String content, Map<String, String> answers, String correctAnswer) {
        this.id = id;
        this.content = content;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Map<String, String> getAnswers() { return answers; }
    public void setAnswers(Map<String, String> answers) { this.answers = answers; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
}
