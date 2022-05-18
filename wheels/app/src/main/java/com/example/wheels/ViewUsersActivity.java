package com.example.wheels;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.wheels.Adapters.ChatContactsAdapter;

import java.util.ArrayList;

import Models.User;

public class ViewUsersActivity extends AppCompatActivity {

    ListView listU;
    ChatContactsAdapter adapter;
    static final String TAG = "ViewUsers";
    static final String USER_CN = "UserInfo";
    //Elements to paint
    ArrayList<User> users;
    //Should send notifications

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        listU=findViewById(R.id.ListUsers);
        //Filling elements
        users=new ArrayList<>();
        //start
        readStartUsers();
        //subscribe
        subscribe();
    }

    private void addUser(User u) {
        User user=u;
        users.add(user);
        adapter=new ChatContactsAdapter(this, users);
        listU.setAdapter(adapter);
    }

    private void subscribe(){
    }

    private void readStartUsers() {
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop called");
    }

    @Override
    public void onResume(){
        super.onResume();
    }
}