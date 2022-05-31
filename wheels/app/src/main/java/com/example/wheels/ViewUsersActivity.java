package com.example.wheels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.wheels.Adapters.ChatContactsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

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
    String mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        firebaseFirestore=FirebaseFirestore.getInstance();
        listU=findViewById(R.id.ListUsers);
        //Filling elements
        mail=FirebaseAuth.getInstance().getCurrentUser().getEmail();
        users=new ArrayList<>();
        if (!getIntent().getBooleanExtra("driver", false)) {
            //start
            readStartUsers();
            //subscribe
            subscribe();
        }
        else{
            searchMessages();
            subscribeAsDriver();
        }
    }

    private void subscribeAsDriver() {
        firebaseFirestore.collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    for (DocumentSnapshot ds : value.getDocuments()) {
                        String email = (String) (ds.get("remitenteMail"));
                        String email2 = (String) (ds.get("receptorMail"));
                        Log.i("Aqui", "Email " + mail + " -> " + email2 + " " + email2.equals(mail));
                        if (email2.equals(mail)) {
                            firebaseFirestore.collection("passenger").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (DocumentSnapshot ds : task.getResult().getDocuments()) {
                                        String passenger_email = (String) (ds.get("mail"));
                                        if (email.equals(passenger_email)) {
                                            String name = (String) (ds.get("name"));
                                            String surname = (String) (ds.get("surname"));
                                            String image = (String) (ds.get("image"));
                                            User u = new User();
                                            u.setMail(passenger_email);
                                            u.setName(name);
                                            u.setSurname(surname);
                                            u.setImage(image);
                                            if (!users.contains(u))
                                                addUser(u);
                                        }
                                    }
                                }
                            });
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error reading users", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void searchMessages() {
        ArrayList<String> userEmails = new ArrayList<>();
        firebaseFirestore.collection("messages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot ds : task.getResult().getDocuments()) {
                    String email = (String) (ds.get("remitenteMail"));
                    String email2 = (String) (ds.get("receptorMail"));
                    Log.i("Aqui", "Email " + mail + " -> " + email2 + " " + email2.equals(mail));
                    if (email2.equals(mail)) {
                        if (!userEmails.contains(email))
                            userEmails.add(email);
                    }
                }
            }
        });
        firebaseFirestore.collection("passenger").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot ds : task.getResult().getDocuments()) {
                    String passenger_email = (String) (ds.get("mail"));
                    Log.i("Aqui", "Aniado  -> " + passenger_email + " " + userEmails.contains(passenger_email));
                    if (userEmails.contains(passenger_email)) {
                        String name = (String) (ds.get("name"));
                        String surname = (String) (ds.get("surname"));
                        String image = (String) (ds.get("image"));
                        User u = new User();
                        u.setMail(passenger_email);
                        u.setName(name);
                        u.setSurname(surname);
                        u.setImage(image);
                        if (!users.contains(u))
                            addUser(u);
                    }
                }
            }
        });
    }

    private void addUser(User u) {
        User user=u;
        users.add(user);
        adapter=new ChatContactsAdapter(this, users);
        listU.setAdapter(adapter);
    }

    private void subscribe(){
        //Only Drivers
        firebaseFirestore.collection("driver").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error==null) {
                    for (DocumentSnapshot ds:value.getDocuments()){
                        String name=(String)(ds.get("name"));
                        String surname=(String)(ds.get("surname"));
                        String email=(String)(ds.get("mail"));
                        String image=(String)(ds.get("image"));
                        User u=new User();
                        u.setMail(email);
                        u.setName(name);
                        u.setSurname(surname);
                        u.setImage(image);
                        if (!users.contains(u))
                        addUser(u);
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Error reading users", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void readStartUsers() {
        //Only Drivers
        firebaseFirestore.collection("driver").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot ds:task.getResult().getDocuments()){
                        String name=(String)(ds.get("name"));
                        String surname=(String)(ds.get("surname"));
                        String email=(String)(ds.get("mail"));
                        String image=(String)(ds.get("image"));
                        User u=new User();
                        u.setMail(email);
                        u.setImage(image);
                        u.setName(name);
                        u.setSurname(surname);
                        if (!users.contains(u))
                        addUser(u);
                    }
            }
        });
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