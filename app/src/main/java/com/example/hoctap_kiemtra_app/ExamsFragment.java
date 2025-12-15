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
public class ExamsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exams, container, false);

        Button btnStartExam = view.findViewById(R.id.btnStartExam);

        btnStartExam.setOnClickListener(v -> {
            // Tìm exam đang active
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("exams")
                    .whereEqualTo("active", true)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            String examId = querySnapshot.getDocuments().get(0).getId();
                            Intent intent = new Intent(requireContext(), com.example.chatapp.DoQuizActivity.class);
                            intent.putExtra("examId", examId);
                            startActivity(intent);
                        } else {
                            android.widget.Toast.makeText(requireContext(),
                                    "Chưa có đề thi nào đang mở!",
                                    android.widget.Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        android.widget.Toast.makeText(requireContext(),
                                "Lỗi: " + e.getMessage(),
                                android.widget.Toast.LENGTH_SHORT).show();
                    });
        });

        return view;
    }
}