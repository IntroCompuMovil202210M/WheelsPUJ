package com.example.wheels;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;
import java.util.Random;

import Models.Driver;
import Models.User;

public class DriverForm extends AppCompatActivity {
    private ImageButton image;
    private Button button;
    private Uri filepath;
    private Bitmap bitmap;
    private User u;
    private TextInputEditText license;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_form);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        u = (User) getIntent().getExtras().getSerializable("user");
        button = findViewById(R.id.createDriver);
        image = findViewById(R.id.carPhoto);
        license = findViewById(R.id.license);
        image.setOnClickListener(view -> Dexter.withActivity(DriverForm.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Image File"), 1);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }


                }).check());
        button.setOnClickListener(view -> {
            if (filepath != null && !String.valueOf(license.getText()).equals("")) {
                Driver d = new Driver(u.getName(), u.getSurname(), u.getMail(), u.getPassword(), u.getImage(), String.valueOf(license.getText()), filepath.toString());
                CollectionReference dbUsers = db.collection("driver");
                dbUsers.document(d.getMail()).set(d).addOnSuccessListener(documentReference -> {
                    mAuth.createUserWithEmailAndPassword(d.getMail(), d.getPassword()).addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(DriverForm.this,
                                    "Conductor: " + d.getName() + "\nRegistrado correctamente!", Toast.LENGTH_LONG).show();

                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent=new Intent(DriverForm.this,MapDriver.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(DriverForm.this,
                                    "No se pudo registrar el Conductor", Toast.LENGTH_LONG).show();

                        }
                    });

                }).addOnFailureListener(e -> {
                    Toast.makeText(DriverForm.this, "Failed", Toast.LENGTH_SHORT).show();
                });

            } else
                Toast.makeText(DriverForm.this, "Informacion incompleta", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            filepath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                image.setImageBitmap(bitmap);
                uploadtofirebase();

            } catch (Exception ex) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadtofirebase() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("File Uploader");
        dialog.show();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference uploader = storage.getReference("driver/Image1" + new Random().nextInt(150));
        uploader.putFile(filepath)
                .addOnSuccessListener(taskSnapshot -> uploader.getDownloadUrl().addOnSuccessListener(uri -> {
                    dialog.dismiss();
                    filepath = uri;
                    Toast.makeText(DriverForm.this, "Imagen subida con exito", Toast.LENGTH_SHORT).show();

                }))
                .addOnProgressListener(taskSnapshot -> {
                    float percent = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    dialog.setMessage("Uploaded :" + (int) percent + " %");
                });

    }
}