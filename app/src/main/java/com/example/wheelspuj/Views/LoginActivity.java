package com.example.wheelspuj.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wheelspuj.R;
import com.example.wheelspuj.models.Driver;
import com.example.wheelspuj.models.Passenger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button login;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    RequestQueue requestQueue;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // inflando datos
        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        login = findViewById(R.id.logginButtonn);

        mAuth = FirebaseAuth.getInstance();
        // mAuth.signOut();

        prefs = getSharedPreferences(this.getString(R.string.app_name), MODE_PRIVATE);
        editor = prefs.edit();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    String emailText = email.getText().toString();
                    String passwordText = password.getText().toString();
                    mAuth.signInWithEmailAndPassword(emailText, passwordText)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(view.getContext(), "Datos erroneos", Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    } else {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    }
                                }
                            });
                } else {
                    Toast.makeText(view.getContext(), "Email y contraseña requeridos", Toast.LENGTH_SHORT).show();
                }
            }
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
        } else {
            email.setText("");
            password.setText("");
        }
    }

    private void getDriver(FirebaseUser currentUser) {
        requestQueue = Volley.newRequestQueue(this);
        String url = "https://wheelspujmovil-default-rtdb.firebaseio.com/drivers.json";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals("null")) {
                            Type type = new TypeToken<HashMap<String, Driver>>() {
                            }.getType();
                            HashMap<String, Driver> driversResponse = new Gson().fromJson(response, type);
                            for (Driver d : driversResponse.values()) {
                                if (d.getEmail().equals(email.getText().toString())) {
                                    Intent intent = new Intent(getBaseContext(), DriverMainActivity.class);
                                    intent.putExtra("user", currentUser.getEmail());
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue.add(request);
    }

    private void getPassenger(FirebaseUser currentUser) {
        requestQueue = Volley.newRequestQueue(this);
        String url = "https://wheelspujmovil-default-rtdb.firebaseio.com/passengers.json";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals("null")) {
                            Type type = new TypeToken<HashMap<String, Passenger>>() {
                            }.getType();
                            HashMap<String, Passenger> passengersResponse = new Gson().fromJson(response, type);
                            for (Passenger p : passengersResponse.values()) {
                                if (p.getEmail().equals(email.getText().toString())) {
                                    Intent intent = new Intent(getBaseContext(), PassengerMainActivity.class);
                                    intent.putExtra("user", currentUser.getEmail());
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue.add(request);
    }
}