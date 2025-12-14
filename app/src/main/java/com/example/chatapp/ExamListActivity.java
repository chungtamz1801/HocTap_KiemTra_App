package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.adapter.ExamAdapter;
import com.example.chatapp.data.FirestoreRepo;
import com.example.chatapp.model.Exam;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ExamListActivity extends AppCompatActivity implements ExamAdapter.OnExamClickListener {

    private RecyclerView rvExams;
    private Button btnAddExam, btnQuestion;

    private ExamAdapter adapter;
    private List<Exam> examList = new ArrayList<>();
    private FirestoreRepo repo = new FirestoreRepo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examlist);

        rvExams = findViewById(R.id.rvExams);
        btnAddExam = findViewById(R.id.btnAddExam);
        btnQuestion = findViewById(R.id.btnQuestion);   // <-- thêm ở đây

        adapter = new ExamAdapter(examList, this);
        rvExams.setLayoutManager(new LinearLayoutManager(this));
        rvExams.setAdapter(adapter);

        btnAddExam.setOnClickListener(v -> {
            Intent i = new Intent(ExamListActivity.this, CreateExamActivity.class);
            startActivity(i);
        });
// ======= NÚT QUẢN LÝ CÂU HỎI =======
        btnQuestion.setOnClickListener(v -> {
            Intent i = new Intent(ExamListActivity.this, QuestionManageActivity.class);
            startActivity(i);
        });
        loadExamListRealtime();
    }

    private void loadExamListRealtime() {
        FirebaseFirestore.getInstance()
                .collection("exams")
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(ExamListActivity.this, "Lỗi load dữ liệu", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    examList.clear();
                    if (value != null) {
                        for (DocumentSnapshot d : value.getDocuments()) {
                            Exam exam = new Exam();
                            exam.setId(d.getId());
                            exam.setName(d.getString("name"));

                            Object t = d.get("time");
                            Object s = d.get("score");

                            exam.setTime(t != null ? ((Long) t).intValue() : 0);
                            exam.setScore(s != null ? ((Long) s).intValue() : 0);

                            examList.add(exam);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onExamClick(Exam exam) {
        Intent i = new Intent(ExamListActivity.this, ExamDetailActivity.class);
        i.putExtra("examId", exam.getId());
        startActivity(i);
    }

    @Override
    public void onEditExam(Exam exam) {
        Intent i = new Intent(ExamListActivity.this, EditExamActivity.class);
        i.putExtra("examId", exam.getId());
        startActivity(i);
    }

    @Override
    public void onDeleteExam(Exam exam) {
        repo.deleteExam(exam.getId(),
                aVoid -> Toast.makeText(ExamListActivity.this, "Đã xoá bộ đề", Toast.LENGTH_SHORT).show(),
                e -> Toast.makeText(ExamListActivity.this, "Lỗi xoá: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

}
