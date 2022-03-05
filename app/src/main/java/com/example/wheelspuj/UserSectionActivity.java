package com.example.wheelspuj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UserSectionActivity extends AppCompatActivity {
    private TextView textUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_section);

        Bundle extra = getIntent().getExtras();
        textUser = findViewById(R.id.textUserSection);
        textUser.setText("mi email es : " + (extra.getString("email") + " Mi contraseña es: " + (extra.getString("password"))));

        textUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("message_back","From UserActivity");
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}