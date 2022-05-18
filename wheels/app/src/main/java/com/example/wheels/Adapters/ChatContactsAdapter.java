package com.example.wheels.Adapters;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.wheels.R;

import java.util.List;

import Models.User;

public class ChatContactsAdapter extends BaseAdapter {
    Context mContext;
    List<User> mListUsers;

    public ChatContactsAdapter(Context ctx, List<User> contacts){
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
        User u= mListUsers.get(i);
        TextView textview_contact_Name= (TextView) v.findViewById(R.id.Name);
        TextView textview_contact_LastName= (TextView) v.findViewById(R.id.LastName);
        TextView textview_contact_Username= (TextView) v.findViewById(R.id.Username);
        textview_contact_Name.setText(u.getName());
        textview_contact_LastName.setText(u.getSurname());
        textview_contact_Username.setText(u.getName());
        /*ImageView imageView=v.findViewById(R.id.Face);
        displayImage(u.getImage(), imageView);
        Button button=v.findViewById(R.id.Button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(mContext, FollowActivity.class);
                i.putExtra("user", u.getMail());
                mContext.startActivity(i);
            }
        });*/
        return v;
    }
}
