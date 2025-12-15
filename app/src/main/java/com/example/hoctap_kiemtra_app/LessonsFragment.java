package com.example.hoctap_kiemtra_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LessonsFragment extends Fragment {

    private RecyclerView recyclerView;
    private LessonAdapter lessonAdapter;
    private FirebaseManager firebaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lessons, container, false);

        recyclerView = view.findViewById(R.id.rvLessons);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        firebaseManager = FirebaseManager.getInstance();

        lessonAdapter = new LessonAdapter(new java.util.ArrayList<>(), lesson -> {
            Intent intent = new Intent(requireContext(), LessonDetailActivity.class);
            intent.putExtra("LESSON_ID", lesson.getId());
            intent.putExtra("LESSON_TITLE", lesson.getTitle());
            intent.putExtra("LESSON_PROGRESS", lesson.getProgress());
            startActivity(intent);
        });

        recyclerView.setAdapter(lessonAdapter);

        loadLessons();

        return view;
    }

    private void loadLessons() {
        firebaseManager.getAllLessons(new FirebaseManager.OnDataLoadListener() {
            @Override
            public void onDataLoaded(java.util.List<Lesson> lessons) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        lessonAdapter.updateData(lessons);
                    });
                }
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLessons();
    }
}
