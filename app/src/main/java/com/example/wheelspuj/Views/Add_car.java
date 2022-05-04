package com.example.wheelspuj.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wheelspuj.R;
import com.example.wheelspuj.models.Car;
import com.example.wheelspuj.services.FirebaseService;

public class Add_car extends AppCompatActivity {

    EditText idInput, amounInput;
    Button but;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_car_activity);

        prefs = getSharedPreferences(this.getString(R.string.app_name), MODE_PRIVATE);
        editor = prefs.edit();

        amounInput = findViewById(R.id.amountInput);
        idInput = findViewById(R.id.idCInput);
        but = findViewById(R.id.registerButton);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!amounInput.getText().toString().isEmpty() && !idInput.getText().toString().isEmpty()) {
                    String idDriver = prefs.getString("idDriver", "");
                    Car car = new Car(idInput.getText().toString(), idDriver, Integer.parseInt(amounInput.getText().toString()));
                    FirebaseService firebaseService = new FirebaseService();
                    firebaseService.writeNewCar(car);
                    Toast.makeText(Add_car.this, "Carro creado correctamente", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Add_car.this, DriverMainActivity.class));
                }
            }
        });
    }
}
