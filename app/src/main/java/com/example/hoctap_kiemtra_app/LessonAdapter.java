package com.example.hoctap_kiemtra_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private List<Lesson> lessons;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Lesson lesson);
    }

    public LessonAdapter(List<Lesson> lessons, OnItemClickListener listener) {
        this.lessons = lessons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        holder.bind(lessons.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return lessons != null ? lessons.size() : 0;
    }

    public void updateData(List<Lesson> newLessons) {
        this.lessons = newLessons;
        notifyDataSetChanged();
    }

    public static class LessonViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView infoTextView;
        private TextView progressTextView;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvLessonTitle);
            infoTextView = itemView.findViewById(R.id.tvLessonInfo);
            progressTextView = itemView.findViewById(R.id.tvLessonProgress);
        }

        public void bind(final Lesson lesson, final OnItemClickListener listener) {
            titleTextView.setText(lesson.getTitle());
            infoTextView.setText(lesson.getInfo());
            progressTextView.setText("Quá trình: " + lesson.getProgress());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(lesson);
                    }
                }
            });
        }
    }
}