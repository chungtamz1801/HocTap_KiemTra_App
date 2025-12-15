package com.example.chatapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hoctap_kiemtra_app.R;

/**
 * Màn hình splash - Hiển thị khi mở app
 * Kiểm tra trạng thái đăng nhập và chuyển hướng tự động
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Delay và chuyển màn hình
        new Handler().postDelayed(() -> {
            checkLoginStatus();
        }, SPLASH_DURATION);
    }

    /**
     * Kiểm tra trạng thái đăng nhập
     */
    private void checkLoginStatus() {
        SharedPreferences prefs = getSharedPreferences("QUIZ_APP", MODE_PRIVATE);
        boolean isRemembered = prefs.getBoolean("REMEMBER_ME", false);
        String userType = prefs.getString("USER_TYPE", "");

        Intent intent;

        if (isRemembered && !userType.isEmpty()) {
            // Đã đăng nhập và chọn ghi nhớ
            if (userType.equals("STUDENT")) {
                intent = new Intent(this, com.example.hoctap_kiemtra_app.MainActivityNew.class);
            } else {
                intent = new Intent(this, LectureActivity.class);
            }
        } else {
            // Chưa đăng nhập hoặc không chọn ghi nhớ
            intent = new Intent(this, LoginActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}