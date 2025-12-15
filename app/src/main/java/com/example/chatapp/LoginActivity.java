package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.example.hoctap_kiemtra_app.R;
public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        db = FirebaseFirestore.getInstance();


        btnLogin.setOnClickListener(v -> checkUser());
    }


    private void checkUser() {
        String userName = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (userName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra trong bảng STUDENTS
        db.collection("students")
                .whereEqualTo("email", userName)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // --- ĐĂNG NHẬP THÀNH CÔNG ---

                            // 1. Lấy thông tin từ Firebase xuống
                            // task.getResult().getDocuments().get(0) là dòng dữ liệu tìm thấy đầu tiên
                            com.google.firebase.firestore.DocumentSnapshot doc = task.getResult().getDocuments().get(0);


                            String studentId = doc.getString("id");
                            String fullName = doc.getString("name");

                            // 2. Lưu vào bộ nhớ tạm (SharedPreferences) để dùng ở màn hình khác
                            android.content.SharedPreferences prefs = getSharedPreferences("QUIZ_APP", MODE_PRIVATE);
                            android.content.SharedPreferences.Editor editor = prefs.edit();

                            editor.putString("STUDENT_ID", studentId);
                            editor.putString("FULLNAME", fullName);
                            editor.apply();

                            // 3. Chuyển màn hình
                            Toast.makeText(LoginActivity.this, "Xin chào: " + fullName, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, StudentActivity.class));
                            finish();

                        } else {
                            // Không phải student → check lecturer
                            checkLecturer(userName, password);
                        }
                    } else {
                        Toast.makeText(this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkLecturer(String email, String password) {
        db.collection("lecturers")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                           
                            startActivity(new Intent(LoginActivity.this, LectureActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}