package com.example.hoctap_kiemtra_app;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LessonDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvProgress;
    private RecyclerView recyclerViewItems;
    private ProgressBar progressBar;
    private LessonItemAdapter itemAdapter;
    private FirebaseManager firebaseManager;
    private String lessonId;
    private Lesson currentLesson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết bài học");
        }

        // Khởi tạo views
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvProgress = findViewById(R.id.tvDetailProgress);
        recyclerViewItems = findViewById(R.id.recyclerViewLessonItems);
        progressBar = findViewById(R.id.progressBarDetail);

        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        firebaseManager = FirebaseManager.getInstance();

        // Lấy dữ liệu từ Intent
        lessonId = getIntent().getStringExtra("LESSON_ID");
        String title = getIntent().getStringExtra("LESSON_TITLE");
        String progress = getIntent().getStringExtra("LESSON_PROGRESS");

        tvTitle.setText(title);
        tvProgress.setText("Tiến độ: " + progress);

        // Load chi tiết lesson từ Firebase
        if (lessonId != null && !lessonId.isEmpty()) {
            loadLessonDetail();
        }
    }

    private void loadLessonDetail() {
        progressBar.setVisibility(View.VISIBLE);

        firebaseManager.getLesson(lessonId, new FirebaseManager.OnLessonLoadListener() {
            @Override
            public void onLessonLoaded(Lesson lesson) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        currentLesson = lesson;

                        // Cập nhật thông tin
                        tvTitle.setText(lesson.getTitle());
                        tvProgress.setText("Tiến độ: " + lesson.getProgress());

                        displayLessonItems(lesson);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LessonDetailActivity.this,
                                "Lỗi: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void displayLessonItems(Lesson lesson) {
        if (lesson.getLessonItems() != null && !lesson.getLessonItems().isEmpty()) {
            itemAdapter = new LessonItemAdapter(
                    lesson.getLessonItems(),
                    new LessonItemAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(LessonItem item, int position) {
                            // Xử lý click vào item
                            Toast.makeText(LessonDetailActivity.this,
                                    "Đã chọn: " + item.getTitle(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onItemStatusChanged(LessonItem item, int position, boolean completed) {
                            // Cập nhật trạng thái hoàn thành - TỰ ĐỘNG LƯU
                            updateItemStatus(position, completed);
                        }
                    }
            );
            recyclerViewItems.setAdapter(itemAdapter);
        } else {
            Toast.makeText(this, "Bài học chưa có nội dung", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateItemStatus(int position, boolean completed) {
        progressBar.setVisibility(View.VISIBLE);

        firebaseManager.updateLessonItemStatus(
                lessonId,
                position,
                completed,
                new FirebaseManager.OnCompleteListener() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                // Reload để cập nhật progress
                                loadLessonDetail();
                                Toast.makeText(LessonDetailActivity.this,
                                        completed ? "Đã đánh dấu hoàn thành" : "Đã bỏ đánh dấu",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LessonDetailActivity.this,
                                        "Lỗi: " + error,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}