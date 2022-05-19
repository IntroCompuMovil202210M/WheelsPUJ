package com.example.wheels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wheels.Adapters.ChatMessagesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Models.FirebaseMessage;
import Models.Message;
import Models.User;

public class SingleChatActivity extends AppCompatActivity {

    ChatMessagesAdapter adapter;
    ListView listU;
    ImageView imageSelf;
    TextView name;
    Button sendMessage;
    EditText msgEdit;
    String chatEmail;
    String chatImage, chatName;
    String selfImageURL=null;
    String selfName=null;
    //Elements to paint
    ArrayList<Message> messages;
    //Firebase instance
    FirebaseFirestore firebaseFirestore;
    static final String TAG = "ViewUsers";
    static final String USER_CN = "UserInfo";
    String thisUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisUser= FirebaseAuth.getInstance().getCurrentUser().getEmail();
        firebaseFirestore=FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_single_chat);
        name=findViewById(R.id.ContactoNombreTextView);
        imageSelf=findViewById(R.id.ContactImageView);
        listU=findViewById(R.id.ListViewMessagesSingle);
        startImage_Name();
        chatEmail=getIntent().getStringExtra("email");
        chatImage=getIntent().getStringExtra("image");
        chatName=getIntent().getStringExtra("name");
        msgEdit=findViewById(R.id.SelfWriteMessage);
        sendMessage=findViewById(R.id.SelfSendBtn);
        listU.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listU.setStackFromBottom(true);
        messages=new ArrayList<>();
        Picasso.with(getApplicationContext()).load(chatImage).into(imageSelf);
        name.setText(chatName);
        readStartMessages();
        subscribe();
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
                msgEdit.setText("");
                msgEdit.clearFocus();
            }
        });
    }

    private boolean sendMsg(){
        boolean b=true;
        try{
            String msgTxt=msgEdit.getText().toString();
            firebaseFirestore.collection("messages").add(new FirebaseMessage(msgTxt, selfImageURL, thisUser, selfName, chatEmail));
            addMessage(new Message(selfName, selfImageURL, msgTxt));
        }catch (Exception e){
            b=false;
        }
        return b;
    }

    private void startImage_Name() {
        try{

            firebaseFirestore.collection("passenger").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    QuerySnapshot result=task.getResult();
                    List<DocumentSnapshot> collection=result.getDocuments();
                    for (DocumentSnapshot document:collection){
                        Map<String, Object> value=document.getData();
                        String user=(String)(value.get("mail"));
                        if (user.equals(thisUser)){
                            selfImageURL=(String)(value.get("image"));
                            selfName=(String)(value.get("name"));
                        }
                    }
                }
            });
            firebaseFirestore.collection("driver").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    QuerySnapshot result=task.getResult();
                    List<DocumentSnapshot> collection=result.getDocuments();
                    for (DocumentSnapshot document:collection){
                        Map<String, Object> value=document.getData();
                        String user=(String)(value.get("mail"));
                        if (user.equals(thisUser)){
                            selfImageURL=(String)(value.get("image"));
                            selfName=(String)(value.get("name"));
                        }
                    }
                }
            });
        } catch (Exception e){
            selfImageURL="https://cdn.teachercreated.com/covers/1965.png";
            selfName="Wheels User";
        }
    }

    private void addMessage(Message u) {
        Message m=u;
        messages.add(m);
        adapter=new ChatMessagesAdapter(this, messages);
        listU.setAdapter(adapter);
    }

    private void subscribe(){
        firebaseFirestore.collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error==null){
                            for (DocumentSnapshot ds:value.getDocuments()){
                                String email=(String)(ds.get("remitenteMail"));
                                String email2=(String)(ds.get("receptorMail"));
                                if((email.equals(thisUser) || email2.equals(thisUser)) && (email.equals(chatEmail) || email2.equals(chatEmail)) ) {
                                    String name=(String)(ds.get("remitenteNombre"));
                                    String image=(String)(ds.get("remitenteImagen"));
                                    String content = (String) (ds.get("contenido"));
                                    Message u = new Message(name, image, content);
                                    if (!messages.contains(u))
                                        addMessage(u);

                                }
                            }

        }else{
                            Toast.makeText(getApplicationContext(), "Error reading messages", Toast.LENGTH_SHORT).show();
                        }}});
    }

    private void readStartMessages() {
            firebaseFirestore.collection("messages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot ds:task.getResult().getDocuments()){

                        String email=(String)(ds.get("remitenteMail"));
                        String email2=(String)(ds.get("receptorMail"));

                        if((email.equals(thisUser) || email2.equals(thisUser)) && (email.equals(chatEmail) || email2.equals(chatEmail)) ) {
                            String name=(String)(ds.get("remitenteNombre"));
                            String image=(String)(ds.get("remitenteImagen"));
                            String content = (String) (ds.get("contenido"));
                            Message u = new Message(name, image, content);
                            if (!messages.contains(u))
                                addMessage(u);
                        }
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