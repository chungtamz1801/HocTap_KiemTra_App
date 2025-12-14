package com.example.hoctap_kiemtra_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LessonItemAdapter extends RecyclerView.Adapter<LessonItemAdapter.ViewHolder> {

    private List<LessonItem> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(LessonItem item, int position);
        void onItemStatusChanged(LessonItem item, int position, boolean completed);
    }

    public LessonItemAdapter(List<LessonItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lesson_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LessonItem item = items.get(position);
        holder.bind(item, position, listener);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivIcon;
        private TextView tvTitle;
        private TextView tvType;
        private CheckBox cbCompleted;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivItemIcon);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvType = itemView.findViewById(R.id.tvItemType);
            cbCompleted = itemView.findViewById(R.id.cbItemCompleted);
        }

        public void bind(LessonItem item, int position, OnItemClickListener listener) {
            tvTitle.setText(item.getTitle());
            tvType.setText(getTypeDisplay(item.getType()));
            cbCompleted.setChecked(item.isCompleted());

            // Set icon based on type
            setIconForType(item.getType());

            // Handle checkbox change
            cbCompleted.setOnCheckedChangeListener(null); // Clear old listener
            cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onItemStatusChanged(item, position, isChecked);
                }
            });

            // Handle item click
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item, position);
                }
            });
        }

        private void setIconForType(String type) {
            int iconRes;
            switch (type) {
                case "label":
                    iconRes = android.R.drawable.ic_menu_info_details;
                    break;
                case "file":
                    iconRes = android.R.drawable.ic_menu_gallery;
                    break;
                case "scorm":
                    iconRes = android.R.drawable.ic_menu_slideshow;
                    break;
                case "assignment":
                    iconRes = android.R.drawable.ic_menu_edit;
                    break;
                case "quiz":
                    iconRes = android.R.drawable.ic_menu_help;
                    break;
                case "link":
                    iconRes = android.R.drawable.ic_menu_share;
                    break;
                default:
                    iconRes = android.R.drawable.ic_menu_info_details;
                    break;
            }
            ivIcon.setImageResource(iconRes);
        }

        private String getTypeDisplay(String type) {
            switch (type) {
                case "label":
                    return "Nhãn";
                case "file":
                    return "Tệp";
                case "scorm":
                    return "SCORM";
                case "assignment":
                    return "Bài tập";
                case "quiz":
                    return "Trắc nghiệm";
                case "link":
                    return "Liên kết";
                default:
                    return type;
            }
        }
    }
}