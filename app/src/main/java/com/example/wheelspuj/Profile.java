package com.example.wheelspuj;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Profile extends Fragment {

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
        telefono=getActivity().findViewById(R.id.txtPhone);
        mail=getActivity().findViewById(R.id.txtEmail);
        mail.setText(this.username);
        telefono.setText(this.phone);
    }

    public Profile(String u, String phone){
        this.username=u;
        this.phone=phone;
    }
}