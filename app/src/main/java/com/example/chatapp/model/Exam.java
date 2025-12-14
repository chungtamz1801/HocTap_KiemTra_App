package com.example.chatapp.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Exam {
    private String id;
    private String name;
    private int time; // minutes
    private int score;
    private boolean active;

    private Date createdAt;
    private Date updatedAt;
    private List<String> questionIds = new ArrayList<>();

    public Exam() {}

//    public Exam(String name, int time, int score) {
//        this.name = name;
//        this.time = time;
//        this.score = score;
//    }

    public Exam(String name, int time, int score, Date createdAt, Date updatedAt) {
        this.name = name;
        this.time = time;
        this.score = score;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Exam(String name, int time, int score, Date createdAt, boolean active) {
        this.name = name;
        this.time = time;
        this.score = score;
        this.createdAt = createdAt;
        this.active = false;
    }

    // getters / setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getTime() { return time; }
    public void setTime(int time) { this.time = time; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public List<String> getQuestionIds() { return questionIds; }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setQuestionIds(List<String> questionIds) { this.questionIds = questionIds; }
}
