package com.example.hoctap_kiemtra_app;


public class LessonItem {
    private String id;
    private String title;
    private String type; // "label", "file", "scorm", "assignment", "quiz", "link"
    private String content;
    private boolean completed;
    private long timestamp;

    // Constructor rỗng cho Firebase
    public LessonItem() {
    }

    public LessonItem(String title, String type, String content) {
        this.title = title;
        this.type = type;
        this.content = content;
        this.completed = false;
        this.timestamp = System.currentTimeMillis();
    }

    public LessonItem(String id, String title, String type, String content, boolean completed) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.content = content;
        this.completed = completed;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}