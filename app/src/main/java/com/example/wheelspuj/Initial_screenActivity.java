package com.example.wheelspuj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Initial_screenActivity extends AppCompatActivity {
    private TextView link;
    private Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_screen);
        link = findViewById(R.id.registerLink);
        login = findViewById(R.id.loginButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Initial_screenActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Initial_screenActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }
}