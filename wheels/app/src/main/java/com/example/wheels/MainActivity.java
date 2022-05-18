package com.example.wheels;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    EditText emailEdit, passEdit;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        emailEdit = findViewById(R.id.loginEmail);
        passEdit = findViewById(R.id.loginPass);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            Dexter.withActivity(MainActivity.this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    updateUI(currentUser);
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }


            }).check();

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateUI(FirebaseUser currentUser) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) + ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, "Location neeeded", 1);
        } else {
            if (currentUser != null) {

                CollectionReference dbDriver = db.collection("driver");
                DocumentReference docIdRef = dbDriver.document(mAuth.getCurrentUser().getEmail());
                docIdRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Intent intent;
                            intent = new Intent(MainActivity.this, MapDriver.class);
                            intent.putExtra("user", currentUser.getEmail());
                            startActivity(intent);
                        } else {  Intent intent;
                            intent = new Intent(MainActivity.this, Home.class);
                            intent.putExtra("user", currentUser.getEmail());
                            startActivity(intent);

                        }
                    }
                });



            } else {
                emailEdit.setText("");
                passEdit.setText("");
            }
        }

    }


    private boolean validateForm() {
        boolean valid = true;
        String email = emailEdit.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEdit.setError("Required.");
            valid = false;
        } else {
            emailEdit.setError(null);
        }
        String password = passEdit.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passEdit.setError("Required.");
            valid = false;
        } else {
            passEdit.setError(null);
        }
        return valid;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void signInUser(String email, String password) {
        if (validateForm()) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI
                            FirebaseUser user = mAuth.getCurrentUser();
                            Dexter.withActivity(MainActivity.this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {
                                    updateUI(user);
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                    permissionToken.continuePermissionRequest();
                                }


                            }).check();


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            updateUI(null);
                        }
                    });
        }
    }

    public static boolean isEmailValid(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void login(View view) {
        String email = emailEdit.getText().toString();
        String pass = passEdit.getText().toString();

        if (!isEmailValid(email)) {
            Toast.makeText(MainActivity.this, "Email is not a valid format",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        signInUser(email, pass);
    }

    public void registrarse(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    private void requestPermission(Activity context,
                                   String[] permissions, String explanation, int idCode) {
        boolean flag = false;
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Grant those permissions");
            builder.setMessage(explanation);
            builder.setPositiveButton("ok", (dialogInterface, i) -> ActivityCompat.requestPermissions(context, permissions, idCode));
            builder.setNegativeButton("cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            ActivityCompat.requestPermissions(context, permissions, idCode);

        }
    }
}