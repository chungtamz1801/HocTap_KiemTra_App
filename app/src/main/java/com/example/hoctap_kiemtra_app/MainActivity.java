package com.example.hoctap_kiemtra_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LessonAdapter lessonAdapter;
    private ProgressBar progressBar;
    private FirebaseManager firebaseManager;
    private List<Lesson> lessons;
    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Phần Mở đầu");
        }

        // Khởi tạo views
        recyclerView = findViewById(R.id.recyclerViewLessons);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseManager = FirebaseManager.getInstance();

        // Khởi tạo dữ liệu
        lessons = new ArrayList<>();

        // Setup adapter
        lessonAdapter = new LessonAdapter(lessons, new LessonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Lesson lesson) {
                Intent intent = new Intent(MainActivity.this, LessonDetailActivity.class);
                intent.putExtra("LESSON_ID", lesson.getId());
                intent.putExtra("LESSON_TITLE", lesson.getTitle());
                intent.putExtra("LESSON_INFO", lesson.getInfo());
                intent.putExtra("LESSON_PROGRESS", lesson.getProgress());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(lessonAdapter);

        // Tự động load hoặc tạo dữ liệu
        initializeData();
    }

    private void initializeData() {
        progressBar.setVisibility(View.VISIBLE);

        firebaseManager.getAllLessons(new FirebaseManager.OnDataLoadListener() {
            @Override
            public void onDataLoaded(List<Lesson> loadedLessons) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loadedLessons.isEmpty() && isFirstLoad) {
                            // Nếu chưa có dữ liệu, tạo dữ liệu mặc định
                            createAndSaveDefaultLessons();
                        } else {
                            // Đã có dữ liệu, hiển thị
                            lessons.clear();
                            lessons.addAll(loadedLessons);
                            lessonAdapter.updateData(lessons);
                            progressBar.setVisibility(View.GONE);
                        }
                        isFirstLoad = false;
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this,
                                "Lỗi tải dữ liệu: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void createAndSaveDefaultLessons() {
        List<Lesson> defaultLessons = createDefaultLessons();

        firebaseManager.saveLessons(defaultLessons, new FirebaseManager.OnCompleteListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Sau khi lưu xong, load lại để lấy ID
                        loadDataFromFirebase();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this,
                                "Lỗi khởi tạo dữ liệu: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private List<Lesson> createDefaultLessons() {
        List<Lesson> sampleLessons = new ArrayList<>();

        // Lesson 1
        Lesson lesson1 = new Lesson(
                "Lý thuyết - Bài 1: Giới thiệu về lập trình di động và thiết kế giao diện trên Android",
                "Các nhãn: 3 Các file: 2 Các gói SCORM: 5 Bài tập: 1 Trắc nghiệm: 1",
                "4 / 6"
        );
        lesson1.addLessonItem(new LessonItem("Giới thiệu về Android", "label", "Nội dung giới thiệu"));
        lesson1.addLessonItem(new LessonItem("Cài đặt môi trường", "label", "Hướng dẫn cài đặt"));
        lesson1.addLessonItem(new LessonItem("Tài liệu tham khảo", "file", "document.pdf"));
        lesson1.addLessonItem(new LessonItem("Video hướng dẫn", "scorm", "video_url"));
        lesson1.addLessonItem(new LessonItem("Bài tập 1", "assignment", "Tạo ứng dụng Hello World"));
        lesson1.addLessonItem(new LessonItem("Kiểm tra kiến thức", "quiz", "quiz_id_1"));
        lesson1.getLessonItems().get(0).setCompleted(true);
        lesson1.getLessonItems().get(1).setCompleted(true);
        lesson1.getLessonItems().get(2).setCompleted(true);
        lesson1.getLessonItems().get(3).setCompleted(true);
        lesson1.updateProgress();
        sampleLessons.add(lesson1);

        // Lesson 2
        Lesson lesson2 = new Lesson(
                "Thực hành/Thí nghiệm - Bài 2: Thực hành xây dựng giao diện ứng dụng",
                "Các nhãn: 3 Các file: 3 Các gói SCORM: 2 Bài tập: 3 Trắc nghiệm: 1",
                "2 / 7"
        );
        lesson2.addLessonItem(new LessonItem("Layout XML", "label", "Giới thiệu về Layout"));
        lesson2.addLessonItem(new LessonItem("LinearLayout", "file", "linear_layout.pdf"));
        lesson2.addLessonItem(new LessonItem("RelativeLayout", "file", "relative_layout.pdf"));
        lesson2.addLessonItem(new LessonItem("Demo giao diện", "scorm", "demo_video"));
        lesson2.addLessonItem(new LessonItem("Bài tập 2.1", "assignment", "Tạo form đăng nhập"));
        lesson2.addLessonItem(new LessonItem("Bài tập 2.2", "assignment", "Tạo danh sách"));
        lesson2.addLessonItem(new LessonItem("Trắc nghiệm 2", "quiz", "quiz_id_2"));
        lesson2.getLessonItems().get(0).setCompleted(true);
        lesson2.getLessonItems().get(1).setCompleted(true);
        lesson2.updateProgress();
        sampleLessons.add(lesson2);

        // Lesson 3
        Lesson lesson3 = new Lesson(
                "Lý thuyết - Bài 3: Xử lý sự kiện trên giao diện ứng dụng",
                "Các nhãn: 3 Các file: 2 Các gói SCORM: 3 Bài tập: 2 Trắc nghiệm: 1",
                "2 / 6"
        );
        lesson3.addLessonItem(new LessonItem("Sự kiện onClick", "label", "Xử lý click"));
        lesson3.addLessonItem(new LessonItem("Sự kiện onTouch", "label", "Xử lý touch"));
        lesson3.addLessonItem(new LessonItem("Event Listener", "file", "event_listener.pdf"));
        lesson3.addLessonItem(new LessonItem("Demo sự kiện", "scorm", "event_demo"));
        lesson3.addLessonItem(new LessonItem("Bài tập 3", "assignment", "Xử lý button click"));
        lesson3.addLessonItem(new LessonItem("Quiz 3", "quiz", "quiz_id_3"));
        lesson3.getLessonItems().get(0).setCompleted(true);
        lesson3.getLessonItems().get(1).setCompleted(true);
        lesson3.updateProgress();
        sampleLessons.add(lesson3);

        // Lesson 4
        Lesson lesson4 = new Lesson(
                "Thực hành/Thí nghiệm - Bài 4: Thực hành xử lý sự kiện trên giao diện ứng dụng",
                "Các nhãn: 3 Các file: 2 Các gói SCORM: 2 Bài tập: 5 Trắc nghiệm: 1",
                "1 / 13"
        );
        lesson4.addLessonItem(new LessonItem("Hướng dẫn bài 4", "label", "Giới thiệu"));
        lesson4.addLessonItem(new LessonItem("Tài liệu 4", "file", "doc4.pdf"));
        lesson4.addLessonItem(new LessonItem("Video 4", "scorm", "video4"));
        lesson4.addLessonItem(new LessonItem("Thực hành 4.1", "assignment", "Button và TextView"));
        lesson4.addLessonItem(new LessonItem("Thực hành 4.2", "assignment", "EditText validation"));
        lesson4.addLessonItem(new LessonItem("Thực hành 4.3", "assignment", "CheckBox và RadioButton"));
        lesson4.addLessonItem(new LessonItem("Quiz 4", "quiz", "quiz_id_4"));
        lesson4.getLessonItems().get(0).setCompleted(true);
        lesson4.updateProgress();
        sampleLessons.add(lesson4);

        // Lesson 5
        Lesson lesson5 = new Lesson(
                "Lý thuyết - Bài 5: Menu và Intent",
                "Các nhãn: 3 File: 1 Các gói SCORM: 5 Bài tập: 3 Trắc nghiệm: 1 Link lớp học/tài liệu: 1",
                "0 / 14"
        );
        lesson5.addLessonItem(new LessonItem("Menu trong Android", "label", "Options Menu, Context Menu"));
        lesson5.addLessonItem(new LessonItem("Intent Explicit", "label", "Chuyển màn hình"));
        lesson5.addLessonItem(new LessonItem("Intent Implicit", "label", "Mở ứng dụng khác"));
        lesson5.addLessonItem(new LessonItem("Tài liệu Menu", "file", "menu_guide.pdf"));
        lesson5.addLessonItem(new LessonItem("Demo Menu", "scorm", "menu_demo"));
        lesson5.addLessonItem(new LessonItem("Bài tập 5", "assignment", "Tạo menu"));
        lesson5.addLessonItem(new LessonItem("Quiz 5", "quiz", "quiz_id_5"));
        sampleLessons.add(lesson5);

        // Lesson 6
        Lesson lesson6 = new Lesson(
                "Thực hành/Thí nghiệm - Bài 6: Thực hành xây dựng ứng dụng với Menu và Intent",
                "Các nhãn: 3 File: 1 Các gói SCORM: 2 Bài tập: 3",
                "0 / 9"
        );
        lesson6.addLessonItem(new LessonItem("Hướng dẫn bài 6", "label", "Giới thiệu"));
        lesson6.addLessonItem(new LessonItem("Tài liệu 6", "file", "doc6.pdf"));
        lesson6.addLessonItem(new LessonItem("Video 6", "scorm", "video6"));
        lesson6.addLessonItem(new LessonItem("Thực hành Menu", "assignment", "Tạo Options Menu"));
        lesson6.addLessonItem(new LessonItem("Thực hành Intent", "assignment", "Chuyển dữ liệu giữa Activity"));
        lesson6.addLessonItem(new LessonItem("Bài tập tổng hợp", "assignment", "Ứng dụng hoàn chỉnh"));
        lesson6.addLessonItem(new LessonItem("Quiz 6", "quiz", "quiz_id_6"));
        sampleLessons.add(lesson6);

        // Lesson 7
        Lesson lesson7 = new Lesson(
                "Thực hành/Thí nghiệm - Bài 7: ListView và Adapter",
                "Các nhãn: 3 File: 1 Các gói SCORM: 2 Bài tập: 3",
                "0 / 9"
        );
        lesson7.addLessonItem(new LessonItem("ListView cơ bản", "label", "Giới thiệu ListView"));
        lesson7.addLessonItem(new LessonItem("Custom Adapter", "file", "adapter_guide.pdf"));
        lesson7.addLessonItem(new LessonItem("Video ListView", "scorm", "listview_video"));
        lesson7.addLessonItem(new LessonItem("Bài tập ListView", "assignment", "Tạo danh sách sinh viên"));
        lesson7.addLessonItem(new LessonItem("Quiz 7", "quiz", "quiz_id_7"));
        sampleLessons.add(lesson7);

        // Lesson 8
        Lesson lesson8 = new Lesson(
                "Thực hành/Thí nghiệm - Bài 8: RecyclerView và ViewHolder",
                "Các nhãn: 3 File: 1 Các gói SCORM: 2 Bài tập: 3",
                "0 / 9"
        );
        lesson8.addLessonItem(new LessonItem("RecyclerView", "label", "Giới thiệu RecyclerView"));
        lesson8.addLessonItem(new LessonItem("ViewHolder Pattern", "file", "viewholder.pdf"));
        lesson8.addLessonItem(new LessonItem("Video RecyclerView", "scorm", "recyclerview_video"));
        lesson8.addLessonItem(new LessonItem("Bài tập RecyclerView", "assignment", "Tạo danh sách sản phẩm"));
        lesson8.addLessonItem(new LessonItem("Quiz 8", "quiz", "quiz_id_8"));
        sampleLessons.add(lesson8);

        // Lesson 9
        Lesson lesson9 = new Lesson(
                "Thực hành/Thí nghiệm - Bài 9: Fragment và Navigation",
                "Các nhãn: 3 File: 1 Các gói SCORM: 2 Bài tập: 3",
                "0 / 9"
        );
        lesson9.addLessonItem(new LessonItem("Fragment cơ bản", "label", "Giới thiệu Fragment"));
        lesson9.addLessonItem(new LessonItem("Fragment Communication", "file", "fragment_comm.pdf"));
        lesson9.addLessonItem(new LessonItem("Video Fragment", "scorm", "fragment_video"));
        lesson9.addLessonItem(new LessonItem("Bài tập Fragment", "assignment", "Ứng dụng Tab Layout"));
        lesson9.addLessonItem(new LessonItem("Quiz 9", "quiz", "quiz_id_9"));
        sampleLessons.add(lesson9);

        return sampleLessons;
    }

    private void loadDataFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);

        firebaseManager.getAllLessons(new FirebaseManager.OnDataLoadListener() {
            @Override
            public void onDataLoaded(List<Lesson> loadedLessons) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        lessons.clear();
                        lessons.addAll(loadedLessons);
                        lessonAdapter.updateData(lessons);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this,
                                "Lỗi: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data khi quay lại activity để cập nhật progress
        if (!isFirstLoad) {
            loadDataFromFirebase();
        }
    }
}