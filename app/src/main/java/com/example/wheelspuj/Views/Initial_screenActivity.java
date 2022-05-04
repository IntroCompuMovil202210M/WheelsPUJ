package com.example.wheelspuj.Views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class Initial_screenActivity extends AppCompatActivity {
    private TextView link;
    private Button login;

    FirebaseAuth mAuth;
    RequestQueue requestQueue;

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
            Intent intent = new Intent(Initial_screenActivity.this, Choose_Option.class);
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
                                if (d.getEmail().equals(currentUser.getEmail())) {
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
                        if (!response.equals("null")){
                            Type type = new TypeToken<HashMap<String, Passenger>>() {
                            }.getType();
                            HashMap<String, Passenger> passengersResponse = new Gson().fromJson(response, type);
                            for (Passenger p : passengersResponse.values()) {
                                if (p.getEmail().equals(currentUser.getEmail())) {
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