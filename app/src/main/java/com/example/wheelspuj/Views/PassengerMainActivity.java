package com.example.wheelspuj.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wheelspuj.R;
import com.example.wheelspuj.models.Passenger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class PassengerMainActivity extends AppCompatActivity {

    ImageView passengerPicture;
    Button closeSession;
    TextView passengerName;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String email;

    RequestQueue requestQueue;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passenger_main_activity);

        mAuth = FirebaseAuth.getInstance();

        passengerPicture = findViewById(R.id.passenger_picture);
        closeSession = findViewById(R.id.closeSessionPassenger);
        passengerPicture = findViewById(R.id.passenger_picture);

        getPassenger();

        email = getIntent().getStringExtra("user");

        prefs = getSharedPreferences(this.getString(R.string.app_name), MODE_PRIVATE);
        editor = prefs.edit();

        closeSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                editor.clear();
                startActivity(new Intent(PassengerMainActivity.this, Initial_screenActivity.class));
            }
        });
    }

    private void getPassenger() {
        requestQueue = Volley.newRequestQueue(this);
        String url = "https://wheelspujmovil-default-rtdb.firebaseio.com/passengers.json";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type type = new TypeToken<HashMap<String, Passenger>>() {
                        }.getType();
                        HashMap<String, Passenger> passengersResponse = new Gson().fromJson(response, type);
                        for (Passenger p : passengersResponse.values()) {
                            if (p.getEmail().equals(email)) {
                                editor.putString("idPassenger", p.getId());
                                editor.commit();
                                passengerName.setText(p.getName());
                                byte[] imageBytes;
                                imageBytes = Base64.decode(p.getImage(), Base64.DEFAULT);
                                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                passengerPicture.setImageBitmap(decodedImage);
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
