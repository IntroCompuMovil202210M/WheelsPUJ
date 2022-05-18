package com.example.wheels;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.wheels.Adapters.ChatContactsAdapter;
import com.example.wheels.Adapters.ChatMessagesAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Models.Message;
import Models.User;

public class SingleChatActivity extends AppCompatActivity {

    ChatMessagesAdapter adapter;
    ListView listU;
    //Elements to paint
    ArrayList<Message> messages;
    //Firebase instance
    FirebaseFirestore firebaseFirestore;
    static final String TAG = "ViewUsers";
    static final String USER_CN = "UserInfo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);
        listU=findViewById(R.id.ListViewMessagesSingle);
        firebaseFirestore=FirebaseFirestore.getInstance();
        messages=new ArrayList<>();
        readStartMessages();
        subscribe();

    }
    private void addMessage(Message u) {
        Message m=u;
        messages.add(m);
        adapter=new ChatMessagesAdapter(this, messages);
        listU.setAdapter(adapter);
    }

    private void subscribe(){
    }

    private void readStartMessages() {
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