package com.example.wheelspuj.Views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wheelspuj.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Initial_screenActivity extends AppCompatActivity {
    private TextView link;
    private Button login;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_screen);
        link = findViewById(R.id.registerLink);
        login = findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(view -> {
            Intent intent = new Intent(Initial_screenActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        link.setOnClickListener(view -> {
            Intent intent = new Intent(Initial_screenActivity.this, RegisterDriverActivity.class);
            startActivity(intent);
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(getBaseContext(), DriverMainActivity.class);
            intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
        }
    }
}