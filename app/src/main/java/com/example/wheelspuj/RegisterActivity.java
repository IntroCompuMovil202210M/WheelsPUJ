package com.example.wheelspuj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class RegisterActivity extends AppCompatActivity  {

    PassengerFormFragment passengerFragment;
    DriverFormFragment driverFragment;
    RadioGroup opciones_rol;
    RadioButton driverOption;
    RadioButton passengerOption;
    TextView textRol;
    Button loginButton;
    boolean firstclick = true;
    boolean secondClick = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        passengerFragment = new PassengerFormFragment();
        driverFragment = new DriverFormFragment();


        getSupportFragmentManager().beginTransaction().add(R.id.formContainer,passengerFragment).commit();

        textRol = findViewById(R.id.textViewRol);
        opciones_rol = findViewById(R.id.opciones_rol);
        driverOption = findViewById(R.id.radio_driver);
        passengerOption = findViewById(R.id.radio_passenger);
        loginButton = findViewById(R.id.logginButtonn);
        CharSequence next = (CharSequence)"Next";
        CharSequence LoginUser = (CharSequence) "Login User";

        driverOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.setText(next);
                firstclick = false;
                secondClick = true;

            }

        });

        passengerOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.setText(LoginUser);
                firstclick = false;
                secondClick = false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("firstClick: " + firstclick);
                System.out.println("SecondClick: " + secondClick);
                if(!firstclick && !secondClick){
                    Intent intent = new Intent(RegisterActivity.this, Home.class);
                    startActivity(intent);
                }
                else if(!firstclick && secondClick){
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.formContainer,driverFragment).commit();
                    loginButton.setText(LoginUser);
                    secondClick = false;
                    firstclick = true;
                    textRol.setVisibility(View.INVISIBLE);
                    opciones_rol.setVisibility(View.INVISIBLE);


                }else if(firstclick && !secondClick){
                    System.out.println("finall");
                    Intent intent = new Intent(RegisterActivity.this, DriverHome.class);
                    startActivity(intent);
                }


            }
        });






    }




}
