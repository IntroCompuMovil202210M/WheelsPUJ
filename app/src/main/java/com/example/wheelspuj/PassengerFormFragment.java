package com.example.wheelspuj;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.maciejkozlowski.fragmentutils.FragmentUtils;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PassengerFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PassengerFormFragment extends Fragment {

    //For communication with Activity
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
    private static final String TAG = "PassengerF";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView nombres;
    TextView apellidos;
    TextView telefono;
    TextView correo;
    TextView contra1;
    TextView contra2;
    Button logIn;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_passenger_form, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nombres=getActivity().findViewById(R.id.nombres);
        apellidos=getActivity().findViewById(R.id.apellidos);
        telefono=getActivity().findViewById(R.id.telefono);
        correo=getActivity().findViewById(R.id.correo);
        contra1=getActivity().findViewById(R.id.contrasenia1);
        contra2=getActivity().findViewById(R.id.contrasenia2);
        logIn=getActivity().findViewById(R.id.logginButtonn);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contra1.getText().toString().equals(contra2.getText().toString())){
                    mCallback.showMainFragment("names", nombres.getText().toString());
                    mCallback.showMainFragment("last", apellidos.getText().toString());
                    mCallback.showMainFragment("username", correo.getText().toString());
                    mCallback.showMainFragment("phone", telefono.getText().toString());
                    mCallback.showMainFragment("password", contra1.getText().toString());
                    mCallback.showMainFragment("driver","false");
                }else
                    Toast.makeText(getContext(), "Password is not valid", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach");
        super.onDetach();
    }
}