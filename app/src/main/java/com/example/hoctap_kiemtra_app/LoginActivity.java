package com.example.hoctap_kiemtra_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        db = FirebaseFirestore.getInstance();


        btnLogin.setOnClickListener(v -> checkUser());
    }


    private void checkUser(){
        String userName = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        db.collection("students").whereEqualTo("email",userName).whereEqualTo("password",password).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        User user = document.toObject(User.class);
                        if(user!=null){
                            Intent intent = new Intent(LoginActivity.this,StudentHomeActivity.class);
                            startActivity(intent);
                        }

                    }
                }
                else{
                    Log.w("Tag2","Error:"+task.getException());
                }
            }
        });
        db.collection("lecturers").whereEqualTo("email",userName).whereEqualTo("password",password).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        User user = document.toObject(User.class);
                        if(user!=null){
                            Intent intent = new Intent(LoginActivity.this,LecturerHomeActivity.class);
                            startActivity(intent);
                        }

                    }
                }
                else{
                    Log.w("Tag2","Error:"+task.getException());
                }
            }
        });
        Toast.makeText(this,"Đăng nhâp lỗi!!!",Toast.LENGTH_SHORT).show();

    }

}