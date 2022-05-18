package com.example.wheels.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wheels.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Models.Message;


public class ChatMessagesAdapter extends BaseAdapter {
    Context mContext;
    List<Message> mListUsers;

    public ChatMessagesAdapter(Context ctx, List<Message> contacts){
        this.mContext=ctx;
        this.mListUsers=contacts;
    }

    @Override
    public int getCount() {
        return mListUsers.size();
    }

    @Override
    public Object getItem(int i) {
        return mListUsers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v=View.inflate(mContext, R.layout.user, null);
        Message u= mListUsers.get(i);
        TextView textview_contact_Name= (TextView) v.findViewById(R.id.MessageName);
        TextView textview_contact_Content= (TextView) v.findViewById(R.id.MessageContent);
        textview_contact_Name.setText(u.getName());
        textview_contact_Content.setText(u.getContent());
        ImageView imageView=v.findViewById(R.id.MessageFace);
        Picasso.with(view.getContext()).load(u.getImageLocation()).error(R.drawable.ic_launcher_foreground).into(imageView);
        return v;
    }
}
