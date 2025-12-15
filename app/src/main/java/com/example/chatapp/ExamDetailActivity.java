package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.adapter.QuestionAdapterB;
import com.example.chatapp.data.FirestoreRepo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.hoctap_kiemtra_app.R;
public class ExamDetailActivity extends AppCompatActivity implements QuestionAdapterB.OnQuestionActionListener {

    private TextView tvExamName;
    private Button btnEditExam, btnAddQuestion, btnCreateNewQuestion;
    private RecyclerView rvQuestions;
    private QuestionAdapterB adapter;
    private List<Question> questionList = new ArrayList<>();
    private FirestoreRepo repo = new FirestoreRepo();
    private String examId;
    private DocumentSnapshot examSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_detail);

        tvExamName = findViewById(R.id.tvExamName);
        btnEditExam = findViewById(R.id.btnEditExam);
        btnAddQuestion = findViewById(R.id.btnAddExistingQuestion);
        btnCreateNewQuestion = findViewById(R.id.btnCreateQuestion);
        rvQuestions = findViewById(R.id.rvQuestions);

        examId = getIntent().getStringExtra("examId");
        if (examId == null) { finish(); return; }

        adapter = new QuestionAdapterB(questionList, this);
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));
        rvQuestions.setAdapter(adapter);

        btnEditExam.setOnClickListener(v -> {
            Intent i = new Intent(ExamDetailActivity.this, EditExamActivity.class);
            i.putExtra("examId", examId);
            startActivity(i);
        });

        btnAddQuestion.setOnClickListener(v -> showPickExistingQuestionsDialog());
        btnCreateNewQuestion.setOnClickListener(v -> showCreateQuestionDialog());

        loadExamAndQuestions();
    }

    private void loadExamAndQuestions() {
        repo.getExam(examId,
                (OnSuccessListener<DocumentSnapshot>) documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Toast.makeText(ExamDetailActivity.this, "Bộ đề không tồn tại", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    examSnapshot = documentSnapshot;
                    tvExamName.setText(documentSnapshot.getString("name"));

                    List<String> qIds = (List<String>) documentSnapshot.get("questionIds");
                    if (qIds == null || qIds.isEmpty()) {
                        questionList.clear();
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    repo.getQuestionsByIds(qIds,
                            (OnSuccessListener<QuerySnapshot>) querySnapshot -> {
                                questionList.clear();
                                if (querySnapshot != null) {
                                    for (DocumentSnapshot d : querySnapshot.getDocuments()) {
                                        Question q = new Question();
                                        q.setId(d.getId());
                                        q.setContent(d.getString("content"));
                                        q.setAnswers((Map<String, String>) d.get("answers"));
                                        q.setCorrectAnswer(d.getString("correctAnswer"));
                                        questionList.add(q);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            },
                            (OnFailureListener) e -> Toast.makeText(ExamDetailActivity.this, "Lỗi load câu hỏi", Toast.LENGTH_SHORT).show());
                },
                (OnFailureListener) e -> Toast.makeText(ExamDetailActivity.this, "Lỗi load exam", Toast.LENGTH_SHORT).show());
    }

    // show dialog with a simple list of all questions (for demo: we fetch all from 'questions' collection)
    private void showPickExistingQuestionsDialog() {
        FirebaseFirestore.getInstance().collection("questions").get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(this, "Chưa có câu hỏi để chọn", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<DocumentSnapshot> all = querySnapshot.getDocuments();
                    String[] items = new String[all.size()];
                    boolean[] checked = new boolean[all.size()];
                    for (int i = 0; i < all.size(); i++) {
                        items[i] = all.get(i).getString("content");
                        // mark already-in-exam as checked & disabled logically
                        List<String> qIds = (List<String>) examSnapshot.get("questionIds");
                        checked[i] = qIds != null && qIds.contains(all.get(i).getId());
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Chọn câu hỏi để thêm");
                    builder.setMultiChoiceItems(items, checked, (dialog, which, isChecked) -> {
                        // do nothing here, add on positive button
                        checked[which] = isChecked;
                    });
                    builder.setPositiveButton("Thêm", (dialog, which) -> {
                        for (int i = 0; i < all.size(); i++) {
                            if (checked[i]) {
                                String qId = all.get(i).getId();
                                // only add if not already present
                                List<String> qIds = (List<String>) examSnapshot.get("questionIds");
                                if (qIds == null || !qIds.contains(qId)) {
                                    repo.addQuestionToExam(examId, qId,
                                            (OnSuccessListener<Void>) aVoid -> {
                                                // refresh after adding (we'll refresh outside loop)
                                            },
                                            (OnFailureListener) e -> Toast.makeText(this, "Lỗi thêm: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                }
                            }
                        }
                        // small delay then reload or reload in success callbacks; simplified:
                        loadExamAndQuestions();
                    });
                    builder.setNegativeButton("Hủy", null);
                    builder.show();
                }).addOnFailureListener(e -> Toast.makeText(this, "Lỗi load câu hỏi", Toast.LENGTH_SHORT).show());
    }

    private void showCreateQuestionDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_create_question, null);
        EditText etContent = v.findViewById(R.id.etQContent);
        EditText etA = v.findViewById(R.id.etA);
        EditText etB = v.findViewById(R.id.etB);
        EditText etC = v.findViewById(R.id.etC);
        EditText etD = v.findViewById(R.id.etD);
        Spinner spCorrect = v.findViewById(R.id.spCorrect);

        ArrayAdapter<String> adapterSp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"A","B","C","D"});
        adapterSp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCorrect.setAdapter(adapterSp);

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Tạo câu hỏi mới");
        b.setView(v);
        b.setPositiveButton("Tạo", (dialog, which) -> {
            String content = etContent.getText().toString().trim();
            String a = etA.getText().toString().trim();
            String bAns = etB.getText().toString().trim();
            String c = etC.getText().toString().trim();
            String d = etD.getText().toString().trim();
            String correct = spCorrect.getSelectedItem().toString();

            if (content.isEmpty() || a.isEmpty() || bAns.isEmpty() || c.isEmpty() || d.isEmpty()) {
                Toast.makeText(ExamDetailActivity.this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> answers = new HashMap<>();
            answers.put("A", a);
            answers.put("B", bAns);
            answers.put("C", c);
            answers.put("D", d);

            Question q = new Question(content, answers, correct);
            repo.createQuestion(q,
                    (OnSuccessListener<DocumentReference>) docRef -> {
                        // add q id to exam
                        repo.addQuestionToExam(examId, docRef.getId(),
                                (OnSuccessListener<Void>) aVoid -> {
                                    Toast.makeText(ExamDetailActivity.this, "Tạo & thêm câu hỏi vào đề thành công", Toast.LENGTH_SHORT).show();
                                    loadExamAndQuestions();
                                },
                                (OnFailureListener) e -> Toast.makeText(ExamDetailActivity.this, "Lỗi thêm vào đề: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    },
                    (OnFailureListener) e -> Toast.makeText(ExamDetailActivity.this, "Lỗi tạo câu hỏi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
        b.setNegativeButton("Hủy", null);
        b.show();
    }

    // Adapter callback: edit
    @Override
    public void onEditQuestion(Question q) {
        showEditQuestionDialog(q);
    }


    private void showEditQuestionDialog(Question q) {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_create_question, null);
        EditText etContent = v.findViewById(R.id.etQContent);
        EditText etA = v.findViewById(R.id.etA);
        EditText etB = v.findViewById(R.id.etB);
        EditText etC = v.findViewById(R.id.etC);
        EditText etD = v.findViewById(R.id.etD);
        Spinner spCorrect = v.findViewById(R.id.spCorrect);

        // fill data
        etContent.setText(q.getContent());
        Map<String, String> ans = q.getAnswers();
        etA.setText(ans.get("A"));
        etB.setText(ans.get("B"));
        etC.setText(ans.get("C"));
        etD.setText(ans.get("D"));

        ArrayAdapter<String> adapterSp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"A","B","C","D"});
        adapterSp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCorrect.setAdapter(adapterSp);
        spCorrect.setSelection(Arrays.asList("A","B","C","D").indexOf(q.getCorrectAnswer()));

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Sửa câu hỏi");
        b.setView(v);

        b.setPositiveButton("Lưu", (dialog, which) -> {

            String content = etContent.getText().toString().trim();
            String a = etA.getText().toString().trim();
            String bAns = etB.getText().toString().trim();
            String c = etC.getText().toString().trim();
            String d = etD.getText().toString().trim();
            String correct = spCorrect.getSelectedItem().toString();

            if (content.isEmpty() || a.isEmpty() || bAns.isEmpty() || c.isEmpty() || d.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            // STEP 1: Remove old question from exam
            repo.removeQuestionFromExam(examId, q.getId(),
                    aVoid -> {

                        // STEP 2: Create new question
                        Map<String, String> answers = new HashMap<>();
                        answers.put("A", a);
                        answers.put("B", bAns);
                        answers.put("C", c);
                        answers.put("D", d);

                        Question newQ = new Question(content, answers, correct);

                        repo.createQuestion(newQ, docRef -> {

                            String newQid = docRef.getId();

                            // STEP 3: Add new question ID to exam
                            repo.addQuestionToExam(examId, newQid,
                                    aVoid2 -> {
                                        Toast.makeText(this, "Đã cập nhật câu hỏi", Toast.LENGTH_SHORT).show();
                                        loadExamAndQuestions();
                                    },
                                    e -> Toast.makeText(this, "Lỗi thêm câu hỏi mới vào đề", Toast.LENGTH_SHORT).show());

                        }, e -> Toast.makeText(this, "Lỗi tạo câu hỏi mới", Toast.LENGTH_SHORT).show());

                    },
                    e -> Toast.makeText(this, "Lỗi xóa câu hỏi cũ khỏi đề", Toast.LENGTH_SHORT).show()
            );
        });

        b.setNegativeButton("Hủy", null);
        b.show();
    }
    // Adapter callback: delete
    @Override
    public void onDeleteQuestion(Question q) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Xóa câu hỏi khỏi đề? (câu hỏi vẫn còn trong collection 'questions')")
                .setPositiveButton("Xóa khỏi đề", (dialog, which) -> {
                    repo.removeQuestionFromExam(examId, q.getId(),
                            (OnSuccessListener<Void>) aVoid -> {
                                Toast.makeText(ExamDetailActivity.this, "Đã xóa khỏi đề", Toast.LENGTH_SHORT).show();
                                loadExamAndQuestions();
                            },
                            (OnFailureListener) e -> Toast.makeText(ExamDetailActivity.this, "Lỗi xóa", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
