package com.example.chatapp;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.data.FirestoreRepo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import com.example.hoctap_kiemtra_app.R;

public class EditExamActivity extends AppCompatActivity {

    private EditText etName, etTime, etScore;
    private Button btnSave;
    private FirestoreRepo repo = new FirestoreRepo();
    private String examId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exam);

        etName = findViewById(R.id.etEditExamName);
        etTime = findViewById(R.id.etEditExamTime);
        etScore = findViewById(R.id.etEditExamScore);
        btnSave = findViewById(R.id.btnSaveExam);

        examId = getIntent().getStringExtra("examId");
        if (examId == null) {
            Toast.makeText(this, "Không tìm thấy examId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadExam();

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String sTime = etTime.getText().toString().trim();
            String sScore = etScore.getText().toString().trim();

            if (name.isEmpty() || sTime.isEmpty() || sScore.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            // kiểm tra trùng tên
            repo.isExamNameDuplicate(name, examId, isDup -> {
                if (isDup) {
                    Toast.makeText(EditExamActivity.this, "Tên bộ đề đã tồn tại", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("name", name);
                    updates.put("time", Integer.parseInt(sTime));
                    updates.put("score", Integer.parseInt(sScore));
                    repo.updateExam(examId,
                            updates,
                            (OnSuccessListener<Void>) aVoid -> {
                                Toast.makeText(EditExamActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            },
                            (OnFailureListener) e -> Toast.makeText(EditExamActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }, (OnFailureListener) e -> Toast.makeText(EditExamActivity.this, "Lỗi kiểm tra tên", Toast.LENGTH_SHORT).show());
        });
    }

    private void loadExam() {
        repo.getExam(examId,
                (OnSuccessListener<DocumentSnapshot>) documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        etName.setText(documentSnapshot.getString("name"));
                        Object t = documentSnapshot.get("time");
                        Object s = documentSnapshot.get("score");
                        etTime.setText(t != null ? String.valueOf(((Long) t).intValue()) : "0");
                        etScore.setText(s != null ? String.valueOf(((Long) s).intValue()) : "0");
                    }
                },
                (OnFailureListener) e -> Toast.makeText(EditExamActivity.this, "Lỗi load exam", Toast.LENGTH_SHORT).show());
    }
}
