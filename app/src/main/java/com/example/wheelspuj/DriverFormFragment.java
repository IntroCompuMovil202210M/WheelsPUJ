package com.example.wheelspuj;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.maciejkozlowski.fragmentutils.FragmentUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DriverFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverFormFragment extends Fragment {

    //For communication with Activity
    private final int CAMERA_PERMISSION_CODE = 1;
    private ReplaceFragmentListener mCallback;

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        try {
            mCallback = FragmentUtils.getListener(this, ReplaceFragmentListener.class);
        } catch (ClassCastException e) {
            throw new ClassCastException(ctx.toString()
                    + " must implement Interface");
        }
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "DriverF";

    // TODO: Rename and change types of parameters
    private String mParam1;
    Button logIn;
    private String mParam2;
    Button attach1, attach2;

    public DriverFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DriverFormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DriverFormFragment newInstance(String param1, String param2) {
        DriverFormFragment fragment = new DriverFormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach");
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_driver_form, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logIn = getActivity().findViewById(R.id.logginButtonD);
        logIn.setOnClickListener(view12 -> {
            //TODO: Verificar arhivos, guardarlos y enviarlos si hace falta
            mCallback.showMainFragment("driver", "true");
        });
        attach1 = getActivity().findViewById(R.id.attachfile);
        attach1.setOnClickListener(view1 -> {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermission(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, "Camera and read storage needed", CAMERA_PERMISSION_CODE);
            } else {
                ImagePicker.with(getActivity())
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();


            }
        });
        attach2 = getActivity().findViewById(R.id.attachfile2);
        attach2.setOnClickListener(view13 -> {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermission(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, "Camera and read storage needed", CAMERA_PERMISSION_CODE);
            } else {
                ImagePicker.with(getActivity())
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();


            }

        });
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