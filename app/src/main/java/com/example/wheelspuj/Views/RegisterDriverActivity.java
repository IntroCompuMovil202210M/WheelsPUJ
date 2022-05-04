package com.example.wheelspuj.Views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.wheelspuj.DriverHome;
import com.example.wheelspuj.R;
import com.example.wheelspuj.models.Car;
import com.example.wheelspuj.models.Driver;
import com.example.wheelspuj.models.Route;
import com.example.wheelspuj.services.FirebaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


public class RegisterDriverActivity extends AppCompatActivity {

    private static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String CAMERA = Manifest.permission.CAMERA;
    private final String[] permissionsList = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    RadioGroup opciones_rol;
    RadioButton driverOption;
    RadioButton passengerOption;
    TextView textRol;
    Button loginButton;
    boolean firstclick = true;
    boolean secondClick = true;
    static final String USER_CN = "UserCheck";
    static final String TAG = "RegisterActivity";
    private boolean driver = false;
    private String username;
    private String password;
    private String names;
    private String last;
    private String phone;
    private String imageSrc;

    EditText nameInput;
    EditText surnameInput;
    EditText id;
    EditText phoneInput;
    EditText emailInput;
    EditText passwordInput;
    EditText repeatpasswordInput;
    Button reg_button, addLicence, addPhoto;

    FirebaseAuth mAuth;
    Uri uriCamera;

    private final ActivityResultLauncher<String[]> getMultiplePermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    if (!result.containsValue(false))
                        Log.i("PERMISSIONS", "Todos los permisos");
                    else
                        Log.i("PERMISSIONS", "Falta alguno");
                }
            }
    );

    private final ActivityResultLauncher<String> getGalleryPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result)
                        selectGalleryImage();
                    else
                        Toast.makeText(RegisterDriverActivity.this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<String> getCameraPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result)
                        takePhoto();
                    else
                        Toast.makeText(RegisterDriverActivity.this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<String> getGalleryContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    uriToBase64(result);
                }
            }
    );


    private final ActivityResultLauncher<Uri> getCameraContent = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    uriToBase64(uriCamera);
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerdriver);

        mAuth = FirebaseAuth.getInstance();

        getMultiplePermissions.launch(permissionsList);

        nameInput = findViewById(R.id.nameInput);
        surnameInput = findViewById(R.id.surnameInput);
        id = findViewById(R.id.idInput);
        phoneInput = findViewById(R.id.phoneInput);
        emailInput = findViewById(R.id.email_Input);
        passwordInput = findViewById(R.id.password_input1);
        repeatpasswordInput = findViewById(R.id.password_input2);
        //Button
        reg_button = findViewById(R.id.registerButton);
        addLicence = findViewById(R.id.licence_button);
        addPhoto = findViewById(R.id.add_photo);

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((!nameInput.getText().toString().isEmpty() && !surnameInput.getText().toString().isEmpty() && !surnameInput.getText().toString().isEmpty() && !phoneInput.getText().toString().isEmpty() && !emailInput.getText().toString().isEmpty() && !passwordInput.getText().toString().isEmpty() && !repeatpasswordInput.getText().toString().isEmpty())) {
                    if (passwordInput.getText().toString().equals(repeatpasswordInput.getText().toString())) {
                        String email = emailInput.getText().toString();
                        String password = passwordInput.getText().toString();

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(RegisterDriverActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user != null) {
                                                ArrayList<Car> cars = new ArrayList<>();
                                                ArrayList<Route> routes = new ArrayList<>();
                                                Driver driver = new Driver(nameInput.getText().toString(), surnameInput.getText().toString(), id.getText().toString(), phoneInput.getText().toString(), emailInput.getText().toString(), routes, cars);
                                                driver.setImage(imageSrc);
                                                FirebaseService firebaseService = new FirebaseService();
                                                firebaseService.writeNewDriver(driver);
                                                Toast.makeText(RegisterDriverActivity.this, "Conductor creado correctamente", Toast.LENGTH_SHORT).show();
                                                updateUI(user);
                                            }
                                        }
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(RegisterDriverActivity.this, "Ha ocurrido un problema con el registro", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(RegisterDriverActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterDriverActivity.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addLicence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkGalleryPermission();
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermission();
            }
        });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(RegisterDriverActivity.this, DriverMainActivity.class);
            intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
        }
    }

    private void checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            selectGalleryImage();
        else if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
            AlertDialog alert = new AlertDialog.Builder(this)
                    .setMessage("Se necesita acceso a la galeria")
                    .setCancelable(false)
                    .setPositiveButton("Dar permisos",
                            (dialog, which) -> getGalleryPermission.launch(READ_EXTERNAL_STORAGE))
                    .setNegativeButton("Rechazar", (dialog, which) -> dialog.cancel())
                    .create();
            alert.setTitle("Permiso de galería");
            alert.show();
        }
    }

    private void selectGalleryImage() {
        Intent pickImage = new Intent(Intent.ACTION_PICK);
        pickImage.setType("image/*");
        getGalleryContent.launch("image/*");
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_GRANTED)
            takePhoto();
        else if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA)) {
            AlertDialog alert = new AlertDialog.Builder(this)
                    .setMessage("Se necesita acceso a la camara")
                    .setCancelable(false)
                    .setPositiveButton("Dar permisos",
                            (dialog, which) -> getCameraPermission.launch(CAMERA))
                    .setNegativeButton("Rechazar", (dialog, which) -> dialog.cancel())
                    .create();
            alert.setTitle("Permiso de camara");
            alert.show();
        }
    }

    private void takePhoto() {
        File file = new File(getFilesDir(), "picFromCamera");
        uriCamera = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName(), file);
        getCameraContent.launch(uriCamera);
    }

    private void uriToBase64(Uri uri) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            imageSrc = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Picasso.get().load(uri).into(bandera);
    }
}

