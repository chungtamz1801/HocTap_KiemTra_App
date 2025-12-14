package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.adapter.QManageAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class QuestionManageActivity extends AppCompatActivity
        implements QManageAdapter.OnActionListener {

    private RecyclerView rvQ;
    private QManageAdapter adapter;
    private ArrayList<Question> list = new ArrayList<>();

    private FirebaseFirestore db;
    private CollectionReference refQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_manage);

        db = FirebaseFirestore.getInstance();
        refQuestions = db.collection("questions");

        rvQ = findViewById(R.id.rvQManage);
        rvQ.setLayoutManager(new LinearLayoutManager(this));

        adapter = new QManageAdapter(list, this);
        rvQ.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fabAddQ);
        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, QuestionCreateActivity.class))
        );

        loadQuestions();
    }

    private void loadQuestions() {
        refQuestions.get().addOnSuccessListener(snapshot -> {
            list.clear();
            snapshot.getDocuments().forEach(d -> {
                Question q = d.toObject(Question.class);
                if (q != null) q.setId(d.getId());
                list.add(q);
            });
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onEdit(Question q) {
        Intent i = new Intent(this, QuestionEditActivity.class);
        i.putExtra("id", q.getId());
        startActivity(i);
    }

    @Override
    public void onDelete(Question q) {
        refQuestions.document(q.getId()).delete()
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Đã xóa câu hỏi", Toast.LENGTH_SHORT).show();
                    loadQuestions();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadQuestions();
    }
}
