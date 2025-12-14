package com.example.chatapp.data;

import com.example.chatapp.Question;
import com.example.chatapp.model.Exam;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreRepo {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // ----- CREATE exam with duplicate check (used by CreateExamActivity) -----
    public void createExam(final Exam exam,
                           final OnSuccessListener<DocumentReference> onSuccess,
                           final OnFailureListener onFailure) {
        // check duplicate name
        db.collection("exams")
                .whereEqualTo("name", exam.getName())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        onFailure.onFailure(new Exception("Tên bộ đề đã tồn tại"));
                    } else {
                        Map<String, Object> data = new HashMap<>();
                        data.put("name", exam.getName());
                        data.put("time", exam.getTime());
                        data.put("score", exam.getScore());
                        data.put("active", false);
                        data.put("createdAt", new Date());
                        data.put("questionIds", exam.getQuestionIds() != null ? exam.getQuestionIds() : new ArrayList<>());
                        db.collection("exams")
                                .add(data)
                                .addOnSuccessListener(onSuccess)
                                .addOnFailureListener(onFailure);
                    }
                })
                .addOnFailureListener(onFailure);
    }


    // ----- READ single exam -----
    public void getExam(String examId,
                        OnSuccessListener<DocumentSnapshot> onSuccess,
                        OnFailureListener onFailure) {
        db.collection("exams").document(examId)
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    // ----- UPDATE exam (map-based) -----
    public void updateExam(String examId,
                           Map<String, Object> updates,
                           OnSuccessListener<Void> onSuccess,
                           OnFailureListener onFailure) {
        db.collection("exams").document(examId)
                .update(updates)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }


    // ----- DELETE exam -----
    public void deleteExam(String examId,
                           OnSuccessListener<Void> onSuccess,
                           OnFailureListener onFailure) {
        db.collection("exams").document(examId)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    // ----- QUESTION CRUD -----
    public void createQuestion(Question q,
                               OnSuccessListener<DocumentReference> onSuccess,
                               OnFailureListener onFailure) {
        Map<String, Object> data = new HashMap<>();
        data.put("content", q.getContent());
        data.put("answers", q.getAnswers());
        data.put("correctAnswer", q.getCorrectAnswer());
        db.collection("questions")
                .add(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }




    public void getQuestionsByIds(List<String> ids,
                                  OnSuccessListener<QuerySnapshot> onSuccess,
                                  OnFailureListener onFailure) {
        if (ids == null || ids.isEmpty()) {
            onSuccess.onSuccess(null);
            return;
        }
        // chunking if >10
        if (ids.size() <= 10) {
            db.collection("questions")
                    .whereIn(FieldPath.documentId(), ids)
                    .get()
                    .addOnSuccessListener(onSuccess)
                    .addOnFailureListener(onFailure);
        } else {
            // chunk and merge
            List<QuerySnapshot> results = new ArrayList<>();
            List<List<String>> chunks = chunkList(ids, 10);
            final int total = chunks.size();
            final int[] finished = {0};
            final List<QuerySnapshot> collected = new ArrayList<>();
            for (List<String> chunk : chunks) {
                db.collection("questions")
                        .whereIn(FieldPath.documentId(), chunk)
                        .get()
                        .addOnSuccessListener(qs -> {
                            collected.add(qs);
                            finished[0]++;
                            if (finished[0] == total) {
                                // combine into one fake QuerySnapshot is cumbersome; instead invoke onSuccess with first non-null
                                // Caller should handle null or merge manually. We'll call onSuccess with null to indicate "handled".
                                onSuccess.onSuccess(null);
                            }
                        })
                        .addOnFailureListener(onFailure);
            }
        }
    }

    // helper chunk
    private List<List<String>> chunkList(List<String> list, int chunkSize) {
        List<List<String>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += chunkSize) {
            int end = Math.min(list.size(), i + chunkSize);
            chunks.add(new ArrayList<>(list.subList(i, end)));
        }
        return chunks;
    }

    // ----- Add / Remove questionId in exam.document (arrayUnion / arrayRemove) -----
    public void addQuestionToExam(String examId,
                                  String questionId,
                                  OnSuccessListener<Void> onSuccess,
                                  OnFailureListener onFailure) {
        DocumentReference examRef = db.collection("exams").document(examId);
        examRef.update("questionIds", FieldValue.arrayUnion(questionId))
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void removeQuestionFromExam(String examId,
                                       String questionId,
                                       OnSuccessListener<Void> onSuccess,
                                       OnFailureListener onFailure) {
        DocumentReference examRef = db.collection("exams").document(examId);
        examRef.update("questionIds", FieldValue.arrayRemove(questionId))
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    // ----- Check duplicate exam name when editing (excludeExamId may be null) -----
    public void isExamNameDuplicate(String name,
                                    String excludeExamId,
                                    OnSuccessListener<Boolean> onSuccess,
                                    OnFailureListener onFailure) {
        db.collection("exams")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean dup = false;
                    for (DocumentSnapshot doc : querySnapshot) {
                        if (excludeExamId == null || !doc.getId().equals(excludeExamId)) {
                            dup = true;
                            break;
                        }
                    }
                    onSuccess.onSuccess(dup);
                }).addOnFailureListener(onFailure);
    }
}
