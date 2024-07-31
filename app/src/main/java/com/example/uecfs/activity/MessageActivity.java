package com.example.uecfs.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uecfs.R;
import com.example.uecfs.adapters.MessagesAdapter;
import com.example.uecfs.models.MessagesModel;
import com.example.uecfs.utils.TinyDB;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        TextInputLayout message = findViewById(R.id.chat);
        RecyclerView chats = findViewById(R.id.recyclerView);
        TinyDB tinyDB = new TinyDB(this);
        boolean isAdmin = tinyDB.getBoolean("admin");
        String userId = getIntent().getStringExtra("id");
        String userName = getIntent().getStringExtra("name");
        TextView name = findViewById(R.id.name);
        LinearLayout section = findViewById(R.id.section);

        if(!isAdmin){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            name.setText("Admin");
        }else{
            name.setText(userName);
        }

        FirebaseDatabase.getInstance().getReference().child("chats").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<MessagesModel> messages = new ArrayList<>();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    MessagesModel message = dataSnapshot.getValue(MessagesModel.class);
                    messages.add(message);
                }
                section.setVisibility(View.GONE);
                chats.setVisibility(View.VISIBLE);
                chats.setLayoutManager(new LinearLayoutManager(MessageActivity.this,LinearLayoutManager.VERTICAL,false));
                chats.setAdapter(new MessagesAdapter(MessageActivity.this,messages));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MessageActivity.this,"Error loading chat: " + error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        String finalUserId = userId;
        message.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(message.getEditText().getText())){
                    MessagesModel messagesModel = new MessagesModel();
                    messagesModel.setMessage(message.getEditText().getText().toString());
                    messagesModel.setId(String.valueOf(System.currentTimeMillis()));
                    messagesModel.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                    messagesModel.setAdmin(isAdmin);


                    if(!isAdmin){
                        FirebaseDatabase.getInstance().getReference().child("chats").child(messagesModel.getUserId()).child(messagesModel.getId())
                                .setValue(messagesModel);
                    }else{
                        messagesModel.setAdmin(true);
                        FirebaseDatabase.getInstance().getReference().child("chats").child(finalUserId).child(messagesModel.getId())
                                .setValue(messagesModel);
                    }
                    message.getEditText().setText("");
                }else{
                    message.setError("Enter message");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            message.setError("");
                        }
                    }, 1000);
                }

            }
        });
    }
}