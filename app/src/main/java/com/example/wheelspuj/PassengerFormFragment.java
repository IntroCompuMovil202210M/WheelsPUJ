package com.example.wheelspuj;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PassengerFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PassengerFormFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView nombres;
    TextView apellidos;
    TextView telefono;
    TextView correo;
    TextView contra1;
    TextView contra2;

    public PassengerFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PassengerFormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PassengerFormFragment newInstance(String param1, String param2) {
        PassengerFormFragment fragment = new PassengerFormFragment();
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
        nombres=getActivity().findViewById(R.id.nombres);
        apellidos=getActivity().findViewById(R.id.apellidos);
        telefono=getActivity().findViewById(R.id.telefono);
        correo=getActivity().findViewById(R.id.correo);
        contra1=getActivity().findViewById(R.id.contrasenia1);
        contra2=getActivity().findViewById(R.id.contrasenia2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_passenger_form, container, false);
    }
}