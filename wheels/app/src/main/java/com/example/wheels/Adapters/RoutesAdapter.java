package com.example.wheels.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wheels.R;
import com.squareup.picasso.Picasso;

public class RoutesAdapter extends CursorAdapter {

    private final static int a = 0;
    private final static int b = 1;
    private final static int c = 2;

    public RoutesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.available_routes, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = view.findViewById(R.id.name);
        TextView state = view.findViewById(R.id.state);
        ImageView image = view.findViewById(R.id.image);
        System.out.println("RR:"+cursor.getString(c));
        Picasso.with(context).load(cursor.getString(c)).into(image);
        name.setText(cursor.getString(a).toUpperCase());
        state.setText(cursor.getString(b).toUpperCase());
    }
}
