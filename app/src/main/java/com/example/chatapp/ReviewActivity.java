package com.example.chatapp;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.adapter.QuestionAdapter;

import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        RecyclerView rv = findViewById(R.id.rvReview);
        Button btnBack = findViewById(R.id.btnBackHome);

        rv.setLayoutManager(new LinearLayoutManager(this));

        // Lấy dữ liệu đã làm từ Holder
        ArrayList<Question> reviewList = QuestionDataHolder.getInstance().getListQuestions();

        if (reviewList != null) {
            QuestionAdapter adapter = new QuestionAdapter(reviewList);
            rv.setAdapter(adapter);

            // Kích hoạt chế độ xem lại (Hiện màu xanh/đỏ)
            adapter.enableReviewMode();
        }

        btnBack.setOnClickListener(v -> finish());
    }
}