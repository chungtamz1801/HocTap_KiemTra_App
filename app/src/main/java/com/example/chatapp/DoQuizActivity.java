package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hoctap_kiemtra_app.R;
import com.example.chatapp.adapter.QuestionAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DoQuizActivity extends AppCompatActivity {

    RecyclerView rv;
    QuestionAdapter adapter;
    ArrayList<Question> questions;
    TextView tvTimer, txtExam;
    Button btnSubmit;
    CountDownTimer timer;
    FirebaseFirestore db;

    long totalTime = 15 * 60 * 1000;
    long timeRemaining = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doquiz);

        db = FirebaseFirestore.getInstance();

        rv = findViewById(R.id.rvQuestions);
        tvTimer = findViewById(R.id.tvTimer);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtExam = findViewById(R.id.txtExam);

        rv.setLayoutManager(new LinearLayoutManager(this));

        questions = new ArrayList<>();
        adapter = new QuestionAdapter(questions);
        rv.setAdapter(adapter);

        String examId = getIntent().getStringExtra("examId");

        if (examId == null || examId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không nhận được ID đề thi!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadExamData(examId);
        btnSubmit.setOnClickListener(v -> submitQuiz());
    }

    private void loadExamData(String examId) {
        Log.d("DEBUG_APP", "Đang load đề thi có ID: " + examId);

        db.collection("exams").document(examId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        if (name != null) {
                            txtExam.setText(name);
                            Log.d("DEBUG_APP", "Đã set tên đề thi: " + name);
                        } else {
                            txtExam.setText("Đề thi không có tên");
                        }

                        Long timeLimit = doc.getLong("time");
                        if (timeLimit != null && timeLimit > 0) {
                            totalTime = timeLimit * 60 * 1000;
                        }
                        startTimer(totalTime);

                        List<String> questionIds = (List<String>) doc.get("questionIds");
                        if (questionIds != null && !questionIds.isEmpty()) {
                            fetchQuestionsDetails(questionIds);
                        } else {
                            Toast.makeText(this, "Đề thi này chưa có câu hỏi!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy đề thi trên hệ thống!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DEBUG_APP", "Lỗi Firestore: " + e.getMessage());
                });
    }

    private void fetchQuestionsDetails(List<String> ids) {
        questions.clear();
        for (String id : ids) {
            db.collection("questions").document(id)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            Question q = doc.toObject(Question.class);
                            if (q != null) {
                                q.setId(doc.getId());
                                questions.add(q);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    private void startTimer(long duration) {
        if (timer != null) timer.cancel();
        timer = new CountDownTimer(duration, 1000) {
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }
            public void onFinish() {
                tvTimer.setText("00:00");
                Toast.makeText(DoQuizActivity.this, "Hết giờ!", Toast.LENGTH_LONG).show();
                submitQuiz();
            }
        }.start();
    }

    private void submitQuiz() {
        if(timer != null) timer.cancel();

        // 1. Lưu danh sách câu hỏi vào Holder để xem lại
        QuestionDataHolder.getInstance().setListQuestions(questions);

        // 2. Tính điểm
        int correctCount = 0;
        for (Question q : questions) {
            if (q.getUserAnswer() != null && q.getUserAnswer().equals(q.getCorrectAnswer())) {
                correctCount++;
            }
        }

        // 3. Tính thời gian
        long timeTakenMillis = totalTime - timeRemaining;
        long min = (timeTakenMillis / 1000) / 60;
        long sec = (timeTakenMillis / 1000) % 60;
        String timeTakenStr = String.format("%02d phút %02d giây", min, sec);

        double score = 0;
        if (questions.size() > 0) {
            score = (double) correctCount / questions.size() * 10;
        }

        // 4. Lưu lên Firebase
        SharedPreferences prefs = getSharedPreferences("QUIZ_APP", MODE_PRIVATE);
        String studentId = prefs.getString("STUDENT_ID", "UnknownID");
        String studentName = prefs.getString("FULLNAME", "UnknownName");
        String examId = getIntent().getStringExtra("examId");

        saveResultToFirestore(studentId, studentName, examId, score, timeTakenStr, questions);
        Toast.makeText(this, "Đã nộp bài!", Toast.LENGTH_SHORT).show();

        // 5. Chuyển sang ResultActivity (Gửi kèm dữ liệu)
        Intent intent = new Intent(DoQuizActivity.this, ResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putDouble("SCORE", score);
        bundle.putInt("CORRECT", correctCount);
        bundle.putInt("TOTAL", questions.size());
        bundle.putString("TIME", timeTakenStr); // Key là "TIME"

        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void saveResultToFirestore(String id, String name, String examId, double score, String timeTaken, ArrayList<Question> listQ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<Map<String, String>> detailAnswers = new ArrayList<>();
        for (Question q : listQ) {
            Map<String, String> item = new HashMap<>();
            item.put("questionId", q.getId());
            item.put("selected", q.getUserAnswer() != null ? q.getUserAnswer() : "");
            item.put("correct", q.getCorrectAnswer());
            detailAnswers.add(item);
        }

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("id", id);
        resultData.put("name", name);
        resultData.put("examId", examId != null ? examId : "unknown_exam");
        resultData.put("score", score);
        resultData.put("timeTaken", timeTaken);
        resultData.put("timestamp", new Date());
        resultData.put("details", detailAnswers);

        db.collection("student_score")
                .add(resultData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("DEBUG_APP", "Đã lưu kết quả thi!");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi lưu điểm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}