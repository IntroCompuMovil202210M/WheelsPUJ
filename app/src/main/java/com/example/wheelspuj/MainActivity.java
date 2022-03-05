package com.example.wheelspuj;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button loginButton;
    private TextView emailInput;
    private TextView passwordInput;
    private final int REQUEST_CODE = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            assert data != null;
            String message = data.getStringExtra("message_back");

            Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.logginButton);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String name = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if(!name.isEmpty()){
                    Intent intent = new Intent(MainActivity.this, UserSectionActivity.class);
                    intent.putExtra("email",name);
                    intent.putExtra("password",password);


                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this,"Ingrese un email",Toast.LENGTH_SHORT)
                            .show();
                }
                }

        });


    }



}