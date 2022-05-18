package com.example.wheels;

import androidx.annotation.NonNull;
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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import Models.Passenger;
import Models.User;

public class Register extends AppCompatActivity {
    private ImageButton image;
    private TextInputEditText name, surname, mail, pass, rePass;
    private CheckBox checkBox1, checkBox2;
    private Button createAccount;
    //--------------------------
    private Uri filepath;
    private Bitmap bitmap;
    //-------------------------
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        //-------------------------------------
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        mail = findViewById(R.id.mail);
        pass = findViewById(R.id.password);
        rePass = findViewById(R.id.re_password);
        image = findViewById(R.id.addImage);
        createAccount = findViewById(R.id.createAccount);
        checkBox1 = findViewById(R.id.check1);
        checkBox2 = findViewById(R.id.check2);
        //...............................................
        image.setOnClickListener(view -> Dexter.withActivity(Register.this)
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
        //...............................................
        checkBox1.setOnClickListener(view -> {
            checkBox1.setChecked(true);
            checkBox2.setChecked(false);
        });
        checkBox2.setOnClickListener(view -> {
            checkBox2.setChecked(true);
            checkBox1.setChecked(false);
        });
        //------------------------------------------------
        createAccount.setOnClickListener(view -> {
            if (validateForm()) {
                Passenger p = new Passenger(String.valueOf(name.getText()), String.valueOf(surname.getText()), String.valueOf(mail.getText()), String.valueOf(pass.getText()), String.valueOf(filepath));
                if (checkBox1.isChecked()) {
                    CollectionReference dbUsers = db.collection("passenger");
                    mAuth.createUserWithEmailAndPassword(p.getMail(), p.getPassword()).addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this,
                                    "Usuario: " + p.getName() + "\nRegistrado correctamente!", Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            dbUsers.document(p.getMail()).set(p).addOnSuccessListener(unused -> {
                                updateUI(user);
                            }).addOnFailureListener(e -> {
                                Toast.makeText(Register.this,
                                        "No se pudo guardar en la coleccion", Toast.LENGTH_LONG).show();
                            });

                        } else {
                            System.out.println("ERROR:" + task.getException()
                            );
                            Toast.makeText(Register.this,
                                    "No se pudo registrar el usuario", Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    });

                } else {
                    Intent intent = new Intent(Register.this, DriverForm.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", new User(String.valueOf(name.getText()), String.valueOf(surname.getText()), String.valueOf(mail.getText()), String.valueOf(pass.getText()), String.valueOf(filepath)));
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            }

        });


    }

    private boolean validateForm() {
        if (String.valueOf(mail.getText()).equals("")) {
            Toast.makeText(Register.this,
                    "Falta ingresar su correo electronico", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (String.valueOf(pass.getText()).equals("")) {
            Toast.makeText(Register.this,
                    "Falta ingresar su contraseña", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!String.valueOf(pass.getText()).equals(String.valueOf(rePass.getText()))) {
            Toast.makeText(Register.this,
                    "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (filepath == null) {
            Toast.makeText(Register.this,
                    "Seleccione una imagen", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(getBaseContext(), Home.class);
            intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
        } else {
            mail.setText("");
            pass.setText("");
            rePass.setText("");
        }
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
        final StorageReference uploader = storage.getReference("passenger/Image1" + new Random().nextInt(150));
        uploader.putFile(filepath)
                .addOnSuccessListener(taskSnapshot -> uploader.getDownloadUrl().addOnSuccessListener(uri -> {
                    dialog.dismiss();
                    filepath = uri;
                    Toast.makeText(Register.this, "Imagen subida con exito", Toast.LENGTH_SHORT).show();

                }))
                .addOnProgressListener(taskSnapshot -> {
                    float percent = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    dialog.setMessage("Uploaded :" + (int) percent + " %");
                });

    }


}