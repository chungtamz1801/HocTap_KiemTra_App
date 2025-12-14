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

public class QuestionEditActivity extends AppCompatActivity {

    private EditText etContent, etA, etB, etC, etD;
    private RadioGroup rgCorrect;
    private Button btnUpdate;

    private FirebaseFirestore db;
    private String questionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_edit);

        db = FirebaseFirestore.getInstance();

        questionId = getIntent().getStringExtra("id");
        if (questionId == null) {
            Toast.makeText(this, "Lỗi: thiếu ID câu hỏi!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etContent = findViewById(R.id.etContent);
        etA = findViewById(R.id.etA);
        etB = findViewById(R.id.etB);
        etC = findViewById(R.id.etC);
        etD = findViewById(R.id.etD);
        rgCorrect = findViewById(R.id.rgCorrect);
        btnUpdate = findViewById(R.id.btnUpdate);

        loadData();

        btnUpdate.setOnClickListener(v -> updateData());
    }

    private void loadData() {
        db.collection("questions").document(questionId)
                .get()
                .addOnSuccessListener(d -> {
                    Question q = d.toObject(Question.class);
                    if (q == null) return;

                    etContent.setText(q.getContent());
                    etA.setText(q.getAnswers().get("A"));
                    etB.setText(q.getAnswers().get("B"));
                    etC.setText(q.getAnswers().get("C"));
                    etD.setText(q.getAnswers().get("D"));

                    switch (q.getCorrectAnswer()) {
                        case "A": rgCorrect.check(R.id.rbA); break;
                        case "B": rgCorrect.check(R.id.rbB); break;
                        case "C": rgCorrect.check(R.id.rbC); break;
                        case "D": rgCorrect.check(R.id.rbD); break;
                    }

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi load dữ liệu!", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateData() {
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

        Map<String, String> answers = new HashMap<>();
        answers.put("A", a);
        answers.put("B", b);
        answers.put("C", c);
        answers.put("D", d);

        db.collection("questions").document(questionId)
                .update(
                        "content", content,
                        "answers", answers,
                        "correctAnswer", correct
                )
                .addOnSuccessListener(s -> {
                    Toast.makeText(this, "Đã cập nhật!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi update!", Toast.LENGTH_SHORT).show()
                );
    }
}
