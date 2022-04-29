package com.example.wheelspuj;

import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Objects;

public class DriverHome extends AppCompatActivity {
    FloatingActionButton floatingButton;
    ExtendedFloatingActionButton button;
    ImageButton backHome;
    NavigationView nav;
    static final String USER_CN = "UserCheck";


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Fragment fragment = new OSMap(getUsername(), true);
        Fragment profile=new Profile(getUsername(), getPhone());
        Fragment historial=new TripsHistorial();
        loadFragment(fragment);
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
                    loadFragment(fragment);
                    nav.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.VISIBLE);
                    break;
                case R.id.nav_home:
                    loadFragment(profile);
                    nav.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.INVISIBLE);
                    break;
                case R.id.nav_viajes:
                    loadFragment(historial);
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

    private String getUsername() {
        return getIntent().getStringExtra("username");
    }

    private String getPhone(){
        String phone=null;
        ParseQuery parseQuery = ParseQuery.getQuery(USER_CN);
        try {
            for (Object obj : parseQuery.find()) {
                ParseObject row= (ParseObject) obj;
                if (getUsername().equals(row.get("username")))
                    phone = (String) row.get("phone");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return phone;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit(); // save the changes
    }
}