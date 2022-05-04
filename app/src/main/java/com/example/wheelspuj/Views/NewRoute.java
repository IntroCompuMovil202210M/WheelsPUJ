package com.example.wheelspuj.Views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentContainerView;

import com.example.wheelspuj.R;
import com.example.wheelspuj.models.CustomLocation;
import com.example.wheelspuj.models.Geopoint;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NewRoute extends AppCompatActivity {


    private static final double RADIUS_OF_EARTH_KM = 6378;

    Geocoder mGeocoder;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    MyLocationNewOverlay myLocationOverlay;

    public MapView map;
    public double latitude;
    public double longitude;
    JSONArray locations = new JSONArray();
    boolean locationEnabled;

    public SensorManager sensorManager;
    public Sensor lightSensor;
    public SensorEventListener lightSensorListener;
    public Marker destinyMarker, currentMarker;

    public RoadManager roadManager;
    public Polyline roadOverlay;
    //public LocationCallback locationCallback;
    //public LocationRequest locationRequest;
    public static String textSearch;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public static GeoPoint startPoint;
    public static GeoPoint endPoint;
    public Geocoder geocoder;
    public IMapController mapController;
    Locale localeValue = Locale.forLanguageTag("es-ES");

    public Geopoint auxEnd;
    public Geopoint auxStart;

    FloatingActionsMenu buttonsGroup;
    FloatingActionButton putRoute;
    public com.google.android.material.floatingactionbutton.FloatingActionButton confirmButton;
    public com.google.android.material.floatingactionbutton.FloatingActionButton rejectButton;

    public FragmentContainerView createOption;

    ActivityResultLauncher<String> getLocationPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (!result) {
                        Toast.makeText(NewRoute.this.getApplicationContext(), "Permiso de localizacion denegado", Toast.LENGTH_LONG).show();
                        startPoint = new GeoPoint(4.6269924, -74.0651919);
                    } else {
                        startLocationUpdates();
                    }
                }
            }
    );

    private final ActivityResultLauncher<IntentSenderRequest> getLocationSettings =
            registerForActivityResult(
                    new ActivityResultContracts.StartIntentSenderForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == NewRoute.this.RESULT_OK) {
                                locationEnabled = true;
                                startLocationUpdates();
                            } else {
                                Toast.makeText(NewRoute.this, "Sin acceso a GPS", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getSharedPreferences(this.getString(R.string.app_name), MODE_PRIVATE);
        editor = prefs.edit();

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_new_route);

        locationEnabled = false;

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        map = findViewById(R.id.map_View);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(17);
        startPoint = new GeoPoint(4.6269924, -74.0651919);
        mapController.setCenter(startPoint);

        CompassOverlay compassOverlay = new CompassOverlay(this, map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (map != null) {
                    if (sensorEvent.values[0] < 15) {
                        map.getOverlayManager().getTilesOverlay().setColorFilter(TilesOverlay.INVERT_COLORS);

                    } else
                        map.getOverlayManager().getTilesOverlay().setColorFilter(null);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        //init geocoder
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mGeocoder = new Geocoder(this);

        mLocationRequest = createLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION", "Location update in the callback: " + location);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    startPoint = new GeoPoint(latitude, longitude);
                    writeJSONObject(latitude, longitude);
                }
            }
        };

        getLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        checkLocationSettings();
        GpsMyLocationProvider provider = new GpsMyLocationProvider(this);
        provider.addLocationSource(LocationManager.NETWORK_PROVIDER);
        myLocationOverlay = new MyLocationNewOverlay(provider, map);

        myLocationOverlay.enableFollowLocation();

        //overlay para eventos
        map.getOverlayManager().add(myLocationOverlay);
        map.getOverlays().add(createOverlayEvents());

        mapController.setCenter(startPoint);

        roadManager = new OSRMRoadManager(this, "ANDROID");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        confirmButton = findViewById(R.id.confirmButton);
        rejectButton = findViewById(R.id.rejectButton);
        confirmButton.setVisibility(View.INVISIBLE);
        rejectButton.setVisibility(View.INVISIBLE);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // añadir ruta

                Intent intent = new Intent(NewRoute.this, DriverMainActivity.class);
                startActivity(intent);
            }
        });
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOption.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.INVISIBLE);
                confirmButton.setVisibility(View.INVISIBLE);
            }
        });


        buttonsGroup = findViewById(R.id.floatMenu);

        createOption = findViewById(R.id.creteView);
        createOption.setVisibility(View.INVISIBLE);

        geocoder = new Geocoder(getBaseContext());


        putRoute = findViewById(R.id.enterRoute);

        putRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOption.setVisibility(View.VISIBLE);
                createOption.bringToFront();

                Log.i("es", "Espichando estoy");

            }
        });


    }


    public void drawRoute() {
        ArrayList<GeoPoint> routePoints = new ArrayList<>();
        routePoints.add(startPoint);
        routePoints.add(endPoint);
        Road road = roadManager.getRoad(routePoints);
        if (map != null) {
            if (roadOverlay != null)
                map.getOverlays().remove(roadOverlay);
            roadOverlay = RoadManager.buildRoadOverlay(road);
            roadOverlay.getOutlinePaint().setColor(Color.RED);
            roadOverlay.getOutlinePaint().setStrokeWidth(10);
            map.getOverlays().add(roadOverlay);
        }
    }


    private double showDistanceToast(GeoPoint destinyPoint, GeoPoint startPoint) {
        double latDistance = Math.toRadians(startPoint.getLatitude() - destinyPoint.getLatitude());
        double lngDistance = Math.toRadians(startPoint.getLongitude() - destinyPoint.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(startPoint.getLatitude())) * Math.cos(Math.toRadians(destinyPoint.getLongitude()))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        double distance = Math.round(result * 100.0) / 100.0;

        String message = String.format(localeValue, "La distancia entre los puntos es de : %.2f km", distance);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return distance;
    }

    public void setFragmentVision(boolean value) {
        if (value) {
            createOption.setVisibility(View.VISIBLE);
        } else {
            createOption.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
        IMapController mapController = map.getController();
        mapController.setZoom(18.0);
        mapController.setCenter(this.startPoint);
        myLocationOverlay.enableMyLocation();
        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        map.onPause();
        myLocationOverlay.disableMyLocation();
        sensorManager.unregisterListener(lightSensorListener);
    }

    private void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new
                LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                locationEnabled = true;
                startLocationUpdates();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (((ApiException) e).getStatusCode() == CommonStatusCodes.RESOLUTION_REQUIRED) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    IntentSenderRequest isr = new IntentSenderRequest.Builder(resolvable.getResolution()).build();
                    getLocationSettings.launch(isr);
                } else {
                    Log.i("MAP", "GPS is OFF");
                }
            }
        });
    }

    private void writeJSONObject(double latitude, double longitude) {
        CustomLocation currentLocation = new CustomLocation();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        currentLocation.setLongitude(longitude);
        currentLocation.setLatitude(latitude);
        currentLocation.setDate(formatter.format(date));

        Log.i("CUSTOM_LOCATION", String.format("%s", currentLocation));
        locations.put(currentLocation.toJSON());
        Writer output;
        try {
            File file = new File(getBaseContext().getExternalFilesDir(null), "locations.json");
            output = new BufferedWriter(new FileWriter(file));
            output.write(locations.toString());
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LocationRequest createLocationRequest() {
        return LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(10000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private MapEventsOverlay createOverlayEvents() {
        return new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                Log.i("MAP", "marcador?");
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return true;
            }
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationEnabled) {
                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            } else
                startPoint = new GeoPoint(4.6269924, -74.0651919);
        } else
            startPoint = new GeoPoint(4.6269924, -74.0651919);
    }

    private Marker createMarker(GeoPoint p, String title, String desc, int iconID) {
        Marker marker = null;
        if (map != null) {
            marker = new Marker(map);
            if (title != null) marker.setTitle(title);
            if (desc != null) marker.setSubDescription(desc);
            if (iconID != 0) {
                Drawable myIcon = getResources().getDrawable(iconID, this.getTheme());
                marker.setIcon(myIcon);
            }
            marker.setPosition(p);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        }
        return marker;
    }

}
