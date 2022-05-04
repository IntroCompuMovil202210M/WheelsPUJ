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
import androidx.fragment.app.FragmentContainerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wheelspuj.R;
import com.example.wheelspuj.models.Driver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class DriverMainActivity extends AppCompatActivity {

    TextView name;
    Button goRoutes;
    Button add_Route;
    Button goCars, closeSession;
    FragmentContainerView routesFragment;
    ImageView profilePicture;

    String email, idDriver, imageSrc;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Driver currentDriver;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_main_activity);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        email = getIntent().getStringExtra("user");

        prefs = getSharedPreferences(this.getString(R.string.app_name), MODE_PRIVATE);
        editor = prefs.edit();

        getDriver();

        goCars = findViewById(R.id.button3);
        closeSession = findViewById(R.id.closeSession);
        profilePicture = findViewById(R.id.user_picture);


//        routesFragment = findViewById(R.id.fragmentRoutes);
//        routesFragment.setVisibility(View.INVISIBLE);

        name = findViewById(R.id.name_user);
        //name.setText(currentDriver.getName());


        closeSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(DriverMainActivity.this, Initial_screenActivity.class));
            }
        });

        goRoutes = findViewById(R.id.routesButton);
        goRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                /*
                if(  ){

                }else{
                Toast.makeText(DriverMainActivity.this,"No tiene rutas creadas",Toast.LENGTH_LONG).show();
                }
                */
            }
        });


        add_Route = findViewById(R.id.addRoute_button);
        add_Route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverMainActivity.this, NewRoute.class);
                startActivity(intent);
            }
        });

        //Toast.makeText(this,getIntent().getStringExtra("idActual").toString(),Toast.LENGTH_SHORT  ).show();

    }

    private void getDriver() {
        requestQueue = Volley.newRequestQueue(this);
        String url = "https://wheelspujmovil-default-rtdb.firebaseio.com/drivers.json";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type type = new TypeToken<HashMap<String, Driver>>() {
                        }.getType();
                        HashMap<String, Driver> driversResponse = new Gson().fromJson(response, type);
                        for (Driver d : driversResponse.values()) {
                            if (d.getEmail().equals(email)) {
                                editor.putString("idDriver", d.getId());
                                editor.commit();
                                byte[] imageBytes;
                                imageBytes = Base64.decode(d.getImage(), Base64.DEFAULT);
                                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                profilePicture.setImageBitmap(decodedImage);
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
