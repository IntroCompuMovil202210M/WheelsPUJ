package com.example.wheelspuj;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayManager;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OSMap extends Fragment {

    public OSMap(String username, boolean driver){
        this.username=username;
        this.driver=driver;
    }

    static final String TAG = "OSMapFragment";
    static final float DISTANCE = 25;
    MapView map;
    MyLocationNewOverlay locationOverlay = null;
    //For long press
    MapEventsOverlay mapEventsOverlay;
    //To save overlays
    ArrayList<Overlay> overlays = new ArrayList<>();
    //To save last known location
    GeoPoint lastLocation = null;
    //Information to save on firestore
    //Who is there
    String username="Hola";
    //If it is a driver or not
    boolean driver=true; // TODO
    //Access firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Reference to Document
    DocumentReference document=null;
    boolean firstF=false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_osmap, container, false);
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map = view.findViewById(R.id.osmMap);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getController().setZoom(18.0);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setUseDataConnection(true);
        map.setMultiTouchControls(true);
        /*CompassOverlay compassOverlay=new CompassOverlay(ctx, map);
        compassOverlay.enableCompass();
        addOverlay(compassOverlay);*/
        if (requestPermissions()) {
            locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);
            locationOverlay.enableMyLocation();
            if (driver)
                locationOverlay.setDirectionArrow(bitmapFromVector(getContext(), R.drawable.ic_baseline_time_to_leave_24), bitmapFromVector(getContext(), R.drawable.ic_baseline_time_to_leave_24));
            else
                locationOverlay.setDirectionArrow(bitmapFromVector(getContext(), R.drawable.ic_baseline_emoji_people_24), bitmapFromVector(getContext(), R.drawable.ic_baseline_emoji_people_24));
            locationOverlay.enableFollowLocation();
            locationOverlay.runOnFirstFix(updateGeoPoint);
            map.getOverlayManager().add(locationOverlay);
            LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Thread t=new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            GeoPoint p=new GeoPoint(location.getLatitude(), location.getLongitude());
                            if (firstF && p!=lastLocation)
                                updateFirestoreLocation(p);
                        }
                    };
                    t.run();
                }
            };
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, DISTANCE, locationListener);
        }
        return view;
    }

    private void addOverlay(Overlay o){
        map.getOverlayManager().add(o);
        overlays.add(o);
    }

    private void addOverlays(){
        OverlayManager om=map.getOverlayManager();
        map.getOverlays().clear();
        om.clear();
        if (locationOverlay!=null)
            om.add(0, locationOverlay);
        for (Overlay o:overlays)
            om.add(o);
    }

    private void addPointToMap(GeoPoint g, String name, String time, boolean driver){
        try{
            Marker m=new Marker(map);
            m.setPosition(new GeoPoint(g.getLatitude(), g.getLongitude()));
            m.setTitle(name + ' '+time);
            m.setIcon((driver)?drawableFromVector(getContext(), R.drawable.ic_baseline_time_to_leave_24):drawableFromVector(getContext(), R.drawable.ic_baseline_emoji_people_24));
            map.getOverlays().add(m);
        }catch (Exception e){
            Toast.makeText(getContext(), "Ha ocurrido un error dibujando la posicion solicitada, intentelo de nuevo por favor", Toast.LENGTH_LONG).show();
        }
    }

    private void paintMarkers(){
        while (document == null);
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        String user=doc.get("username").toString();
                        if (true){
                        // Falta lo del username, para verificar que si deba de pintarlo if (!user.equals(username)) {
                            HashMap<String, Object> wg = (HashMap<String, Object>) doc.get("location");
                            GeoPoint g=new GeoPoint((Double) wg.get("latitude"), (Double) wg.get("longitude"));
                            addPointToMap(g, user, doc.get("time").toString(), doc.getBoolean("driver"));
                        }
                    }
                }
            }
        });
    }

    private Bitmap bitmapFromVector(Context context, int vectorResId) {
        Drawable drawable = ContextCompat.getDrawable(context, vectorResId);
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private Drawable drawableFromVector(Context context, int vectorResId) {
        Drawable drawable = ContextCompat.getDrawable(context, vectorResId);
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return drawable;
    }

    private void updateFirestoreLocation(GeoPoint myLocation) {
        //TODO
        if (lastLocation == null || lastLocation.distanceToAsDouble(myLocation) >= DISTANCE)
            lastLocation = myLocation;
        Log.i(TAG, "Location is " + lastLocation.toString());
        HashMap<String, Object> user = new HashMap<>();
        user.put("driver", driver);
        user.put("username", username);
        user.put("location", lastLocation);
        user.put("time", new Date().toString());
        if (document==null){
            db.collection("users").add(user).addOnSuccessListener(
                    new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            document=documentReference;
                            Log.i(TAG, "Document with ID "+documentReference.getId());
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error "+e);
                }
            });
        }
        else{
            // Document exists, therefore, have to update
            Task<Void> future1, future2;
            do {
                future1 = document.update("location", lastLocation);
                future2=document.update("time", new Date().toString());
                while (!future1.isComplete() && !future2.isComplete());
            } while (!future1.isSuccessful() && !future2.isSuccessful());
            Log.i(TAG, "Updated location for "+username);
        }
        paintStuff();
    }

    private void paintStuff() {
        Thread t1=new Thread(){
            @Override
            public void run() {
                super.run();
                addOverlays();
                paintMarkers();
            }
        };
        t1.run();
    }

    private Runnable updateGeoPoint=new Runnable() {
        @Override
        public void run() {
            if (locationOverlay!=null)
                updateFirestoreLocation(locationOverlay.getMyLocation());
            firstF=true;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean requestPermissions() {
        ActivityResultLauncher<String> getFine = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (!result)
                Log.e(TAG, "Cant access location");
        });
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            getFine.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        return !(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onPause() {
        super.onPause(); 
        Log.i(TAG, "onPause called");
        if (locationOverlay!=null) {
            locationOverlay.disableFollowLocation();
            locationOverlay.disableMyLocation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume called");
        if (locationOverlay!=null) {
            locationOverlay.enableFollowLocation();
            locationOverlay.enableMyLocation();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView called");
        Thread t=new Thread(){
            @Override
            public void run() {
                super.run();
                Task<Void> future;
                do {
                    future = document.delete();
                    while (!future.isComplete()) ;
                } while (!future.isSuccessful());
            }
        };
        t.run();
        lastLocation=null;
        firstF=false;
        document=null;
    }
}