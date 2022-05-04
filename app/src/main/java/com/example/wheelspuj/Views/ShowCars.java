package com.example.wheelspuj.Views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wheelspuj.R;
import com.example.wheelspuj.models.Car;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ShowCars extends AppCompatActivity {

    ListView list;

    DatabaseReference mDatabase;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    String idDriver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_cars);
        list = findViewById(R.id.listView);

        mDatabase = FirebaseDatabase.getInstance().getReference("/drivers");

        prefs = getSharedPreferences(this.getString(R.string.app_name), MODE_PRIVATE);
        editor = prefs.edit();

        loadUser();

        idDriver = prefs.getString("idDriver", "");

    }

    private void loadUser() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Car> cars = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Car car = dataSnapshot.getValue(Car.class);
                    if (car.getIdDriver().equals(idDriver)) {
                        cars.add(car);
                    }
                }
                ArrayAdapter<Car> adapter = new ArrayAdapter<>(ShowCars.this, android.R.layout.simple_list_item_1, cars);
                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
