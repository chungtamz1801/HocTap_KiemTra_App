package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.hoctap_kiemtra_app.ExamsFragment;
import com.example.hoctap_kiemtra_app.R;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result); // Đảm bảo layout này tồn tại và đúng tên

        // 1. Ánh xạ các View từ XML
        TextView txtTotal = findViewById(R.id.txtTotal);
        TextView txtCorrect = findViewById(R.id.txtCorrect);
        TextView txtWrong = findViewById(R.id.txtWrong);
        TextView txtScore = findViewById(R.id.txtScore);
        TextView txtTime = findViewById(R.id.txtTime);
        Button btnBackHome = findViewById(R.id.btnBackHome);

        // 2. Nhận dữ liệu từ DoQuizActivity gửi sang qua Bundle
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            // Lấy dữ liệu theo đúng Key bên DoQuizActivity gửi
            double score = extras.getDouble("SCORE", 0.0);
            int correct = extras.getInt("CORRECT", 0);
            int total = extras.getInt("TOTAL", 0);
            String timeTaken = extras.getString("TIME"); // Key là "TIME"

            // Tính số câu sai
            int wrong = total - correct;

            // 3. Hiển thị dữ liệu lên màn hình
            txtTotal.setText("Tổng số câu: " + total);
            txtCorrect.setText("Số câu đúng: " + correct);
            txtWrong.setText("Số câu sai: " + wrong);

            // Hiển thị điểm (Làm tròn 1 chữ số thập phân)
            txtScore.setText(String.format("%.1f", score));

            // Hiển thị thời gian
            txtTime.setText("Thời gian: " + (timeTaken != null ? timeTaken : "00:00"));
        }

        // Trong ResultActivity.java
        btnBackHome.setOnClickListener(v -> {
            // Thay vì gọi StudentActivity, ta gọi MainActivity (nơi chứa ExamsFragment)
            // Lưu ý: Đảm bảo bạn import đúng package của MainActivity
            Intent intent = new Intent(ResultActivity.this, com.example.hoctap_kiemtra_app.MainActivity.class);

            // Xóa hết các activity cũ để app nhẹ, tránh back lại màn hình kết quả
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            finish();
        });
        // ĐÃ XÓA ĐOẠN CODE FRAGMENT GÂY LỖI
    }
}