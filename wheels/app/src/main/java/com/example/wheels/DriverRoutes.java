package com.example.wheels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.wheels.Adapters.TripAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import Models.Driver;
import Models.Position;
import Models.Route;
import Models.Trip;

public class DriverRoutes extends AppCompatActivity {

    private ListView listView;
    private Driver d;
    private FirebaseFirestore db;
    private TripAdapter tripAdapter;
    private FirebaseAuth auth;
    private CollectionReference dbTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_routes);
        db = FirebaseFirestore.getInstance();
        dbTrips = db.collection("driver");
        auth = FirebaseAuth.getInstance();
        listView = findViewById(R.id.listView);
        getTrips();
        listView.setOnItemLongClickListener((parent, view, i, l) -> {
            Cursor cursor = (Cursor) parent.getItemAtPosition(i);
            if (cursor != null) {
                new AlertDialog.Builder(DriverRoutes.this).setTitle("Eliminar ruta").setPositiveButton("si", (dialogInterface, i12) -> {
                    d.getRoutes().remove(d.getRoutes().get(i));
                    if (!dbTrips.document(auth.getCurrentUser().getEmail()).update("routes", d.getRoutes()).isSuccessful()) {
                        Toast.makeText(DriverRoutes.this, "Hubo un error al agregar la ruta", Toast.LENGTH_SHORT);
                    }
                }).setNegativeButton("no", (dialogInterface, i1) -> dialogInterface.dismiss()).create().show();

            }
            return false;
        });
        listView.setOnItemClickListener((parent, view, i, l) -> {
            Cursor cursor = (Cursor) parent.getItemAtPosition(i);
            if (cursor != null) {
                if (d.getRoutes().get(i).isAvailable()) {
                    d.getRoutes().get(i).setAvailable(false);
                    new AlertDialog.Builder(DriverRoutes.this).setTitle("Desactivar ruta").setPositiveButton("si", (dialogInterface, i12) -> {
                        if (!dbTrips.document(auth.getCurrentUser().getEmail()).update("routes", d.getRoutes()).isSuccessful()) {
                            Toast.makeText(DriverRoutes.this, "Hubo un error al actualizar la ruta", Toast.LENGTH_SHORT);
                        }
                    }).setNegativeButton("no", (dialogInterface, i1) -> dialogInterface.dismiss()).create().show();
                } else {
                    d.getRoutes().get(i).setAvailable(true);
                    new AlertDialog.Builder(DriverRoutes.this).setTitle("Activar ruta").setPositiveButton("si", (dialogInterface, i12) -> {
                        if (!dbTrips.document(auth.getCurrentUser().getEmail()).update("routes", d.getRoutes()).isSuccessful()) {
                            Toast.makeText(DriverRoutes.this, "Hubo un error al actualizar la ruta", Toast.LENGTH_SHORT);
                        }
                        onBackPressed();
                    }).setNegativeButton("no", (dialogInterface, i1) -> dialogInterface.dismiss()).create().show();

                }
            }

        });

    }

    private void getTrips() {
        dbTrips.document(auth.getCurrentUser().getEmail()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                d = value.toObject(Driver.class);
                int i = 0;
                tripAdapter = new TripAdapter(DriverRoutes.this, null, 0);
                MatrixCursor driverCursor = new MatrixCursor(new String[]{"_id", "name", "state"});
                for (Route r : d.getRoutes()) {
                    driverCursor.newRow()
                            .add("_id", i + 1)
                            .add("name", r.getName())
                            .add("state", r.isAvailable());
                    i++;
                }

                tripAdapter.changeCursor(driverCursor);
                listView.setAdapter(tripAdapter);
            }
        });

    }
}