package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.data.FirestoreRepo;
import com.example.chatapp.model.Exam;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class CreateExamActivity extends AppCompatActivity {

    private EditText etName, etTime, etScore;
    private Button btnCreate;
    private FirestoreRepo repo = new FirestoreRepo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exam);

        etName = findViewById(R.id.etExamName);
        etTime = findViewById(R.id.etExamTime);
        etScore = findViewById(R.id.etExamScore);
        btnCreate = findViewById(R.id.btnCreateExam);

        btnCreate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String sTime = etTime.getText().toString().trim();
            String sScore = etScore.getText().toString().trim();
            if (name.isEmpty() || sTime.isEmpty() || sScore.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            int time = Integer.parseInt(sTime);
            int score = Integer.parseInt(sScore);
            Date create = new Date();


            Exam exam = new Exam(name, time, score, create, false);

            repo.createExam(exam,
                    (OnSuccessListener<DocumentReference>) documentReference -> {
                        Toast.makeText(CreateExamActivity.this, "Tạo bộ đề thành công", Toast.LENGTH_SHORT).show();
                        // mở detail exam mới
                        Intent i = new Intent(CreateExamActivity.this, ExamDetailActivity.class);
                        i.putExtra("examId", documentReference.getId());
                        startActivity(i);
                        finish();
                    },
                    (OnFailureListener) e -> {
                        Toast.makeText(CreateExamActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
}

