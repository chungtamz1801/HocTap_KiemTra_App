package com.example.chatapp.model;

import java.util.Date;

public class ScoreHistory {
    // Lưu ý: Tên biến phải KHỚP với tên field trên Firebase
    private String id;        // Firebase lưu là "id" (ID sinh viên)
    private double score;     // Firebase lưu là "score"
    private String timeTaken; // Firebase lưu là "timeTaken"
    private Date timestamp;   // Firebase lưu là "timestamp"

    public ScoreHistory() {} // Bắt buộc có

    public ScoreHistory(String id, double score, String timeTaken, Date timestamp) {
        this.id = id;
        this.score = score;
        this.timeTaken = timeTaken;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public double getScore() { return score; }
    public String getTimeTaken() { return timeTaken; }
    public Date getTimestamp() { return timestamp; }
}