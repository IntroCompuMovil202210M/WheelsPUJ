package com.example.wheelspuj;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Profile extends Fragment {


    private final int CAMERA_PERMISSION_CODE = 1;
    private ImageButton picker;
    ImageView image;
    TextView telefono;
    TextView mail;
    String username;
    String phone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        telefono = getActivity().findViewById(R.id.textView4);
        mail = getActivity().findViewById(R.id.textView2);
        mail.append(this.username);
        telefono.append(this.phone);
        image = getActivity().findViewById(R.id.imageView);
        picker = getActivity().findViewById(R.id.pickImage);
        picker.setOnClickListener(view1 -> {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermission(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, "Camera and read storage needed", CAMERA_PERMISSION_CODE);
            } else {
                ImagePicker.with(this)
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();


            }

        });

    }

    public Profile(String u, String phone) {
        this.username = u;
        this.phone = phone;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            Uri uri = data.getData();
            // Use Uri object instead of File to avoid storage permissions
            image.setImageURI(uri);

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[]
            permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE: {
                if (grantResults.length > 0
                        && (grantResults[0] + grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.with(this)
                            .crop()                    //Crop image(Optional), Check Customization for more option
                            .compress(1024)            //Final image size will be less than 1 MB(Optional)
                            .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                            .start();

                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT);
                    // permission denied, disable functionality that depends on this permission.
                }
                break;
            }

        }

    }
}