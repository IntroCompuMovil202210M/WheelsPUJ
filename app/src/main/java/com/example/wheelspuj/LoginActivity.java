package com.example.wheelspuj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private TextView emailInput;
    private TextView passwordInput;
    static final String USER_CN = "UserCheck";
    static final String TAG = "LoginActivity";
    public static final String PREFS = "SelfExpPrefs";
    private SharedPreferences preferences=null;

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
        if (!done) {
            username = null;
        }
        Log.i(TAG, "LogIn Status "+done);
        return new Pair<>(driver, username);
    }

    private String getSharedPreferenceID() {
        //Se lee el cache
        String id=preferences.getString("id", null);
        return id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.logginButtonn);

        preferences=getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String id=getSharedPreferenceID();
        Log.i(TAG, "ID registrado es "+id);
        if (id!=null){
            //Hay un usuario logueado en el equipo
            String username="";
            Boolean driver=false;
            boolean done=false;
                try {
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
                    ParseQuery parseQuery = ParseQuery.getQuery(USER_CN);
                    for (Object obj : parseQuery.find()) {
                        if (!done) {
                            ParseObject row = (ParseObject) obj;
                            username = (String) row.get("username");

                            messageDigest.update(username.getBytes());
                            String stringHash = new String(messageDigest.digest());
                            Log.i(TAG, "User is" + username + id.equals(stringHash));
                            if (id.equals(stringHash)) {
                                driver = (Boolean) row.get("driver");
    done=true;
                            }
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            if (!driver) {
                Intent intent = new Intent(LoginActivity.this, Home.class);
                intent.putExtra("username", username);
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoginActivity.this, DriverHome.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        }

        Log.i(TAG, "LogIn Attempt");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                Pair<Boolean, String> pair = logIn(name, password);
                String username = pair.getValue1();
                if (username != null) {
                    saveSharedPreference(username);
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

    private void saveSharedPreference(String username) {
        //Se guarda el cache, se asume que no existe si llega a este punto
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(username.getBytes());
            String stringHash = new String(messageDigest.digest());
            SharedPreferences.Editor edit=preferences.edit();
            edit.putString("id", stringHash);
            edit.commit();

            Log.i(TAG, username+""+preferences.getString("id",""));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}