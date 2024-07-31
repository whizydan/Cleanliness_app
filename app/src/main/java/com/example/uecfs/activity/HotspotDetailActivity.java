package com.example.uecfs.activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uecfs.R;
import com.example.uecfs.adapters.LocationDistributionAdapter;
import com.example.uecfs.models.ActionsModel;
import com.example.uecfs.models.Distribution;
import com.example.uecfs.models.FeedbackModel;
import com.example.uecfs.models.HotspotDistributions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HotspotDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot_detail);
        RecyclerView items = findViewById(R.id.items);
        TextView location = findViewById(R.id.location);
        ArrayList<FeedbackModel> feedbackModelArraylist = new ArrayList<>();
        ArrayList<ActionsModel> actionsModelArrayList = new ArrayList<>();
        ArrayList<HotspotDistributions> distributionArrayList = new ArrayList<>();
        String locationName = getIntent().getStringExtra("location");
        ImageButton back = findViewById(R.id.back);

        back.setOnClickListener(view -> {
            super.onBackPressed();
        });

        actionsModelArrayList.add(new ActionsModel("Wiping", R.drawable.wipe));
        actionsModelArrayList.add(new ActionsModel("Mopping", R.drawable.mop));
        actionsModelArrayList.add(new ActionsModel("Vacuum", R.drawable.vacuum));
        actionsModelArrayList.add(new ActionsModel("Sweeping", R.drawable.sweep));
        actionsModelArrayList.add(new ActionsModel("Hand Washing",R.drawable.hand_wash));
        actionsModelArrayList.add(new ActionsModel("Flowering",R.drawable.flower));
        actionsModelArrayList.add(new ActionsModel("Spraying",R.drawable.spray));
        actionsModelArrayList.add(new ActionsModel("Disinfecting", R.drawable.disinfect));

        location.setText(locationName);
        FirebaseDatabase.getInstance().getReference("feeds").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    FeedbackModel feedback = dataSnapshot.getValue(FeedbackModel.class);

                    if(feedback.getLocation().equals(locationName) || feedback.getCoordinates().equals(locationName)){
                        feedbackModelArraylist.add(feedback);
                    }
                }
                for(ActionsModel action: actionsModelArrayList){
                    int totalActions = feedbackModelArraylist.size();
                    int totalFeedbacksForThisAction = 0;

                    for(FeedbackModel feedbackModel: feedbackModelArraylist){
                        if(feedbackModel.getAction().equals(action.getAction())){
                            totalFeedbacksForThisAction++;
                        }
                    }

                    HotspotDistributions distribution = new HotspotDistributions();
                    distribution.setDrawable(action.getIcon());
                    distribution.setAction(action.getAction());
                    float percentage = ((float) totalFeedbacksForThisAction /totalActions) * 100;
                    distribution.setPercentage(percentage);
                    if(percentage > 0.1){
                        distributionArrayList.add(distribution);
                    }
                }

                items.setLayoutManager(new LinearLayoutManager(HotspotDetailActivity.this,LinearLayoutManager.VERTICAL,false));
                items.setAdapter(new LocationDistributionAdapter(HotspotDetailActivity.this,distributionArrayList,locationName));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
