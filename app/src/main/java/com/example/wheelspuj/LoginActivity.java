package com.example.wheelspuj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.javatuples.Pair;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private TextView emailInput;
    private TextView passwordInput;
    static final String USER_CN = "User";
    static final String TAG = "LoginActivity";

    static Pair<Boolean, String> logIn(String username, String password) {
        ParseQuery parseQuery = ParseQuery.getQuery(USER_CN);
        Boolean driver=false;
        boolean done=false;
        try {
            for (Object obj : parseQuery.find()) {
                ParseObject row= (ParseObject) obj;
                if (username.equals(row.get("username")) && password.equals(row.get("password"))) {
                    driver = (Boolean) row.get("driver");
                    done=true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (!done)
            username = null;
        Log.i(TAG, "LogIn Status "+done);
        return new Pair<>(driver, username);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.logginButtonn);

        Log.i(TAG, "LogIn Attempt");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                Pair<Boolean, String> pair = logIn(name, password);
                String username = pair.getValue1();
                if (username != null) {
                    if (!pair.getValue0()) {
                        Intent intent = new Intent(LoginActivity.this, Home.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(LoginActivity.this, DriverHome.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                }
                else
                    Toast.makeText(getApplicationContext(), "Usuario incorrecto", Toast.LENGTH_LONG).show();
            }
        });
    }
}