package com.example.wheels;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.wheels.databinding.ActivityHomeBinding;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Models.Driver;
import Models.Passenger;
import Models.Position;
import Models.Route;
import Models.Trip;
import Models.TripRequest;

public class Home extends FragmentActivity implements OnMapReadyCallback, LocationListener, TaskLoadedCallback {
    private FusedLocationProviderClient fusedLocationClient;
    private NotificationManagerCompat notificationManager;
    public static final String CHANNEL_ID = "Wheels PUJ";
    private final int MIN_TIME = 1000;
    private final int MIN_DISTANCE = 1;
    private FirebaseAuth auth;
    private TripRequest currentTrip;
    private FirebaseFirestore db;
    private Passenger me;
    private CollectionReference dbDrivers;
    private boolean camera = true;
    private Polyline currentPolyline, polyPickUp;
    private GoogleMap mMap;
    private Map<String, Route> routes;
    private ActivityHomeBinding binding;
    private List<LatLng> linePoints = new ArrayList<>();
    private Marker mymarker, suggestedPoint;
    private Map<String, Marker> map = new HashMap<>();
    private LocationManager manager;
    private LatLng currentPosition;
    private Position position;
    private Route currentRoute;
    private Button cancelTrip;
    private FloatingActionButton singOut, chat;
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
        notificationManager = NotificationManagerCompat.from(this);
        mGeocoder = new Geocoder(getBaseContext());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        chat = findViewById(R.id.chat);
        chat.setVisibility(View.INVISIBLE);
        singOut = findViewById(R.id.singout);
        routes = new HashMap<>();
        cancelTrip = findViewById(R.id.cancelTrip);
        cancelTrip.setVisibility(View.INVISIBLE);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                updatePassenger("position", new Position(location.getLatitude(), location.getLongitude()));
                getLocationUpdates();
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
                driverListener();
                acceptedListener();
                listenerPassenger();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "No se pudo obtener la ultima posicion", Toast.LENGTH_SHORT).show();
        });
        singOut.setOnClickListener(view -> {
            auth.signOut();
            Intent intent = new Intent(Home.this, MainActivity.class);
            startActivity(intent);
        });
        cancelTrip.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
            builder.setTitle("Cancelar ruta");
            builder.setMessage("Esta seguro que desea cancelar el viaje?");
            builder.setPositiveButton("Si", (dialogInterface, i) -> {
                if (currentTrip != null) {
                    db.collection("tripRequest").document(currentTrip.getMailDriver()).update("accepted", -2);
                    db.collection("passenger").document(auth.getCurrentUser().getEmail()).update("inTrip", false).addOnSuccessListener(unused -> {
                        cancelTrip.setVisibility(View.INVISIBLE);
                        chat.setVisibility(View.INVISIBLE);
                        if (suggestedPoint != null)
                            suggestedPoint.remove();
                        Toast.makeText(Home.this, "Viaje cancelado", Toast.LENGTH_SHORT);

                    });
                }
            });
            builder.setNegativeButton("No", (dialogInterface, i) -> {

                dialogInterface.dismiss();
            });
            builder.create().show();


        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        createNotificationChannel();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(marker -> {
            if (marker.getTag() != null) {
                if (!marker.getTag().equals("Me") && !marker.getTag().equals("Suggested")) {
                    if (me != null && !me.isInTrip()) {
                        LatLng shorter = shorterDistance();
                        if (shorter != null) {
                            if (suggestedPoint != null)
                                suggestedPoint.remove();
                            suggestedPoint = mMap.addMarker(new MarkerOptions().position(shorter).title("Punto sugerido").icon(BitmapDescriptorFactory.fromResource(R.drawable.persona)));
                            suggestedPoint.setTag("Suggested");
                            suggestedPoint.showInfoWindow();
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(Home.this);
                            mBuilder.setTitle("Punto de encuentro");
                            mBuilder.setMessage("Enviar solicitud en el punto sugerido ?");
                            mBuilder.setPositiveButton("Si", (dialogInterface, i) -> {

                                addRequest(new TripRequest(marker.getTitle(), auth.getCurrentUser().getEmail(), (String) marker.getTag(), new Position(shorter.latitude, shorter.longitude)));

                            });
                            mBuilder.setNegativeButton("No", (dialogInterface, i) -> {
                                if (suggestedPoint != null) {
                                    suggestedPoint.remove();
                                    suggestedPoint = null;
                                }

                            });
                            mBuilder.create().show();
                        } else
                            Toast.makeText(Home.this, "Fuera de rango", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        chat.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, SingleChatActivity.class);
            startActivity(intent);
        });
        mMap.setOnMarkerClickListener(marker -> {
            if (marker.getTag() != null) {
                if (!marker.getTag().equals("Me") && !marker.getTag().equals("Suggested")) {
                    currentRoute = routes.get(marker.getTag());
                    linePoints.clear();
                    for (Position p : currentRoute.getPoints())
                        linePoints.add(new LatLng(p.getLatitude(), p.getLongitude()));
                    if (currentPolyline != null)
                        currentPolyline.remove();
                    currentPolyline = mMap.addPolyline(new PolylineOptions().addAll(linePoints));

                }
            }
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
                    .zoom(17)
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

    private void addRequest(TripRequest request) {
        CollectionReference dbRequest = db.collection("tripRequest");
        dbRequest.document(request.getMailDriver()).set(request).addOnCompleteListener(task -> {
            if (!task.isSuccessful())
                Toast.makeText(Home.this, "No se pudo agregar la solicitud", Toast.LENGTH_SHORT);
        });
    }

    private void listenerPassenger() {
        db.collection("passenger").document(auth.getCurrentUser().getEmail()).addSnapshotListener((value, error) -> {
            if (value.exists()) {
                me = value.toObject(Passenger.class);
                if (!me.isInTrip()) {
                    if (suggestedPoint != null)
                        suggestedPoint.remove();
                    mymarker.setTitle("Ubicacion actual");
                    if (polyPickUp != null)
                        polyPickUp.remove();
                } else {
                    if (suggestedPoint != null && suggestedPoint.getPosition() != null) {
                        suggestedPoint.setVisible(false);
                        mymarker.setTitle(distancia(currentPosition.latitude, currentPosition.longitude, suggestedPoint.getPosition().latitude, suggestedPoint.getPosition().longitude) + " del punto de encuentro");
                        mymarker.showInfoWindow();
                        new FetchURL(Home.this).execute(getUrl(currentPosition, suggestedPoint.getPosition(), "walking"), "walking");
                    }

                }
            }

        });
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
    private void driverListener() {
        dbDrivers = db.collection("driver");
        dbDrivers.whereEqualTo("available", true).addSnapshotListener((value, error) -> {
            List<Driver> drivers = value.toObjects(Driver.class);
            boolean flag = false;
            routes.clear();
            if (!drivers.isEmpty()) {
                for (Driver d : drivers) {
                    for (Route r : d.getRoutes()) {
                        if (r.isAvailable()) {
                            flag = true;
                            routes.put(d.getMail(), r);
                            if (!map.containsKey(d.getMail())) {
                                map.put(d.getMail(), mMap.addMarker(new MarkerOptions().position(new LatLng(d.getPosition().getLatitude(), d.getPosition().getLongitude()))));
                            } else {
                                if (map.get(d.getMail()) != null) {
                                    map.get(d.getMail()).setPosition(new LatLng(d.getPosition().getLatitude(), d.getPosition().getLongitude()));
                                } else {
                                    map.put(d.getMail(), mMap.addMarker(new MarkerOptions().position(new LatLng(d.getPosition().getLatitude(), d.getPosition().getLongitude()))));
                                }

                            }
                            map.get(d.getMail()).setTag(d.getMail());
                            map.get(d.getMail()).setTitle(r.getName().toUpperCase());
                            map.get(d.getMail()).showInfoWindow();
                            linePoints.clear();
                            if (currentPolyline != null) {
                                currentPolyline.remove();
                            }
                            if (r.getPoints() != null) {
                                for (Position p : r.getPoints())
                                    linePoints.add(new LatLng(p.getLatitude(), p.getLongitude()));
                                currentPolyline = mMap.addPolyline(new PolylineOptions().addAll(linePoints));

                            }

                        }
                    }
                    if (!flag) {
                        if (map.containsKey(d.getMail())) {
                            if (map.get(d.getMail()) != null)
                                map.get(d.getMail()).remove();
                            map.replace(d.getMail(), null);
                            if (currentPolyline != null)
                                currentPolyline.remove();

                        }
                    }

                }
            } else {
                for (String key : map.keySet()) {
                    if (map.get(key) != null)
                        map.get(key).remove();
                    map.remove(key);
                }
                if (currentPolyline != null)
                    currentPolyline.remove();
            }

        });
    }

    private void acceptedListener() {
        db.collection("tripRequest").whereEqualTo("mailPassenger", auth.getCurrentUser().getEmail()).addSnapshotListener((value, error) -> {
            if (!value.isEmpty()) {

                List<TripRequest> t = value.toObjects(TripRequest.class);
                for (TripRequest tr : t) {
                    if (tr.getAccepted() == 0) {
                        if (suggestedPoint != null)
                            suggestedPoint.remove();
                        suggestedPoint = mMap.addMarker(new MarkerOptions().position(new LatLng(tr.getPickPoint().getLatitude(), tr.getPickPoint().getLongitude())).title("Punto de encuentro").icon(BitmapDescriptorFactory.fromResource(R.drawable.persona)));
                        suggestedPoint.setTag("Suggested");
                    } else if (tr.getAccepted() == 1) {
                        currentTrip = tr;
                        createNotification(tr, " ha sido aceptado");
                        cancelTrip.setVisibility(View.VISIBLE);
                        chat.setVisibility(View.VISIBLE);
                        new FetchURL(Home.this).execute(getUrl(currentPosition, new LatLng(tr.getPickPoint().getLatitude(), tr.getPickPoint().getLongitude()), "walking"), "walking");
                        if (suggestedPoint != null)
                            suggestedPoint.remove();
                    } else if (tr.getAccepted() == -1) {
                        cancelTrip.setVisibility(View.INVISIBLE);
                        createNotification(tr, " ha sido rechazado");
                    } else if (tr.getAccepted() == 2) {
                        chat.setVisibility(View.VISIBLE);
                        cancelTrip.setVisibility(View.INVISIBLE);
                        if (suggestedPoint != null)
                            suggestedPoint.remove();
                        if (mymarker != null)
                            mymarker.remove();
                    } else {
                        cancelTrip.setVisibility(View.INVISIBLE);
                    }

                }
            } else {
                if (currentPolyline != null)
                    currentPolyline.remove();
                if (mymarker == null)
                    mymarker = mMap.addMarker(new MarkerOptions().position(currentPosition).title("Ubicación actual").icon(BitmapDescriptorFactory.fromResource(R.drawable.persona)));
                else
                    mymarker.setPosition(currentPosition);
                if (suggestedPoint != null)
                    suggestedPoint.remove();
                if (polyPickUp != null)
                    polyPickUp.remove();
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

    public double distancia(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) *
                Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) *
                        Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lngDistance / 2) *
                        Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = 6370 * c;
        return Math.round(result * 100.0) / 100.0;
    }

    public LatLng shorterDistance() {
        List<LatLng> sorted = new ArrayList<>();
        if (currentPolyline != null) {
            if (!currentPolyline.getPoints().isEmpty()) {
                for (LatLng l : currentPolyline.getPoints()) {
                    if (distancia(currentPosition.latitude, currentPosition.longitude, l.latitude, l.longitude) < 1.5) {
                        sorted.add(l);
                    }
                }
                Collections.sort(sorted, (latLng, t1) -> {
                    if (distancia(currentPosition.latitude, currentPosition.longitude, latLng.latitude, latLng.longitude) < distancia(currentPosition.latitude, currentPosition.longitude, t1.latitude, t1.longitude))
                        return -1;
                    if (distancia(currentPosition.latitude, currentPosition.longitude, latLng.latitude, latLng.longitude) == distancia(currentPosition.latitude, currentPosition.longitude, t1.latitude, t1.longitude))
                        return 0;
                    return 1;
                });
            }
        }
        if (sorted.isEmpty())
            return null;
        return sorted.get(0);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            //IMPORTANCE_MAX MUESTRA LA NOTIFICACIÓN ANIMADA
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotification(TripRequest t, String msm) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.logo);
        builder.setContentTitle("Tu viaje a " + t.getRoute() + msm);
        builder.setGroup(CHANNEL_ID);
        builder.setColor(Color.BLUE);
        //builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setVibrate(new long[]{1000, 1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setAutoCancel(true);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(t.getId(), builder.build());
    }

    @Override
    public void onTaskDone(Object... values) {
        if (polyPickUp != null)
            polyPickUp.remove();
        polyPickUp = mMap.addPolyline((PolylineOptions) values[0]);

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
}