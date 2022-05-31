package com.example.wheels;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.wheels.Adapters.TripAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

import Models.Driver;
import Models.Route;

public class DriverRoutes extends AppCompatActivity {

    private ListView listView;
    private Driver d;
    private FirebaseFirestore db;
    private TripAdapter tripAdapter;
    private boolean pressed = false;
    private FirebaseAuth auth;
    private CollectionReference dbDrivers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_routes);
        db = FirebaseFirestore.getInstance();
        dbDrivers = db.collection("driver");
        auth = FirebaseAuth.getInstance();
        listView = findViewById(R.id.listView);
        getTrips();
        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            ;
            AlertDialog.Builder mbuilder = new AlertDialog.Builder(DriverRoutes.this);
            mbuilder.setTitle("Eliminar ruta");
            mbuilder.setMessage("Esta seguro que desear eliminar la ruta " + d.getRoutes().get(i).getName() + "?");
            mbuilder.setPositiveButton("Si", (dialogInterface, i12) -> {
                d.getRoutes().remove(i);
                update(d.getMail(), d.getRoutes());
            });
            mbuilder.setNegativeButton("No", (dialogInterface, i1) -> {
                dialogInterface.dismiss();
            });
            mbuilder.create().show();
            return false;
        });
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            boolean state = d.getRoutes().get(i).isAvailable();
            if (!state == true)
                for (int r = 0; r < d.getRoutes().size(); r++)
                    if (d.getRoutes().get(i) == d.getRoutes().get(r))
                        d.getRoutes().get(i).setAvailable(true);
                    else
                        d.getRoutes().get(r).setAvailable(false);
            else
                d.getRoutes().get(i).setAvailable(false);
            update(d.getMail(), d.getRoutes());
        });
    }
    private void update(String mail, List<Route> routes) {
        dbDrivers.document(mail).update("routes", routes).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                Toast.makeText(DriverRoutes.this, "Actualizado", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(DriverRoutes.this, "Hubo un error al actualizar", Toast.LENGTH_SHORT).show();
        });

    }

    private void getTrips() {
        dbDrivers.document(auth.getCurrentUser().getEmail()).addSnapshotListener((value, error) -> {
            d = value.toObject(Driver.class);
            int i = 0;
            tripAdapter = new TripAdapter(DriverRoutes.this, null, 0);
            MatrixCursor driverCursor = new MatrixCursor(new String[]{"_id", "name", "state", "index"});
            for (Route r : d.getRoutes()) {
                driverCursor.newRow()
                        .add("_id", i + 1)
                        .add("name", r.getName())
                        .add("state", r.isAvailable())
                        .add("index", d.getRoutes().indexOf(r));
                i++;
            }
            tripAdapter.changeCursor(driverCursor);
            listView.setAdapter(tripAdapter);
        });
    }
}