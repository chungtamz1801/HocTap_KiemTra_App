package com.example.chatapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class QuestionCreateActivity extends AppCompatActivity {

    private EditText etContent, etA, etB, etC, etD;
    private RadioGroup rgCorrect;
    private Button btnSave;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_create);

        db = FirebaseFirestore.getInstance();

        etContent = findViewById(R.id.etContent);
        etA = findViewById(R.id.etA);
        etB = findViewById(R.id.etB);
        etC = findViewById(R.id.etC);
        etD = findViewById(R.id.etD);
        rgCorrect = findViewById(R.id.rgCorrect);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        String content = etContent.getText().toString().trim();
        String a = etA.getText().toString().trim();
        String b = etB.getText().toString().trim();
        String c = etC.getText().toString().trim();
        String d = etD.getText().toString().trim();

        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(a) ||
                TextUtils.isEmpty(b) || TextUtils.isEmpty(c) || TextUtils.isEmpty(d)) {
            Toast.makeText(this, "Nhập đầy đủ dữ liệu!", Toast.LENGTH_SHORT).show();
            return;
        }

        int idCorrect = rgCorrect.getCheckedRadioButtonId();

        String correct = "";
        if (idCorrect == R.id.rbA) correct = "A";
        else if (idCorrect == R.id.rbB) correct = "B";
        else if (idCorrect == R.id.rbC) correct = "C";
        else if (idCorrect == R.id.rbD) correct = "D";

        if (correct.isEmpty()) {
            Toast.makeText(this, "Chọn đáp án đúng!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> answers = new HashMap<>();
        answers.put("A", a);
        answers.put("B", b);
        answers.put("C", c);
        answers.put("D", d);

        Question q = new Question(content, answers, correct);

        db.collection("questions")
                .add(q)
                .addOnSuccessListener(e -> {
                    Toast.makeText(this, "Đã thêm câu hỏi!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(err ->
                        Toast.makeText(this, "Lỗi: " + err.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
