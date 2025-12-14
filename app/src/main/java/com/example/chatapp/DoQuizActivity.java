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
    TextView tvTimer, txtExam; // Khai báo txtExam
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

        // Ánh xạ View
        rv = findViewById(R.id.rvQuestions);
        tvTimer = findViewById(R.id.tvTimer);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtExam = findViewById(R.id.txtExam); // Đảm bảo ID này khớp với XML

        rv.setLayoutManager(new LinearLayoutManager(this));

        questions = new ArrayList<>();
        adapter = new QuestionAdapter(questions);
        rv.setAdapter(adapter);

        // 1. CHỈ NHẬN ID TỪ HOME GỬI SANG (Không tự query active nữa)
        String examId = getIntent().getStringExtra("examId");

        if (examId == null || examId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không nhận được ID đề thi!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Load dữ liệu dựa trên ID đó
        loadExamData(examId);

        btnSubmit.setOnClickListener(v -> submitQuiz());
    }

    private void loadExamData(String examId) {
        Log.d("DEBUG_APP", "Đang load đề thi có ID: " + examId);

        db.collection("exams").document(examId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        // --- CẬP NHẬT TÊN BỘ ĐỀ ---
                        String name = doc.getString("name");
                        if (name != null) {
                            txtExam.setText(name); // Gán tên vào TextView
                            Log.d("DEBUG_APP", "Đã set tên đề thi: " + name);
                        } else {
                            txtExam.setText("Đề thi không có tên");
                        }

                        // --- CẬP NHẬT THỜI GIAN ---
                        Long timeLimit = doc.getLong("time");
                        if (timeLimit != null && timeLimit > 0) {
                            totalTime = timeLimit * 60 * 1000;
                        }
                        startTimer(totalTime);

                        // --- LOAD CÂU HỎI ---
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

        // Lưu vào Holder để xem lại ngay lập tức (Logic cũ của bạn - Giữ nguyên)
        QuestionDataHolder.getInstance().setListQuestions(questions);

        int correctCount = 0;
        int wrongCount = 0;
        for (Question q : questions) {
            if (q.getUserAnswer() != null && q.getUserAnswer().equals(q.getCorrectAnswer())) {
                correctCount++;
            } else {
                wrongCount++;
            }
        }

        // Tính toán thời gian và điểm
        long timeTakenMillis = totalTime - timeRemaining;
        long min = (timeTakenMillis / 1000) / 60;
        long sec = (timeTakenMillis / 1000) % 60;
        String timeTakenStr = String.format("%02d phút %02d giây", min, sec);

        double score = 0;
        if (questions.size() > 0) {
            score = (double) correctCount / questions.size() * 10;
        }

        // --- BƯỚC MỚI: LƯU LÊN FIREBASE ---
        // Lấy ID sinh viên từ SharedPreferences (đã lưu lúc Login)
        SharedPreferences prefs = getSharedPreferences("QUIZ_APP", MODE_PRIVATE);
        String studentId = prefs.getString("STUDENT_ID", "UnknownID");
        String studentName = prefs.getString("FULLNAME", "UnknownName");

        // Lấy ExamID từ Intent ban đầu
        String examId = getIntent().getStringExtra("examId");

        // Gọi hàm lưu
        saveResultToFirestore(studentId, studentName, examId, score, timeTakenStr, questions);

        // Chuyển màn hình
        Intent intent = new Intent(DoQuizActivity.this, ResultActivity.class);
        intent.putExtra("TOTAL", questions.size());
        intent.putExtra("CORRECT", correctCount);
        intent.putExtra("WRONG", wrongCount);
        intent.putExtra("SCORE", score);
        intent.putExtra("TIME_TAKEN", timeTakenStr);
        startActivity(intent);
        finish();
    }
    // --- HÀM LƯU KẾT QUẢ ---
    private void saveResultToFirestore(String id, String name, String examId, double score, String timeTaken, ArrayList<Question> listQ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 1. Chuẩn bị danh sách câu trả lời chi tiết
        List<Map<String, String>> detailAnswers = new ArrayList<>();
        for (Question q : listQ) {
            Map<String, String> item = new HashMap<>();
            item.put("questionId", q.getId()); // ID câu hỏi (cần thiết để truy vết)
            item.put("selected", q.getUserAnswer() != null ? q.getUserAnswer() : ""); // Đáp án SV chọn
            item.put("correct", q.getCorrectAnswer()); // Đáp án đúng (lưu luôn để tiện so sánh sau này)
            detailAnswers.add(item);
        }

        // 2. Đóng gói dữ liệu tổng
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("id", id);
        resultData.put("name",name);
        resultData.put("examId", examId != null ? examId : "unknown_exam");
        resultData.put("score", score);
        resultData.put("timeTaken", timeTaken);
        resultData.put("timestamp", new Date()); // Thời điểm nộp bài
        resultData.put("details", detailAnswers); // Lưu chi tiết bài làm

        // 3. Đẩy lên bảng "student_score"
        db.collection("student_score")
                .add(resultData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Đã lưu kết quả thi!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi lưu điểm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}