package com.example.wheelspuj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class Home extends AppCompatActivity {
    FloatingActionButton floatingButton;
    NavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Fragment fragment = new GoogleMap();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
        floatingButton = findViewById(R.id.home);
        nav = findViewById(R.id.navId);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav.setVisibility(View.VISIBLE);
            }
        });
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        Toast.makeText(Home.this, "Home is clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_viajes:
                        Toast.makeText(Home.this, "Trips is clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_info:
                        Toast.makeText(Home.this, "Info is clicked", Toast.LENGTH_SHORT).show();
                        break;

                }
                return false;
            }
        });
    }
}