package com.example.wheels;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.wheels.databinding.ActivityMapDriverBinding;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import Models.Driver;
import Models.Passenger;
import Models.Position;
import Models.Route;
import Models.Trip;
import Models.TripRequest;
public class MapDriver extends FragmentActivity implements OnMapReadyCallback, LocationListener, TaskLoadedCallback {

    private FusedLocationProviderClient fusedLocationClient;
    private final int MIN_TIME = 1000;
    private final int MIN_DISTANCE = 1;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private GoogleMap mMap;
    private ActivityMapDriverBinding binding;
    private LocationManager manager;
    private LatLng currentPosition;
    private Position position;
    private TripRequest tripRequest;
    private Passenger passenger;
    private EditText search;
    private Driver d;
    private Route activeRoute;
    private Button inTrip, finished;
    private Marker mymarker, searchMarker, passengerMarker, pickUpMarker;
    private NotificationManagerCompat notificationManager;
    public static final String CHANNEL_ID = "Wheels PUJ";
    List<Route> routes;
    private boolean visible = true;
    private FloatingActionButton singOut, visibility, mytrips, chat;
    private Polyline currentPolyline;
    private Geocoder mGeocoder;

    @Override
    public void onBackPressed() {
        search.clearFocus();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapDriverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mGeocoder = new Geocoder(getBaseContext());
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        visibility = findViewById(R.id.visibility);
        chat=findViewById(R.id.chat);
        db = FirebaseFirestore.getInstance();
        singOut = findViewById(R.id.singout);
        search = findViewById(R.id.search);
        mytrips = findViewById(R.id.myTrip);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            try {
                saveLocation(location);
                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                getLocationUpdates();
                drawRouteListener();
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }catch(Exception e){

            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "No se pudo obtener la ultima posicion", Toast.LENGTH_SHORT).show();
        });
        singOut.setOnClickListener(view -> {
            auth.signOut();
            Intent intent = new Intent(MapDriver.this, MainActivity.class);
            startActivity(intent);
        });
        visibility.setOnClickListener(view -> {
            if (visible) {
                updateState(false);
                visible = false;
                visibility.setImageResource(R.drawable.ic_baseline_visibility_off_24);
            } else {
                updateState(true);

                visible = true;
                visibility.setImageResource(R.drawable.ic_baseline_visibility_24);

            }
        });
        chat.setOnClickListener(view -> {
            Intent intent = new Intent(MapDriver.this, ViewUsersActivity.class);
            intent.putExtra("driver", true);
            startActivity(intent);
        });

        search.setOnEditorActionListener((v, actionId, event) -> {
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
                                if (visible) {
                                    new AlertDialog.Builder(MapDriver.this).setTitle("Desea añadir esta ruta").setPositiveButton("si", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            CollectionReference dbDriver = db.collection("driver");
                                            dbDriver.document(currentUser.getEmail()).get().addOnCompleteListener(
                                                    task -> {
                                                        if (task.isSuccessful()) {
                                                            d = task.getResult().toObject(Driver.class);
                                                            routes = d.getRoutes();
                                                            routes.add(new Route(new Position(position.latitude, position.longitude), text));
                                                            if (!dbDriver.document(currentUser.getEmail()).update("routes", routes).isSuccessful()) {
                                                                Toast.makeText(MapDriver.this, "Hubo un error al agregar la ruta", Toast.LENGTH_SHORT);
                                                            }
                                                        }
                                                    }
                                            );

                                        }
                                    }).setNegativeButton("no", (dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                        new FetchURL(MapDriver.this).execute(getUrl(currentPosition, position, "driving"), "driving");
                                    }).create().show();
                                } else {
                                    new FetchURL(MapDriver.this).execute(getUrl(currentPosition, position, "driving"), "driving");

                                }
                                //

                            }
                        } else {
                            Toast.makeText(MapDriver.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MapDriver.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();
                }
                search.clearFocus();
            }
            return false;
        });
        mytrips.setOnClickListener(view -> {
            Intent intent = new Intent(MapDriver.this, DriverRoutes.class);
            startActivity(intent);
        });


    }

    public void drawRouteListener() {
        CollectionReference dbDriver = db.collection("driver");
        dbDriver.document(currentUser.getEmail()).addSnapshotListener((value, error) -> {
                    boolean flag = true;
                    d = value.toObject(Driver.class);
                    if (!d.getRoutes().isEmpty()) {
                        for (Route r : d.getRoutes()) {
                            if (r.isAvailable()) {
                                if (searchMarker != null)
                                    searchMarker.remove();
                                searchMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(r.getEndPoint().getLatitude(), r.getEndPoint().getLongitude())));
                                new FetchURL(MapDriver.this).execute(getUrl(currentPosition, new LatLng(r.getEndPoint().getLatitude(), r.getEndPoint().getLongitude()), "driving"), "driving");
                                return;
                            }
                            flag = false;
                        }

                    } else {
                        if (searchMarker != null)
                            searchMarker.remove();
                        if (currentPolyline != null)
                            currentPolyline.remove();
                    }
                    if (!flag) {
                        if (searchMarker != null)
                            searchMarker.remove();
                        if (currentPolyline != null)
                            currentPolyline.remove();
                    }
                }
        );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mymarker == null) {
            mymarker = mMap.addMarker(new MarkerOptions().position(currentPosition).title("Ubicación actual"));
        } else {
            mymarker.setPosition(currentPosition);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentPosition)
                .zoom(17)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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

    private void saveLocation(Location location) {
        CollectionReference dbUsers = db.collection("driver");
        try {
            dbUsers.document(currentUser.getEmail()).update("position", new Position(location.getLatitude(), location.getLongitude())).isSuccessful();
        }catch(Exception e){

        }
    }

    private void updateState(boolean b) {
        CollectionReference dbUsers = db.collection("driver");
        dbUsers.document(currentUser.getEmail()).update("available", b).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(unused -> Toast.makeText(getApplicationContext(), "Error al actualizar localizacion", Toast.LENGTH_SHORT).show());

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null) {
            position = new Position(location.getLatitude(), location.getLongitude());
            currentPosition = new LatLng(position.getLatitude(), position.getLongitude());
            saveLocation(location);
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
    private void requestListener() {
        CollectionReference requests = db.collection("tripRequest");
        requests.document(auth.getCurrentUser().getEmail()).addSnapshotListener((value, error) -> {
            tripRequest = value.toObject(TripRequest.class);
            if (tripRequest != null ) {
                if (tripRequest.getAccepted() == 0) {
                    db.collection("passenger").document(tripRequest.getMailPassenger()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            passenger = task.getResult().toObject(Passenger.class);
                            onButtonShowPopupWindowClick(findViewById(android.R.id.content).getRootView());
                        }
                    });
                } else if (tripRequest.getAccepted() == 1) {
                    db.collection("passenger").document(tripRequest.getMailPassenger()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            passenger = task.getResult().toObject(Passenger.class);
                            if (passengerMarker != null)
                                passengerMarker.remove();
                            passengerMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(passenger.getPosition().getLatitude(), passenger.getPosition().getLongitude())).title(passenger.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.persona)));
                        }
                    });
                } else if (tripRequest.getAccepted() == -2) {
                    db.collection("passenger").document(tripRequest.getMailPassenger()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            passenger = task.getResult().toObject(Passenger.class);
                            createNotification(tripRequest, passenger.getName() + " ha cancelado el viaje");
                            inTrip.setVisibility(View.INVISIBLE);
                            chat.setVisibility(View.INVISIBLE);
                        }
                    });
                } else if (tripRequest.getAccepted() == 2) {
                    finished.setVisibility(View.VISIBLE);
                    if (passengerMarker != null)
                        passengerMarker.remove();
                } else if (passengerMarker != null)
                    passengerMarker.remove();
            } else {
                if (passengerMarker != null)
                    passengerMarker.remove();
                if (currentPolyline != null)
                    currentPolyline.remove();
                chat.setVisibility(View.INVISIBLE);
            }
        });
    }
    public void onButtonShowPopupWindowClick(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up, null);
        ImageView popImage = popupView.findViewById(R.id.popImage);
        TextView text = popupView.findViewById(R.id.passenger);
        Button cancel = popupView.findViewById(R.id.cancel);
        Button accept = popupView.findViewById(R.id.accept);
        text.setText(passenger.getName() + " te ha enviado una solicitud para unirse a tu ruta,deseas aceptarla?");
        Picasso.with(MapDriver.this).load(passenger.getImage()).into(popImage);
        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        cancel.setOnClickListener(view1 -> {
            inTrip.setVisibility(View.INVISIBLE);
            chat.setVisibility(View.INVISIBLE);
            db.collection("passenger").document(passenger.getMail()).update("inTrip", false);
            db.collection("tripRequest").document(currentUser.getEmail()).update("accepted", -1);
            popupWindow.dismiss();
        });
        accept.setOnClickListener(view12 -> {
            chat.setVisibility(View.VISIBLE);
            inTrip.setVisibility(View.VISIBLE);
            if (pickUpMarker != null)
                pickUpMarker.remove();
            pickUpMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(tripRequest.getPickPoint().getLatitude(), tripRequest.getPickPoint().getLongitude())));
            if (passengerMarker != null)
                passengerMarker.remove();
            passengerMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(passenger.getPosition().getLatitude(), passenger.getPosition().getLongitude())).title(passenger.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.persona)));
            db.collection("passenger").document(passenger.getMail()).update("inTrip", true);
            db.collection("tripRequest").document(currentUser.getEmail()).update("accepted", 1);
            popupWindow.dismiss();
        });
    }
    private void passengersListener() {
        db.collection("tripRequest").document(auth.getCurrentUser().getEmail()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                tripRequest = task.getResult().toObject(TripRequest.class);
                if (tripRequest != null) {
                    if (tripRequest.getAccepted() == 1) {
                        inTrip.setVisibility(View.VISIBLE);
                        db.collection("passenger").whereEqualTo("mail", tripRequest.getMailPassenger()).addSnapshotListener((value, error) -> {
                            passenger = value.toObjects(Passenger.class).get(0);
                            if (passengerMarker != null)
                                passengerMarker.remove();
                            passengerMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(passenger.getPosition().getLatitude(), passenger.getPosition().getLongitude())).title(passenger.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.persona)));
                            if (pickUpMarker != null)
                                pickUpMarker.remove();
                            pickUpMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(tripRequest.getPickPoint().getLatitude(), tripRequest.getPickPoint().getLatitude())));
                        });
                    } else if (tripRequest.getAccepted() == -2) {
                        if (pickUpMarker != null)
                            pickUpMarker.remove();
                        if (passengerMarker != null)
                            passengerMarker.remove();
                    }
                }
            }
        });
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
        builder.setContentTitle(msm);
        builder.setGroup(CHANNEL_ID);
        builder.setColor(Color.RED);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setVibrate(new long[]{1000, 1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setAutoCancel(true);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(t.getId(), builder.build());
    }
}