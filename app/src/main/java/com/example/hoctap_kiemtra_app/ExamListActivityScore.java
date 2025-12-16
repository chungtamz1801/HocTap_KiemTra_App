package com.example.hoctap_kiemtra_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.model.Exam;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ExamListActivityScore extends AppCompatActivity {

    RecyclerView rv;
    FirebaseFirestore db;
    List<Exam> examList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list_score);

        rv = findViewById(R.id.rvExam);
        rv.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        db.collection("exams")
                .get()
                .addOnSuccessListener(query -> {
                    examList.clear();

                    for (DocumentSnapshot d : query) {
                        Exam e = d.toObject(Exam.class);
                        if (e == null) continue;

                        e.setId(d.getId());
                        examList.add(e);
                    }

                    rv.setAdapter(new ExamAdapterScore(examList, exam -> {
                        Intent intent = new Intent(
                                ExamListActivityScore.this,
                                StudentScoreActivity.class
                        );
                        intent.putExtra("examId", exam.getId());
                        startActivity(intent);
                    }));
                });

    }

}
