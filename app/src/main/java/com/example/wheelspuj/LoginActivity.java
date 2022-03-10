package com.example.wheelspuj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private TextView emailInput;
    private TextView passwordInput;
    private final int REQUEST_CODE = 2;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.logginButtonn);

        
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String name = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if(!name.isEmpty()){
                    Intent intent = new Intent(LoginActivity.this, Home.class);
                    intent.putExtra("email",name);
                    intent.putExtra("password",password);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(LoginActivity.this, DriverHome.class);
                    startActivity(intent);
                }
                }

        });


    }



}