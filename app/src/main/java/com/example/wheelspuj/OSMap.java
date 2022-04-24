package com.example.wheelspuj;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import org.javatuples.Triplet;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OSMap extends Fragment {

    public OSMap(String username, boolean driver){
        this.username=username;
        this.driver=driver;
        this.locations=new HashMap<>();
    }

    static final String TAG = "OSMapFragment";
    static final float DISTANCE = 25;
    static final String USER_CN = "SmartUser";
    MapView map;
    MyLocationNewOverlay locationOverlay = null;
    //For long press
    //MapEventsOverlay mapEventsOverlay;
    //Key is username, values are location (GeoPoint), time (String), driver(Boolean)
    HashMap<String, Triplet<GeoPoint, String, Boolean>> locations;
    //To save last known location
    GeoPoint lastLocation = null;
    //Who is there
    String username;
    //If it is a driver or not
    boolean driver;
    //Object id
    String id;
    //Parse live query
    ParseLiveQueryClient parseLiveQueryClient=null;
    ParseQuery<ParseObject> parseQuery=null; //suscription to changes

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_osmap, container, false);
        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        parseQuery = ParseQuery.getQuery(USER_CN);
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
            subscribe();
            loadUsers();
            //Location changes
            LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, DISTANCE, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Thread t=new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            GeoPoint p=new GeoPoint(location.getLatitude(), location.getLongitude());
                            if (lastLocation!=null && (p.getLongitude()!=lastLocation.getLongitude() || p.getLatitude()!=lastLocation.getLatitude()))
                                updateLocation(p);
                        }
                    };
                    t.start();
                }
            });
        }
        return view;
    }

    private void loadUsers(){
        String user=this.username;
        try {
                for (ParseObject row : parseQuery.find()) {
                    String username = (String) row.get("username");
                    Boolean driver = (Boolean) row.get("driver");
                    GeoPoint geoPoint = new GeoPoint((double) row.get("latitude"), (double) row.get("longitude"));
                    String date=(String) row.get("time");
                    if (!username.equals(user)) {
                        locations.put(username, new Triplet<>(geoPoint, date, driver));
                    }
                }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void paintLocations(){
        Thread t=new Thread(){
            @Override
            public void run() {
                super.run();
                for (Map.Entry<String, Triplet<GeoPoint, String, Boolean>> mp:locations.entrySet())
                    Log.i(TAG, "Location of "+mp.getKey()+": "+mp.getValue().getValue0());
                for (Map.Entry<String, Triplet<GeoPoint, String, Boolean>> me:locations.entrySet())
                    if (username!=me.getKey())
                        addPointToMap(me.getValue().getValue0(), me.getKey(), me.getValue().getValue1(), me.getValue().getValue2());

        }};
        t.start();
    }

    private void subscribe(){
        SubscriptionHandling<ParseObject> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.DELETE,new SubscriptionHandling.HandleEventCallback<ParseObject>() {
            @Override
            public void onEvent(ParseQuery<ParseObject> query, ParseObject object) {
                if (object.getObjectId()!=id)
                dataDeleted(object);
            }
        });
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<ParseObject>() {
            @Override
            public void onEvent(ParseQuery<ParseObject> query, ParseObject object) {
                if (object.getObjectId()!=id)
                dataUpdated(object);
            }
        });
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<ParseObject>() {
            @Override
            public void onEvent(ParseQuery<ParseObject> query, ParseObject object) {
                if (object.getObjectId()!=id)
                dataCreated(object);
            }
        });
    }

    public void saveData() {
        Log.i(TAG, "Attempt to write on parse");
        final ParseObject firstObject = new ParseObject(USER_CN);
        if (firstObject !=null){
            firstObject.put("username", this.username);
            firstObject.put("latitude", lastLocation.getLatitude());
            firstObject.put("longitude", lastLocation.getLongitude());
            firstObject.put("time", new Date().toString());
            firstObject.put("driver", this.driver);
            try {
                firstObject.save();
            }
            catch(Exception e){
                Log.e(TAG, "Error happened saving object");
            }
            try {
                this.id = parseQuery.whereContains("username", this.username).getFirst().getObjectId();
            }catch(Exception e){
                Log.e(TAG, "Error happened getting object ID");
            }
            Log.i(TAG, "ID is "+this.id);
        }
    }

    private void dataCreated(ParseObject row) {
        String username = (String) row.get("username");
        Boolean driver = (Boolean) row.get("driver");
        GeoPoint geoPoint = new GeoPoint((double) row.get("latitude"), (double) row.get("longitude"));
        String date = (String) row.get("time");
        if (!username.equals(this.username))
        {
            Log.i(TAG,"ADD POINT");
            addPointToMap(geoPoint, username, date, driver);
            this.locations.put(username, new Triplet<>(geoPoint, date, driver));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void dataUpdated(ParseObject row) {
        map.getOverlays().clear();
        map.getOverlays().add(0, this.locationOverlay);
        GeoPoint g=new GeoPoint((double) row.get("latitude"), (double) row.get("longitude"));
        changePositionAndTimeByUsername(g, new Date().toString(), (String) row.get("username"));
        this.paintLocations();
    }

    private void dataDeleted(ParseObject row) {
        map.getOverlays().clear();
        map.getOverlays().add(0, locationOverlay);
        String username = (String) row.get("username");
        this.locations.remove(username);
        this.paintLocations();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void changePositionAndTimeByUsername(GeoPoint g, String time, String username){
        Triplet<GeoPoint, String, Boolean> toChange=this.locations.get(username);
        Triplet<GeoPoint, String, Boolean> copy=this.locations.get(username);
        if (toChange!=null) {
            Log.i(TAG, "Change "+username+" position to "+g);
            toChange=toChange.setAt0(g);
            toChange=toChange.setAt1(time);
            for (Map.Entry<String, Triplet<GeoPoint, String, Boolean>> mp:this.locations.entrySet()){
                Log.i(TAG, "Location of "+mp.getKey()+": "+mp.getValue().getValue0());
            }
            this.locations.replace(username, copy, toChange);
        }
    }

    private void deleteObject(){

        try {
            parseQuery.get(this.id).delete();
            Log.i(TAG, "Object deleted");
        } catch (ParseException parseException) {
            Log.e(TAG, parseException.getMessage());
        }
    }

    private void updateObject(GeoPoint g, String time){
        parseQuery.getInBackground(this.id, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e==null){
                    object.put("time", time);
                    object.put("latitude", g.getLatitude());
                    object.put("longitude", g.getLongitude());
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e==null)
                                Log.i(TAG, "Object updated on parse");
                        }
                    });
                }
            }
        });
    }

    private void addPointToMap(GeoPoint g, String name, String time, boolean driver){
        try{
            Marker m=new Marker(map);
            m.setPosition(g);
            m.setTitle(name + ' '+time);
            m.setIcon((driver)?drawableFromVector(getContext(), R.drawable.ic_baseline_time_to_leave_24):drawableFromVector(getContext(), R.drawable.ic_baseline_emoji_people_24));
            map.getOverlays().add(m);
        }catch (Exception e){
            Toast.makeText(getContext(), "Ha ocurrido un error dibujando la posicion solicitada, intentelo de nuevo por favor", Toast.LENGTH_LONG).show();
        }
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

    private void updateLocation(GeoPoint myLocation) {
            Thread t=new Thread(){
                @Override
                public void run() {
                    super.run();
                    if (lastLocation==null) {
                        lastLocation = myLocation;
                        saveData();
                        loadUsers();
                        paintLocations();
                    }
                    else if (myLocation.getLatitude() != lastLocation.getLatitude() && myLocation.getLongitude()!= lastLocation.getLongitude()) {
                        updateObject(myLocation, new Date().toString());
                    }
                }
            };
            t.start();
    }

    private final Runnable updateGeoPoint=new Runnable() {
        @Override
        public void run() {
            if (locationOverlay!=null)
                updateLocation(locationOverlay.getMyLocation());
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
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop called");
        if (parseQuery != null) {
            parseLiveQueryClient.unsubscribe(parseQuery);
            deleteObject();
        }
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
}