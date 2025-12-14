package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LectureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);


        Button btnManage = findViewById(R.id.btnManage); // Nút mới

        // 2. NÚT MANAGE: Gọi ExamListActivity (Code đồng nghiệp: Để thêm/sửa/xóa đề)
        btnManage.setOnClickListener(v -> {
            Intent intent = new Intent(LectureActivity.this, ExamListActivity.class);
            startActivity(intent);
        });
    }
}