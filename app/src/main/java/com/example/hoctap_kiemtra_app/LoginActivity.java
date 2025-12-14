package com.example.hoctap_kiemtra_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;

import com.example.hoctap_kiemtra_app.models.UserModel;
import com.example.hoctap_kiemtra_app.utils.AndroidUtil;
import com.example.hoctap_kiemtra_app.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    EditText userNameEdt,passwordEdt;
    Button loginBtn,registerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWidgets();
        setEvents();
    }
    private void setEvents(){
        loginBtn.setOnClickListener(v->login());
    }
    private void getWidgets(){
        userNameEdt = findViewById(R.id.userNameEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
    }
    private void login(){
        String userName = userNameEdt.getText().toString();
        String password = passwordEdt.getText().toString();
        FirebaseUtil.getUserOnLogin(userName,password).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        UserModel user = document.toObject(UserModel.class);
                        FirebaseUtil.setUserID(user.getUserID());
                        Intent intent = new Intent(LoginActivity.this,StudentActivity.class);
                        AndroidUtil.passUserModelAsIntent(intent,user);
                        startActivity(intent);
                    }
                }
                else{
                    Log.w("Tag2","Error:"+task.getException());
                }
            }
        });
    }
}