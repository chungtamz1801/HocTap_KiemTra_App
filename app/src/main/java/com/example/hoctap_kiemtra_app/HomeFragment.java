package com.example.hoctap_kiemtra_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatapp.LoginActivity;

// ============ HOME FRAGMENT ============
public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView tvWelcome = view.findViewById(R.id.tvWelcome);
        Button btnQuickStart = view.findViewById(R.id.btnQuickStart);

        // Lấy tên người dùng từ SharedPreferences
        android.content.SharedPreferences prefs = requireActivity().getSharedPreferences("QUIZ_APP", android.content.Context.MODE_PRIVATE);
        String userName = prefs.getString("FULLNAME", "Sinh viên");
        tvWelcome.setText("Xin chào, " + userName + "!");

        btnQuickStart.setOnClickListener(v -> {
            // Chuyển sang tab Kiểm tra
            ((MainActivityNew) requireActivity()).findViewById(R.id.bottomNavigation);
            com.google.android.material.bottomnavigation.BottomNavigationView nav =
                    requireActivity().findViewById(R.id.bottomNavigation);
            nav.setSelectedItemId(R.id.nav_exams);
        });

        return view;
    }
}

