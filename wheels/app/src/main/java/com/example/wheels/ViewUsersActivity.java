package com.example.wheels;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.wheels.Adapters.ChatContactsAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Models.User;

public class ViewUsersActivity extends AppCompatActivity {

    ListView listU;
    ChatContactsAdapter adapter;
    static final String TAG = "ViewUsers";
    static final String USER_CN = "UserInfo";
    //Elements to paint
    ArrayList<User> users;
    //Firebase instance
    FirebaseFirestore firebaseFirestore;

    //https://www.youtube.com/watch?v=D_q9NJAm5OI
    // https://www.youtube.com/watch?v=DFnxY_PEnYY
    //https://www.youtube.com/watch?v=a9I7Ppzh1_Y

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        firebaseFirestore=FirebaseFirestore.getInstance();
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