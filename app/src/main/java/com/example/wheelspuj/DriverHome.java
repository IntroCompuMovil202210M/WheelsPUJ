package com.example.wheelspuj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class DriverHome extends AppCompatActivity {
    FloatingActionButton floatingButton;
    ExtendedFloatingActionButton button;
    ImageButton backHome;
    NavigationView nav;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Fragment fragment = new GoogleMap();
        Fragment profile=new Profile();
        Fragment historial=new TripsHistorial();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
        floatingButton = findViewById(R.id.home);
        backHome = findViewById(R.id.backHome);
        nav = findViewById(R.id.navId);
        button=findViewById(R.id.button);
        floatingButton.setOnClickListener(v -> nav.setVisibility(View.VISIBLE));
        backHome.setOnClickListener(view -> nav.setVisibility(View.INVISIBLE ));
        button.setOnClickListener(view -> new AlertDialog.Builder(DriverHome.this).setTitle("Recoger pasajeros").setMessage("Esta seguro que desea recoger el pasajero Juanito a 100m de tu ubicacion actual?").setPositiveButton("si", (dialogInterface, i) -> Toast.makeText(DriverHome.this,"Aceptado",Toast.LENGTH_SHORT).show()).setNegativeButton("no", (dialogInterface, i) -> Toast.makeText(DriverHome.this,"Cancelado",Toast.LENGTH_SHORT).show()).create().show());
        nav.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.nav_inicio:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
                    nav.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.VISIBLE);
                    break;
                case R.id.nav_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, profile).commit();
                    nav.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.INVISIBLE);
                    break;
                case R.id.nav_viajes:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, historial).commit();
                    nav.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.INVISIBLE);
                    break;
                case R.id.nav_info:
                    Toast.makeText(DriverHome.this, "Info is clicked", Toast.LENGTH_SHORT).show();
                    break;
                default:

            }
            return false;
        });
    }
}