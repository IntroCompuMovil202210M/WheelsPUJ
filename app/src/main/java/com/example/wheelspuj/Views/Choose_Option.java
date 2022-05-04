package com.example.wheelspuj.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wheelspuj.R;

public class Choose_Option extends AppCompatActivity {

    Button passenger,driver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooseoption);

        passenger = findViewById(R.id.passengerButton);
        driver = findViewById(R.id.driverButton);
         driver.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(Choose_Option.this, RegisterDriverActivity.class);
                 startActivity(intent);
             }
         });

         passenger.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(Choose_Option.this, RegisterPassengerActivity.class);
                 startActivity(intent);
             }
         });

    }
}
