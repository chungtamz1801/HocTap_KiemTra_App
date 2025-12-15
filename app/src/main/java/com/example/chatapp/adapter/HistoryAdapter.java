package com.example.chatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatapp.model.ScoreHistory;
import com.example.hoctap_kiemtra_app.R; // Import R của project chính

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryVH> {

    private List<ScoreHistory> list;

    public HistoryAdapter(List<ScoreHistory> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public HistoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryVH holder, int position) {
        ScoreHistory item = list.get(position);

        holder.tvScore.setText(String.format("%.1f điểm", item.getScore()));

        // Format ngày giờ
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String dateStr = (item.getTimestamp() != null) ? sdf.format(item.getTimestamp()) : "Không rõ ngày";
        holder.tvDate.setText(dateStr);

        holder.tvTime.setText("Thời gian làm: " + (item.getTimeTaken() != null ? item.getTimeTaken() : "N/A"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class HistoryVH extends RecyclerView.ViewHolder {
        TextView tvDate, tvScore, tvTime;
        public HistoryVH(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvScore = itemView.findViewById(R.id.tvHistoryScore);
            tvTime = itemView.findViewById(R.id.tvHistoryTime);
        }
    }
}