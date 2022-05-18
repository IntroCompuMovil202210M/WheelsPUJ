package com.example.wheels;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wheels.directionhelpers.FetchURL;
import com.example.wheels.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.wheels.databinding.ActivityHomeBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Models.Driver;
import Models.Position;
import Models.Route;

public class Home extends FragmentActivity implements OnMapReadyCallback, LocationListener, TaskLoadedCallback{
    private FusedLocationProviderClient fusedLocationClient;
    private final int MIN_TIME = 1000;
    private final int MIN_DISTANCE = 1;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private boolean camera = true;
    private Polyline currentPolyline;
    private List<Driver> drivers;
    private GoogleMap mMap;
    Route route;
    private ActivityHomeBinding binding;
    private Marker mymarker, searchMarker;
    private Map<String, Marker> map = new HashMap<>();
    private Map<String, Circle> circle = new HashMap<>();
    private LocationManager manager;
    private LatLng currentPosition;
    private Position position;
    private EditText search, start;
    private FloatingActionButton singOut, setPosition, searchTrip, chatBut;
    private Geocoder mGeocoder;

    @Override
    public void onBackPressed() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mGeocoder = new Geocoder(getBaseContext());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        singOut = findViewById(R.id.singout);
        chatBut=findViewById(R.id.chatButton);
        setPosition = findViewById(R.id.location);
        searchTrip = findViewById(R.id.trips);
        start = findViewById(R.id.Startpoint);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location!=null) {
                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                updatePassenger("position", new Position(location.getLatitude(), location.getLongitude()));
                getLocationUpdates();
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
                carListener();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "No se pudo obtener la ultima posicion", Toast.LENGTH_SHORT).show();
        });
        singOut.setOnClickListener(view -> {
                    auth.signOut();
                    Intent intent = new Intent(Home.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        chatBut.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, ViewUsersActivity.class);
            startActivity(intent);
        });
        setPosition.setOnClickListener(view -> {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(Home.this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(currentPosition.latitude, currentPosition.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0);
                updatePassenger("startPoint", new Position(currentPosition.latitude, currentPosition.longitude));
                start.setText(address);
                start.clearFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }


        });
        searchTrip.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, ActiveRoutes.class);
            startActivity(intent);
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(marker ->{
            new FetchURL(Home.this).execute(getUrl(currentPosition, marker.getPosition(), "driving"), "driving");

            return false;
        });
        if (mymarker == null)
            mymarker = mMap.addMarker(new MarkerOptions().position(currentPosition).title("Ubicación actual").icon(BitmapDescriptorFactory.fromResource(R.drawable.persona)));
        else
            mymarker.setPosition(currentPosition);
        if (camera) {
            camera = false;
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentPosition)
                    .zoom(20)
                    .tilt(45)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }


    private void updatePassenger(String field, Position location) {
        CollectionReference dbUsers = db.collection("passenger");
        dbUsers.document(auth.getCurrentUser().getEmail()).update(field, location).addOnSuccessListener(unused -> {

        }).addOnFailureListener(unused -> Toast.makeText(getApplicationContext(), "Error al actualizar localizacion", Toast.LENGTH_SHORT).show());

    }

    private void getLocationUpdates() {
        if (manager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) + ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this::onLocationChanged);
                } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this::onLocationChanged);

                } else {
                    Toast.makeText(this, "No provider", Toast.LENGTH_SHORT).show();
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void carListener() {
        db.collection("driver")
                .whereEqualTo("available", true)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        return;
                    }
                    drivers = value.toObjects(Driver.class);
                    if (drivers.isEmpty() && map.size() > 0) {
                        for (String key : map.keySet()) {
                            circle.get(key).remove();
                            circle.remove(key);
                            map.get(key).remove();
                            map.remove(key);
                        }
                        return;
                    }
                    for (Driver d : drivers) {
                        if (!d.getRoutes().isEmpty()) {
                            for (Route r : d.getRoutes()) {
                                if (r.isAvailable()) {
                                    route = r;
                                    break;
                                } else
                                    route = null;

                            }
                        } else
                            route = null;

                        if (map.containsKey(d.getMail())) {
                            map.get(d.getMail()).setPosition(new LatLng(d.getPosition().getLatitude(), d.getPosition().getLongitude()));
                            circle.get(d.getMail()).setCenter(new LatLng(d.getPosition().getLatitude(), d.getPosition().getLongitude()));

                        } else {
                            if (route != null) {
                                map.put(d.getMail(), mMap.addMarker(new MarkerOptions().position(new LatLng(d.getPosition().getLatitude(), d.getPosition().getLongitude()))));
                                map.get(d.getMail()).setTitle(route.getName());
                                map.get(d.getMail()).showInfoWindow();
                                circle.put(d.getMail(), mMap.addCircle(new CircleOptions().center(new LatLng(d.getPosition().getLatitude(), d.getPosition().getLongitude())).radius(150).strokeWidth(3f).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50))));
                            } else {
                                map.put(d.getMail(), mMap.addMarker(new MarkerOptions().position(new LatLng(d.getPosition().getLatitude(), d.getPosition().getLongitude()))));
                                circle.put(d.getMail(), mMap.addCircle(new CircleOptions().center(new LatLng(d.getPosition().getLatitude(), d.getPosition().getLongitude())).radius(150).strokeWidth(3f).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50))));
                                map.get(d.getMail()).showInfoWindow();
                                map.get(d.getMail()).setTitle("");

                            }
                        }
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(this);

                    }
                });
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null) {
            camera = true;
            position = new Position(location.getLatitude(), location.getLongitude());
            currentPosition = new LatLng(position.getLatitude(), position.getLongitude());
            updatePassenger("position", new Position(location.getLatitude(), location.getLongitude()));
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "No location", Toast.LENGTH_SHORT).show();
        }
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }



/* search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                String text = String.valueOf(search.getText());
                if (!text.isEmpty()) {
                    try {
                        List<Address> addresses = mGeocoder.getFromLocationName(text, 2);
                        if (addresses != null && !addresses.isEmpty()) {

                            Address addressResult = addresses.get(0);
                            LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                            if (mMap != null) {
                                if (searchMarker != null)
                                    searchMarker.remove();
                                searchMarker = mMap.addMarker(new MarkerOptions().position(position));
                                new FetchURL(Home.this).execute(getUrl(currentPosition, position, "driving"), "driving");
                                updatePassenger("endPoint", new Position(position.latitude, position.longitude));

                            }
                        } else {
                            Toast.makeText(Home.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Home.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();
                }
                search.clearFocus();
            }
            return false;
        });*/
}