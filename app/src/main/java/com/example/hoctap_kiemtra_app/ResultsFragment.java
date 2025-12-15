package com.example.hoctap_kiemtra_app;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
public class ResultsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);

        Button btnReview = view.findViewById(R.id.btnReview);
        RecyclerView rvResults = view.findViewById(R.id.rvResults);

        rvResults.setLayoutManager(new LinearLayoutManager(requireContext()));

        btnReview.setOnClickListener(v -> {
            if (com.example.chatapp.QuestionDataHolder.getInstance().getListQuestions() != null) {
                Intent intent = new Intent(requireContext(), com.example.chatapp.ReviewActivity.class);
                startActivity(intent);
            } else {
                android.widget.Toast.makeText(requireContext(),
                        "Bạn chưa làm bài nào!",
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // Load lịch sử làm bài từ Firebase
        loadResultsHistory(rvResults);

        return view;
    }

    private void loadResultsHistory(RecyclerView recyclerView) {
        android.content.SharedPreferences prefs = requireActivity()
                .getSharedPreferences("QUIZ_APP", android.content.Context.MODE_PRIVATE);
        String studentId = prefs.getString("STUDENT_ID", "");

        if (!studentId.isEmpty()) {
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("student_score")
                    .whereEqualTo("id", studentId)
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(10)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        // Hiển thị lịch sử làm bài
                        // TODO: Tạo adapter cho results
                    });
        }
    }
}