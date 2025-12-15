package com.example.chatapp;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.example.hoctap_kiemtra_app.R;
public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // 1. Ánh xạ các View từ XML
        TextView txtTotal = findViewById(R.id.txtTotal);
        TextView txtCorrect = findViewById(R.id.txtCorrect);
        TextView txtWrong = findViewById(R.id.txtWrong);
        TextView txtScore = findViewById(R.id.txtScore);
        TextView txtTime = findViewById(R.id.txtTime); // Mới thêm
        Button btnBackHome = findViewById(R.id.btnBackHome);

        // 2. Nhận dữ liệu từ MainActivity gửi sang
        Intent intent = getIntent();
        int total = intent.getIntExtra("TOTAL", 0);
        int correct = intent.getIntExtra("CORRECT", 0);
        int wrong = intent.getIntExtra("WRONG", 0);
        double score = intent.getDoubleExtra("SCORE", 0.0);

        // Nhận chuỗi thời gian (MỚI)
        // Nếu không có dữ liệu thì mặc định là "00:00"
        String timeTaken = intent.getStringExtra("TIME_TAKEN");

        // 3. Hiển thị dữ liệu lên màn hình
        txtTotal.setText("Tổng số câu: " + total);
        txtCorrect.setText("Số câu đúng: " + correct);
        txtWrong.setText("Số câu sai: " + wrong);

        // Hiển thị điểm (Làm tròn 1 chữ số thập phân, ví dụ: 7.5)
        txtScore.setText(String.format("%.1f", score));

        // Hiển thị thời gian
        txtTime.setText("Thời gian làm bài: " + (timeTaken != null ? timeTaken : "N/A"));

        // 4. Xử lý nút quay về
        // Trong ResultActivity.java

        btnBackHome.setOnClickListener(v -> {
            // Đổi tên biến từ 'intent' thành 'homeIntent' để không bị trùng
            Intent homeIntent = new Intent(ResultActivity.this, StudentActivity.class);

            // Xóa stack cũ để về trang chủ sạch sẽ
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(homeIntent); // Nhớ truyền đúng tên biến mới
            finish();
        });
    }
}
//package com.example.chatapp;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
//import com.example.chatapp.model.Student;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.List;
//
//public class ResultActivity extends AppCompatActivity {
//
//    private TextView txtStudentName, txtExamName, txtTotal, txtCorrect, txtWrong, txtScore, txtTime;
//    private Button btnBackHome;
//    private RecyclerView rvReview;
//
//    private List<com.example.chatapp.Question> questions;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.hien_thi_ket_qua);
//
//        // --- Ánh xạ ---
//        txtStudentName = findViewById(R.id.txtStudentName);
//        txtExamName = findViewById(R.id.txtExamName);
//        txtScore = findViewById(R.id.txtScore);
//        btnBackHome = findViewById(R.id.btnBackHome);
//        rvReview = findViewById(R.id.rvReview);
//
//        rvReview.setLayoutManager(new LinearLayoutManager(this));
//
//        // --- Lấy dữ liệu từ Intent ---
//        Intent intent = getIntent();
//        int total = intent.getIntExtra("TOTAL", 0);
//        int correct = intent.getIntExtra("CORRECT", 0);
//        int wrong = intent.getIntExtra("WRONG", 0);
//        double score = intent.getDoubleExtra("SCORE", 0.0);
//        String timeTaken = intent.getStringExtra("TIME_TAKEN");
//
//        // --- Fix cứng sinh viên ---
//        String studentId = "SV001";
//        String studentName = "Nguyễn Văn A";
//
//        // --- Lấy examId ---
//        SharedPreferences prefs = getSharedPreferences("QUIZ_APP", MODE_PRIVATE);
//        String examId = prefs.getString("selectedExamId", null);
//
//        if (examId == null) {
//            Toast.makeText(this, "Không tìm thấy đề thi vừa làm!", Toast.LENGTH_SHORT).show();
//            txtExamName.setText("Đề thi: N/A");
//        } else {
//            // Lấy tên đề từ Firebase
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            db.collection("exams").document(examId)
//                    .get()
//                    .addOnSuccessListener(doc -> {
//                        if (doc.exists()) {
//                            String examName = doc.getString("name");
//                            txtExamName.setText("Đề thi: " + examName);
//                        } else {
//                            txtExamName.setText("Đề thi: N/A");
//                        }
//                    })
//                    .addOnFailureListener(e -> txtExamName.setText("Đề thi: N/A"));
//        }
//
//        // --- Hiển thị thông tin ---
//        txtStudentName.setText("Sinh viên: " + studentName);
//        txtTotal.setText("Tổng số câu: " + total);
//        txtCorrect.setText("Số câu đúng: " + correct);
//        txtWrong.setText("Số câu sai: " + wrong);
//        txtScore.setText(String.format("%.1f", score));
//        txtTime.setText("Thời gian làm bài: " + (timeTaken != null ? timeTaken : "N/A"));
//        QuestionAdapter adapter = new QuestionAdapter(questions);
//        rvReview.setAdapter(adapter);
//
//        // --- Nút về Home ---
//        btnBackHome.setOnClickListener(v -> {
//            Intent homeIntent = new Intent(ResultActivity.this, HomeActivity.class);
//            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(homeIntent);
//            finish();
//        });
//    }
//}
