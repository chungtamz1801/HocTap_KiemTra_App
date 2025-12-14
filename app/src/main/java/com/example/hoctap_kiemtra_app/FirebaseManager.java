package com.example.hoctap_kiemtra_app;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
    private static final String COLLECTION_LESSONS = "lessons";

    private FirebaseFirestore db;
    private static FirebaseManager instance;

    private FirebaseManager() {
        db = FirebaseFirestore.getInstance();
    }

    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    // Lưu một lesson lên Firebase
    public void saveLesson(Lesson lesson, OnCompleteListener listener) {
        Map<String, Object> lessonData = new HashMap<>();
        lessonData.put("title", lesson.getTitle());
        lessonData.put("info", lesson.getInfo());
        lessonData.put("progress", lesson.getProgress());
        lessonData.put("completedItems", lesson.getCompletedItems());
        lessonData.put("totalItems", lesson.getTotalItems());
        lessonData.put("timestamp", System.currentTimeMillis());

        // Chuyển đổi list lesson items
        List<Map<String, Object>> itemsList = new ArrayList<>();
        if (lesson.getLessonItems() != null) {
            for (LessonItem item : lesson.getLessonItems()) {
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("title", item.getTitle());
                itemData.put("type", item.getType());
                itemData.put("content", item.getContent());
                itemData.put("completed", item.isCompleted());
                itemData.put("timestamp", item.getTimestamp());
                itemsList.add(itemData);
            }
        }
        lessonData.put("lessonItems", itemsList);

        if (lesson.getId() != null && !lesson.getId().isEmpty()) {
            // Cập nhật lesson đã tồn tại
            db.collection(COLLECTION_LESSONS)
                    .document(lesson.getId())
                    .set(lessonData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Lesson updated successfully");
                        if (listener != null) listener.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error updating lesson", e);
                        if (listener != null) listener.onFailure(e.getMessage());
                    });
        } else {
            // Thêm lesson mới
            db.collection(COLLECTION_LESSONS)
                    .add(lessonData)
                    .addOnSuccessListener(documentReference -> {
                        lesson.setId(documentReference.getId());
                        Log.d(TAG, "Lesson added with ID: " + documentReference.getId());
                        if (listener != null) listener.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error adding lesson", e);
                        if (listener != null) listener.onFailure(e.getMessage());
                    });
        }
    }

    // Lưu nhiều lessons cùng lúc
    public void saveLessons(List<Lesson> lessons, OnCompleteListener listener) {
        if (lessons == null || lessons.isEmpty()) {
            if (listener != null) listener.onFailure("Danh sách lessons trống");
            return;
        }

        final int[] count = {0};
        final int total = lessons.size();

        for (Lesson lesson : lessons) {
            saveLesson(lesson, new OnCompleteListener() {
                @Override
                public void onSuccess() {
                    count[0]++;
                    if (count[0] == total) {
                        if (listener != null) listener.onSuccess();
                    }
                }

                @Override
                public void onFailure(String error) {
                    Log.e(TAG, "Error saving lesson: " + error);
                    count[0]++;
                    if (count[0] == total) {
                        if (listener != null) listener.onFailure("Một số lessons không được lưu");
                    }
                }
            });
        }
    }

    // Lấy tất cả lessons từ Firebase
    public void getAllLessons(OnDataLoadListener listener) {
        db.collection(COLLECTION_LESSONS)
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Lesson> lessons = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Lesson lesson = documentToLesson(document);
                        lessons.add(lesson);
                    }
                    Log.d(TAG, "Loaded " + lessons.size() + " lessons");
                    if (listener != null) listener.onDataLoaded(lessons);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading lessons", e);
                    if (listener != null) listener.onError(e.getMessage());
                });
    }

    // Lấy một lesson theo ID
    public void getLesson(String lessonId, OnLessonLoadListener listener) {
        db.collection(COLLECTION_LESSONS)
                .document(lessonId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Lesson lesson = documentToLesson(documentSnapshot);
                        if (listener != null) listener.onLessonLoaded(lesson);
                    } else {
                        if (listener != null) listener.onError("Lesson không tồn tại");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading lesson", e);
                    if (listener != null) listener.onError(e.getMessage());
                });
    }

    // Xóa một lesson
    public void deleteLesson(String lessonId, OnCompleteListener listener) {
        db.collection(COLLECTION_LESSONS)
                .document(lessonId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Lesson deleted successfully");
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting lesson", e);
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    // Cập nhật tiến trình của lesson
    public void updateLessonProgress(String lessonId, String progress, OnCompleteListener listener) {
        db.collection(COLLECTION_LESSONS)
                .document(lessonId)
                .update("progress", progress)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Progress updated successfully");
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating progress", e);
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    // Cập nhật trạng thái hoàn thành của một lesson item
    public void updateLessonItemStatus(String lessonId, int itemIndex, boolean completed, OnCompleteListener listener) {
        getLesson(lessonId, new OnLessonLoadListener() {
            @Override
            public void onLessonLoaded(Lesson lesson) {
                if (lesson.getLessonItems() != null && itemIndex < lesson.getLessonItems().size()) {
                    lesson.getLessonItems().get(itemIndex).setCompleted(completed);
                    lesson.updateProgress();
                    saveLesson(lesson, listener);
                } else {
                    if (listener != null) listener.onFailure("Item không tồn tại");
                }
            }

            @Override
            public void onError(String error) {
                if (listener != null) listener.onFailure(error);
            }
        });
    }

    // Chuyển đổi DocumentSnapshot thành Lesson object
    private Lesson documentToLesson(com.google.firebase.firestore.DocumentSnapshot document) {
        String id = document.getId();
        String title = document.getString("title");
        String info = document.getString("info");
        String progress = document.getString("progress");

        List<LessonItem> lessonItems = new ArrayList<>();
        List<Map<String, Object>> itemsList = (List<Map<String, Object>>) document.get("lessonItems");
        if (itemsList != null) {
            for (Map<String, Object> itemData : itemsList) {
                LessonItem item = new LessonItem();
                item.setTitle((String) itemData.get("title"));
                item.setType((String) itemData.get("type"));
                item.setContent((String) itemData.get("content"));
                item.setCompleted(Boolean.TRUE.equals(itemData.get("completed")));
                Object timestamp = itemData.get("timestamp");
                if (timestamp instanceof Long) {
                    item.setTimestamp((Long) timestamp);
                }
                lessonItems.add(item);
            }
        }

        return new Lesson(id, title, info, progress, lessonItems);
    }

    // Interfaces cho callbacks
    public interface OnCompleteListener {
        void onSuccess();
        void onFailure(String error);
    }

    public interface OnDataLoadListener {
        void onDataLoaded(List<Lesson> lessons);
        void onError(String error);
    }

    public interface OnLessonLoadListener {
        void onLessonLoaded(Lesson lesson);
        void onError(String error);
    }
}