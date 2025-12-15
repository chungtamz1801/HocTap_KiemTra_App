package com.example.chatapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.hoctap_kiemtra_app.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private CheckBox cbRememberMe;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ẩn ActionBar nếu có
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Khởi tạo views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        progressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();
        prefs = getSharedPreferences("QUIZ_APP", MODE_PRIVATE);

        // Kiểm tra đã login chưa
        checkAutoLogin();

        // Xử lý nút đăng nhập
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInput(email, password)) {
                performLogin(email, password);
            }
        });
    }

    /**
     * Kiểm tra tự động đăng nhập
     */
    private void checkAutoLogin() {
        boolean isRemembered = prefs.getBoolean("REMEMBER_ME", false);
        String savedEmail = prefs.getString("SAVED_EMAIL", "");
        String savedPassword = prefs.getString("SAVED_PASSWORD", "");

        if (isRemembered && !savedEmail.isEmpty() && !savedPassword.isEmpty()) {
            etEmail.setText(savedEmail);
            etPassword.setText(savedPassword);
            cbRememberMe.setChecked(true);

            // Tự động đăng nhập
            performLogin(savedEmail, savedPassword);
        }
    }

    /**
     * Kiểm tra dữ liệu đầu vào
     */
    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Thực hiện đăng nhập
     */
    private void performLogin(String email, String password) {
        showLoading(true);

        // Kiểm tra trong bảng STUDENTS trước
        db.collection("students")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Đăng nhập thành công với tài khoản sinh viên
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        handleStudentLogin(doc, email, password);
                    } else {
                        // Kiểm tra trong bảng LECTURERS
                        checkLecturerLogin(email, password);
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showError("Lỗi kết nối: " + e.getMessage());
                });
    }

    /**
     * Xử lý đăng nhập sinh viên
     */
    private void handleStudentLogin(DocumentSnapshot doc, String email, String password) {
        String studentId = doc.getString("id");
        String fullName = doc.getString("name");

        // Lưu thông tin vào SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("USER_TYPE", "STUDENT");
        editor.putString("STUDENT_ID", studentId);
        editor.putString("FULLNAME", fullName);
        editor.putString("EMAIL", email);

        // Lưu thông tin đăng nhập nếu chọn "Ghi nhớ"
        if (cbRememberMe.isChecked()) {
            editor.putBoolean("REMEMBER_ME", true);
            editor.putString("SAVED_EMAIL", email);
            editor.putString("SAVED_PASSWORD", password);
        } else {
            editor.putBoolean("REMEMBER_ME", false);
            editor.remove("SAVED_EMAIL");
            editor.remove("SAVED_PASSWORD");
        }

        editor.apply();

        showLoading(false);
        showSuccess("Xin chào, " + fullName + "!");

        // Chuyển đến MainActivity mới với Bottom Navigation
        Intent intent = new Intent(LoginActivity.this, com.example.hoctap_kiemtra_app.MainActivityNew.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Kiểm tra đăng nhập giảng viên
     */
    private void checkLecturerLogin(String email, String password) {
        db.collection("lecturers")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    showLoading(false);

                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Đăng nhập thành công với tài khoản giảng viên
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        handleLecturerLogin(doc, email, password);
                    } else {
                        // Sai email hoặc mật khẩu
                        showError("Email hoặc mật khẩu không đúng!");
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showError("Lỗi kết nối: " + e.getMessage());
                });
    }

    /**
     * Xử lý đăng nhập giảng viên
     */
    private void handleLecturerLogin(DocumentSnapshot doc, String email, String password) {
        String lecturerId = doc.getString("id");
        String fullName = doc.getString("name");

        // Lưu thông tin vào SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("USER_TYPE", "LECTURER");
        editor.putString("LECTURER_ID", lecturerId);
        editor.putString("FULLNAME", fullName);
        editor.putString("EMAIL", email);

        // Lưu thông tin đăng nhập nếu chọn "Ghi nhớ"
        if (cbRememberMe.isChecked()) {
            editor.putBoolean("REMEMBER_ME", true);
            editor.putString("SAVED_EMAIL", email);
            editor.putString("SAVED_PASSWORD", password);
        } else {
            editor.putBoolean("REMEMBER_ME", false);
            editor.remove("SAVED_EMAIL");
            editor.remove("SAVED_PASSWORD");
        }

        editor.apply();

        showSuccess("Xin chào, " + fullName + "!");

        // Chuyển đến LectureActivity
        Intent intent = new Intent(LoginActivity.this, LectureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Hiển thị/Ẩn loading
     */
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            btnLogin.setText("");
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            btnLogin.setText("Đăng nhập");
        }
    }

    /**
     * Hiển thị thông báo lỗi
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Hiển thị thông báo thành công
     */
    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        // Thoát ứng dụng khi nhấn back ở màn hình login
        finishAffinity();
    }
}