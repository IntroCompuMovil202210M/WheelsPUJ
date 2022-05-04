package com.example.wheelspuj.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.wheelspuj.R;
import com.example.wheelspuj.Views.DriverMainActivity;
import com.example.wheelspuj.Views.NewRoute;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateRoute#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateRoute extends Fragment {

    public NewRoute mainActivity = (NewRoute) getActivity();
    public Geocoder geocoder;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateRoute() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment create_route.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateRoute newInstance(String param1, String param2) {
        CreateRoute fragment = new CreateRoute();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private Button createButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_create_route, container, false);
        mainActivity = (NewRoute) getActivity();


        geocoder = new Geocoder(getActivity());
        CheckBox startPuj = root.findViewById(R.id.startPointU);
        CheckBox endPuj = root.findViewById(R.id.endPointU);
        EditText startLocation = root.findViewById(R.id.startPoint_text);
        EditText endLocation = root.findViewById(R.id.endPointText);

        startPuj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocation.setText("");
                endPuj.setChecked(false);
            }
        });

        endPuj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPuj.setChecked(false);
                endLocation.setText("");
            }
        });
        startLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPuj.setChecked(false);
                endLocation.setText("");
                endPuj.setChecked(true);
            }
        });


        endLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endPuj.setChecked(false);
                startLocation.setText("");
                startPuj.setChecked(true);
            }
        });


        createButton = root.findViewById(R.id.acceptRoute);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Marker startmarker, endMarker;

                if (startLocation.getText().toString().isEmpty() && !startPuj.isChecked()) {
                    Toast.makeText(root.getContext(), "Seleccione un punto de salida", Toast.LENGTH_LONG).show();
                } else if (endLocation.getText().toString().isEmpty() && !endPuj.isChecked()) {
                    Toast.makeText(root.getContext(), "Seleccione un punto de destino", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getActivity(), DriverMainActivity.class);

                    if (startPuj.isChecked()) {
                        intent.putExtra("StartDirection", false);

                        NewRoute.startPoint = new GeoPoint(4.628586718652143, -74.06467363060628);
                        mainActivity.auxStart = new com.example.wheelspuj.models.Geopoint(4.628586718652143, -74.06467363060628);
                        NewRoute.textSearch = endLocation.getText().toString();
                        startmarker = new Marker(mainActivity.map);
                        startmarker.setIcon(ResourcesCompat.getDrawable(root.getResources(), R.drawable.ic_baseline_person_pin_circle_24, root.getContext().getTheme()));
                        startmarker.setPosition(NewRoute.startPoint);
                        startmarker.setTitle("Punto de salida");
                        startmarker.setSubDescription("Universidad Javeriana");
                        mainActivity.mapController.setCenter(NewRoute.startPoint);
                        mainActivity.mapController.setZoom(16.0);
                        mainActivity.map.getOverlays().add(startmarker);

                        String addressString = endLocation.getText().toString();
                        try {
                            List<Address> addresses = geocoder.getFromLocationName(addressString, 2);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address addressResult = addresses.get(0);
                                mainActivity.auxEnd = new com.example.wheelspuj.models.Geopoint(addressResult.getLatitude(), addressResult.getLongitude());
                                NewRoute.endPoint = new GeoPoint(addressResult.getLatitude(), addressResult.getLongitude());
                                if (mainActivity.map != null) {
                                    String title = addressResult.getAddressLine(0);
                                    endMarker = new Marker(mainActivity.map);
                                    endMarker.setPosition(NewRoute.endPoint);
                                    endMarker.setTitle("Punto de llegada");
                                    endMarker.setSubDescription(title);
                                    mainActivity.map.getOverlays().add(endMarker);
                                    mainActivity.drawRoute();
                                }
                            } else {
                                Toast.makeText(root.getContext(), "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        intent.putExtra("BusquedaDestino", endLocation.getText().toString());

                    } else {
                        intent.putExtra("StartDirection", true);
                        intent.putExtra("BusquedaSalida", startLocation.getText().toString());
                        intent.putExtra("LatitudFinal", 4.628586718652143);
                        intent.putExtra("LongitudFinal", -74.06467363060628);

                        String addressString = startLocation.getText().toString();
                        try {
                            List<Address> addresses = geocoder.getFromLocationName(addressString, 2);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address addressResult = addresses.get(0);
                                mainActivity.auxStart = new com.example.wheelspuj.models.Geopoint(addressResult.getLatitude(), addressResult.getLongitude());
                                NewRoute.startPoint = new GeoPoint(addressResult.getLatitude(), addressResult.getLongitude());
                                if (mainActivity.map != null) {
                                    String title = addressResult.getAddressLine(0);
                                    startmarker = new Marker(mainActivity.map);
                                    startmarker.setPosition(NewRoute.startPoint);
                                    startmarker.setIcon(ResourcesCompat.getDrawable(root.getResources(), R.drawable.ic_baseline_person_pin_circle_24, root.getContext().getTheme()));
                                    startmarker.setTitle("Punto de salida");
                                    startmarker.setSubDescription(title);
                                    mainActivity.map.getOverlays().add(startmarker);
                                }
                            } else {
                                Toast.makeText(root.getContext(), "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        NewRoute.endPoint = new GeoPoint(4.628586718652143, -74.06467363060628);
                        mainActivity.auxEnd = new com.example.wheelspuj.models.Geopoint(4.628586718652143, -74.06467363060628);
                        NewRoute.textSearch = startLocation.getText().toString();
                        endMarker = new Marker(mainActivity.map);
                        endMarker.setPosition(NewRoute.endPoint);
                        endMarker.setTitle("Punto de llegada");
                        endMarker.setSubDescription("Universidad Javeriana");
                        mainActivity.mapController.setCenter(NewRoute.startPoint);
                        mainActivity.mapController.setZoom(16.0);
                        mainActivity.map.getOverlays().add(endMarker);
                        mainActivity.drawRoute();
                    }

                    mainActivity.setFragmentVision(false);
                    mainActivity.confirmButton.setVisibility(View.VISIBLE);
                    mainActivity.rejectButton.setVisibility(View.VISIBLE);


                }


                Toast.makeText(root.getContext(), "Vamonos", Toast.LENGTH_LONG).show();
            }


        });

        return root;
    }

    public void drawRoute(GeoPoint sStartPoint, GeoPoint destinyPoint) {

        mainActivity.roadManager = new OSRMRoadManager(getContext(), "ANDROID");
        ArrayList<GeoPoint> routePoints = new ArrayList<>();
        routePoints.add(sStartPoint);
        routePoints.add(destinyPoint);
        Road road = mainActivity.roadManager.getRoad(routePoints);
        if (mainActivity.map != null) {
            if (mainActivity.roadOverlay != null)
                mainActivity.map.getOverlays().remove(mainActivity.roadOverlay);
            mainActivity.roadOverlay = RoadManager.buildRoadOverlay(road);
            mainActivity.roadOverlay.getOutlinePaint().setColor(Color.RED);
            mainActivity.roadOverlay.getOutlinePaint().setStrokeWidth(10);
            mainActivity.map.getOverlays().add(mainActivity.roadOverlay);
        }
    }


}






