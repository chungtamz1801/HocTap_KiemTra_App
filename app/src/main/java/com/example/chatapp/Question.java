package com.example.chatapp;

import com.google.firebase.firestore.Exclude;
import java.util.Map;

public class Question {
    private String id;
    private String content; // Nội dung câu hỏi
    private Map<String, String> answers; // Map chứa A, B, C, D
    private String correctAnswer; // Đáp án đúng (A, B, C hoặc D)

    // Biến này chỉ dùng cho App (để lưu đáp án người dùng chọn), không lưu lên Firestore
    @Exclude
    private String userAnswer = "";

    public Question() { }

    public Question(String content, Map<String, String> answers, String correctAnswer) {
        this.content = content;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
    }

    // --- GETTER & SETTER ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Map<String, String> getAnswers() { return answers; }
    public void setAnswers(Map<String, String> answers) { this.answers = answers; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }

    // --- HELPER METHOD (Để code cũ của bạn gọi q.getA() cho tiện) ---
    @Exclude public String getA() { return answers != null ? answers.get("A") : ""; }
    @Exclude public String getB() { return answers != null ? answers.get("B") : ""; }
    @Exclude public String getC() { return answers != null ? answers.get("C") : ""; }
    @Exclude public String getD() { return answers != null ? answers.get("D") : ""; }
}