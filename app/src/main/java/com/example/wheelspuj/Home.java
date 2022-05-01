package com.example.wheelspuj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class Home extends AppCompatActivity {
    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }
    FloatingActionButton floatingButton;
    ExtendedFloatingActionButton button;
    ImageButton backHome;
    NavigationView nav;
    static final String USER_CN = "UserCheck";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        contextOfApplication = getApplicationContext();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Fragment fragment = new OSMap(getUsername(), false);
        Fragment possibleTrips=new PossibleTrips();
        Fragment profile=new Profile(getUsername(), getPhone());
        Fragment historial=new TripsHistorial();
        loadFragment(fragment);
        floatingButton = findViewById(R.id.home);
        backHome = findViewById(R.id.backHome);
        nav = findViewById(R.id.navId);
        button=findViewById(R.id.button);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav.setVisibility(View.VISIBLE);
            }
        });
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              nav.setVisibility(View.INVISIBLE );
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Home.this,"Trips",Toast.LENGTH_SHORT).show();
                loadFragment(possibleTrips);
            }
        });
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                        Toast.makeText(Home.this, "Info is clicked", Toast.LENGTH_SHORT).show();
                        break;

                }
                return false;
            }
        });
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

    private String getUsername() {
        return getIntent().getStringExtra("username");
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit(); // save the changes
    }
}