package com.example.hoctap_kiemtra_app;

import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private String id;
    private String title;
    private String info;
    private String progress;
    private int completedItems;
    private int totalItems;
    private List<LessonItem> lessonItems;

    // Constructor rỗng cho Firebase
    public Lesson() {
        this.lessonItems = new ArrayList<>();
    }

    public Lesson(String title, String info, String progress) {
        this.title = title;
        this.info = info;
        this.progress = progress;
        this.lessonItems = new ArrayList<>();
        parseProgress();
    }

    public Lesson(String id, String title, String info, String progress, List<LessonItem> lessonItems) {
        this.id = id;
        this.title = title;
        this.info = info;
        this.progress = progress;
        this.lessonItems = lessonItems != null ? lessonItems : new ArrayList<>();
        parseProgress();
    }

    private void parseProgress() {
        if (progress != null && progress.contains("/")) {
            String[] parts = progress.split("/");
            try {
                this.completedItems = Integer.parseInt(parts[0].trim());
                this.totalItems = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                this.completedItems = 0;
                this.totalItems = 0;
            }
        }
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
        parseProgress();
    }

    public int getCompletedItems() {
        return completedItems;
    }

    public void setCompletedItems(int completedItems) {
        this.completedItems = completedItems;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<LessonItem> getLessonItems() {
        return lessonItems;
    }

    public void setLessonItems(List<LessonItem> lessonItems) {
        this.lessonItems = lessonItems;
    }

    public void addLessonItem(LessonItem item) {
        if (this.lessonItems == null) {
            this.lessonItems = new ArrayList<>();
        }
        this.lessonItems.add(item);
    }

    public void updateProgress() {
        if (lessonItems != null) {
            int completed = 0;
            for (LessonItem item : lessonItems) {
                if (item.isCompleted()) {
                    completed++;
                }
            }
            this.completedItems = completed;
            this.totalItems = lessonItems.size();
            this.progress = completed + " / " + totalItems;
        }
    }
}