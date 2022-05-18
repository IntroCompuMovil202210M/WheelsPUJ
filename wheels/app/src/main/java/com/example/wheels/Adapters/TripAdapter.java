package com.example.wheels.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.example.wheels.R;

import java.util.ArrayList;
import java.util.Locale;

import Models.Trip;

public class TripAdapter extends CursorAdapter {

    private final static int a = 0;
    private final static int b= 1;
    private final static int c = 2;
    public TripAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.trip_driver, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView index = view.findViewById(R.id.index);
        TextView name =view.findViewById(R.id.name);
        TextView state =view.findViewById(R.id.state);
        index.setText(cursor.getString(a).toUpperCase());
        name.setText(cursor.getString(b).toUpperCase());
        state.setText(cursor.getString(c).toUpperCase());
    }
}
