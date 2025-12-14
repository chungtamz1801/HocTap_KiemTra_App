package com.example.hoctap_kiemtra_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

        //  Check STUDENT
        db.collection("students")
                .whereEqualTo("email", userName)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Đăng nhập sinh viên
                            startActivity(new Intent(LoginActivity.this, StudentHomeActivity.class));
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
                            //Đăng nhập giảng viên
                            startActivity(new Intent(LoginActivity.this, LecturerHomeActivity.class));
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