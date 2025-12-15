package com.example.hoctap_kiemtra_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log; // Import Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.QuestionDataHolder;
import com.example.chatapp.ReviewActivity;
import com.example.chatapp.adapter.HistoryAdapter;
import com.example.chatapp.model.ScoreHistory;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ResultsFragment extends Fragment {

    private RecyclerView rvResults;
    private HistoryAdapter adapter;
    private List<ScoreHistory> scoreList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);

        Button btnReview = view.findViewById(R.id.btnReview);
        rvResults = view.findViewById(R.id.rvResults);

        rvResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        scoreList = new ArrayList<>();
        adapter = new HistoryAdapter(scoreList);
        rvResults.setAdapter(adapter);

        btnReview.setOnClickListener(v -> {
            if (QuestionDataHolder.getInstance().getListQuestions() != null && !QuestionDataHolder.getInstance().getListQuestions().isEmpty()) {
                Intent intent = new Intent(requireContext(), ReviewActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "Chỉ có thể xem lại ngay sau khi thi!", Toast.LENGTH_SHORT).show();
            }
        });

        loadResultsHistory();
        return view;
    }

    private void loadResultsHistory() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("QUIZ_APP", android.content.Context.MODE_PRIVATE);
        String studentId = prefs.getString("STUDENT_ID", "");

        Log.d("DEBUG_HISTORY", "Student ID: " + studentId); // Kiểm tra ID

        if (!studentId.isEmpty()) {
            FirebaseFirestore.getInstance()
                    .collection("student_score")
                    .whereEqualTo("id", studentId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(20)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        scoreList.clear();
                        if (querySnapshot.isEmpty()) {
                            Log.d("DEBUG_HISTORY", "Không tìm thấy bài thi nào trên Firebase");
                            Toast.makeText(getContext(), "Bạn chưa làm bài thi nào", Toast.LENGTH_SHORT).show();
                        } else {
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                ScoreHistory item = doc.toObject(ScoreHistory.class);
                                scoreList.add(item);
                            }
                            adapter.notifyDataSetChanged();
                            Log.d("DEBUG_HISTORY", "Đã load được " + scoreList.size() + " bài thi");
                        }
                    })
                    .addOnFailureListener(e -> {
                        // QUAN TRỌNG: Hãy xem Logcat nếu dòng này hiện ra
                        Log.e("DEBUG_HISTORY", "Lỗi tải lịch sử: " + e.getMessage());
                        Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            Toast.makeText(getContext(), "Lỗi: Chưa đăng nhập (Không có ID)", Toast.LENGTH_SHORT).show();
        }
    }
}