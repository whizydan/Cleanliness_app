package com.example.uecfs.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.Toast;

import com.example.uecfs.R;
import com.example.uecfs.adapters.NotificationAdapter;
import com.example.uecfs.models.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        RecyclerView notifications = findViewById(R.id.notifications);
        ArrayList<NotificationModel> notificationModels = new ArrayList<>();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationModels.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    NotificationModel notification = dataSnapshot.getValue(NotificationModel.class);
                    if(notification.getUserId().equals(userId)){
                        notificationModels.add(notification);
                    }
                }
                notifications.setLayoutManager(new LinearLayoutManager(NotificationActivity.this,LinearLayoutManager.VERTICAL,false));
                notifications.setAdapter(new NotificationAdapter(NotificationActivity.this,notificationModels));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NotificationActivity.this,error.getDetails(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
