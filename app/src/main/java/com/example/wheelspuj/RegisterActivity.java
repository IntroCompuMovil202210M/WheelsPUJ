package com.example.wheelspuj;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.javatuples.Pair;

import java.util.Date;


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
    static final String USER_CN = "User";
    static final String TAG = "RegisterActivity";

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
                if (!userExists(passengerFragment.correo.getText().toString())) {
                    Pair<Boolean, String> user = createUser();
                    String username=user.getValue1();
                    if (username!=null) {
                        if (!firstclick && !secondClick) {
                            Intent intent = new Intent(RegisterActivity.this, Home.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                        } else if (!firstclick && secondClick) {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.formContainer, driverFragment).commit();
                            loginButton.setText(LoginUser);
                            secondClick = false;
                            firstclick = true;
                            textRol.setVisibility(View.INVISIBLE);
                            opciones_rol.setVisibility(View.INVISIBLE);


                        } else if (firstclick && !secondClick) {
                            System.out.println("finall");
                            Intent intent = new Intent(RegisterActivity.this, DriverHome.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    boolean userExists(String username){
        try {
            ParseQuery parseQuery = ParseQuery.getQuery(USER_CN);
            for (Object obj : parseQuery.find()) {
                ParseObject row= (ParseObject) obj;
                if (username.equals(row.get("username")))
                    return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    Pair<Boolean, String> createUser(){
        ParseQuery parseQuery = ParseQuery.getQuery(USER_CN);
        Boolean driver=(driverOption.isChecked());
        String username=passengerFragment.correo.getText().toString();
        String password=passengerFragment.contra1.getText().toString();
        if (!passengerFragment.contra2.getText().toString().equals(password))
        username=null;
        if(username!=null) {
            final ParseObject firstObject = new ParseObject(USER_CN);
            if (firstObject != null) {
                firstObject.put("username", username);
                firstObject.put("password", password);
                firstObject.put("nombres", passengerFragment.nombres.getText().toString());
                firstObject.put("apellidos", passengerFragment.apellidos.getText().toString());
                firstObject.put("driver", driver);
                try {
                    firstObject.save();
                } catch (Exception e) {
                    Log.e(TAG, "Error happened saving object");
                }
                try {
                    Log.i(TAG, "ID is " + parseQuery.whereContains("username", username).getFirst().getObjectId());
                } catch (Exception e) {
                    Log.e(TAG, "Error happened getting object ID");
                }
            }
        }
        return new Pair<>(driver, username);
    }
}
