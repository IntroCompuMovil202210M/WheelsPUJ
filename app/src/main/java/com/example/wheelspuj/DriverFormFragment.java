package com.example.wheelspuj;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.maciejkozlowski.fragmentutils.FragmentUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DriverFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverFormFragment extends Fragment {

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
    private static final String TAG = "DriverF";

    // TODO: Rename and change types of parameters
    private String mParam1;
    Button logIn;
    private String mParam2;

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
        View v= inflater.inflate(R.layout.fragment_driver_form, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logIn=getActivity().findViewById(R.id.logginButtonD);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Verificar arhivos, guardarlos y enviarlos si hace falta
                mCallback.showMainFragment("driver", "true");
            }
        });
    }
}