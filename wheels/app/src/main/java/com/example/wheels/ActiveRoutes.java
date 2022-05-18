package com.example.wheels;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.database.MatrixCursor;
import android.os.Bundle;
import android.widget.ListView;

import com.example.wheels.Adapters.RoutesAdapter;
import com.example.wheels.Adapters.TripAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import Models.Driver;
import Models.Route;
import Models.Trip;

public class ActiveRoutes extends AppCompatActivity {
    private ListView listView;
    private List<Driver> d;
    private FirebaseFirestore db;
    private RoutesAdapter routesAdapter;
    private FirebaseAuth auth;
    private CollectionReference dbTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_trips);
        listView = findViewById(R.id.listView);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        getRoutes();


    }

    private void getRoutes() {
        dbTrips = db.collection("driver");
        dbTrips.whereEqualTo("available", true).addSnapshotListener((value, error) -> {
            if (!value.isEmpty()) {
                routesAdapter = new RoutesAdapter(ActiveRoutes.this, null, 0);
                MatrixCursor driverCursor = new MatrixCursor(new String[]{"_id", "name", "uri"});
                d = value.toObjects(Driver.class);
                int i = 0;
                for (Driver driver : d) {
                    for (Route r : driver.getRoutes()) {
                        if (r.isAvailable()) {
                            driverCursor.newRow()
                                    .add("_id", i + 1)
                                    .add("name", r.getName())
                                    .add("uri", driver.getImage());
                            i++;
                        }
                    }
                }
                routesAdapter.changeCursor(driverCursor);
                listView.setAdapter(routesAdapter);
            }
        });
    }
}