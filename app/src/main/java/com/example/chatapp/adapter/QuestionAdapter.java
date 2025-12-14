package com.example.chatapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Question;
import com.example.chatapp.R;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    List<Question> list;
    boolean isReviewMode = false; // Biến cờ đánh dấu chế độ xem lại

    public QuestionAdapter(List<Question> list) {
        this.list = list;
    }

    // Hàm bật chế độ xem lại
    public void enableReviewMode() {
        this.isReviewMode = true;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Question q = list.get(pos);

        // --- SỬA ĐỔI TẠI ĐÂY (Dùng Getter thay vì gọi biến trực tiếp) ---
        h.txtQuestion.setText("Câu " + (pos + 1) + ": " + q.getContent());
        h.optA.setText(q.getA());
        h.optB.setText(q.getB());
        h.optC.setText(q.getC());
        h.optD.setText(q.getD());
        // -------------------------------------------------------------

        h.rgOptions.setOnCheckedChangeListener(null);
        h.rgOptions.clearCheck();

        // Hiển thị đáp án người dùng đã chọn
        // Dùng q.getUserAnswer() thay vì q.userAnswer
        if ("A".equals(q.getUserAnswer())) h.optA.setChecked(true);
        else if ("B".equals(q.getUserAnswer())) h.optB.setChecked(true);
        else if ("C".equals(q.getUserAnswer())) h.optC.setChecked(true);
        else if ("D".equals(q.getUserAnswer())) h.optD.setChecked(true);

        if (isReviewMode) {
            // 1. Vô hiệu hóa để người dùng không sửa được
            h.optA.setEnabled(false);
            h.optB.setEnabled(false);
            h.optC.setEnabled(false);
            h.optD.setEnabled(false);

            // 2. Reset màu về mặc định (đen)
            h.optA.setTextColor(Color.BLACK);
            h.optB.setTextColor(Color.BLACK);
            h.optC.setTextColor(Color.BLACK);
            h.optD.setTextColor(Color.BLACK);

            // 3. Tô màu đáp án ĐÚNG (màu XANH)
            // Dùng q.getCorrectAnswer()
            if ("A".equals(q.getCorrectAnswer())) h.optA.setTextColor(Color.GREEN);
            if ("B".equals(q.getCorrectAnswer())) h.optB.setTextColor(Color.GREEN);
            if ("C".equals(q.getCorrectAnswer())) h.optC.setTextColor(Color.GREEN);
            if ("D".equals(q.getCorrectAnswer())) h.optD.setTextColor(Color.GREEN);

            // 4. Nếu người dùng chọn SAI -> Tô màu ĐỎ vào cái đã chọn
            if (q.getUserAnswer() != null && !q.getUserAnswer().equals(q.getCorrectAnswer())) {
                if ("A".equals(q.getUserAnswer())) h.optA.setTextColor(Color.RED);
                if ("B".equals(q.getUserAnswer())) h.optB.setTextColor(Color.RED);
                if ("C".equals(q.getUserAnswer())) h.optC.setTextColor(Color.RED);
                if ("D".equals(q.getUserAnswer())) h.optD.setTextColor(Color.RED);
            }
        } else {
            // Chế độ làm bài: Cho phép chọn và lưu
            h.optA.setEnabled(true);
            h.optB.setEnabled(true);
            h.optC.setEnabled(true);
            h.optD.setEnabled(true);

            // Reset màu
            h.optA.setTextColor(Color.BLACK);
            h.optB.setTextColor(Color.BLACK);
            h.optC.setTextColor(Color.BLACK);
            h.optD.setTextColor(Color.BLACK);

            h.rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
                // Dùng Setter để lưu đáp án
                if (checkedId == R.id.optA) q.setUserAnswer("A");
                else if (checkedId == R.id.optB) q.setUserAnswer("B");
                else if (checkedId == R.id.optC) q.setUserAnswer("C");
                else if (checkedId == R.id.optD) q.setUserAnswer("D");
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtQuestion;
        RadioGroup rgOptions;
        RadioButton optA, optB, optC, optD;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtQuestion = itemView.findViewById(R.id.txtQuestion);
            rgOptions = itemView.findViewById(R.id.rgOptions);
            optA = itemView.findViewById(R.id.optA);
            optB = itemView.findViewById(R.id.optB);
            optC = itemView.findViewById(R.id.optC);
            optD = itemView.findViewById(R.id.optD);
        }
    }
}