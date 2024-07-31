package com.example.uecfs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uecfs.R;
import com.example.uecfs.dialogs.Loader;
import com.example.uecfs.models.FeedbackModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ActionActivity extends AppCompatActivity {
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        action = "";
        ImageButton wipe = findViewById(R.id.wipe);
        ImageButton mop = findViewById(R.id.mop);
        ImageButton vacuum = findViewById(R.id.vacuum);
        ImageButton sweep = findViewById(R.id.sweep);
        ImageButton wash = findViewById(R.id.wash);
        ImageButton flowering = findViewById(R.id.flower);
        ImageButton spray = findViewById(R.id.spray);
        ImageButton disinfect = findViewById(R.id.disinfect);

        wipe.setOnClickListener(v -> {
            next("Wiping");
        });
        mop.setOnClickListener(v -> {
            next("Mopping");
        });
        vacuum.setOnClickListener(v -> {
            next("Vacuum");
        });
        sweep.setOnClickListener(v -> {
            next("Sweeping");
        });
        wash.setOnClickListener(v -> {
            next("Hand Washing");
        });
        flowering.setOnClickListener(v -> {
            next("Flowering");
        });
        spray.setOnClickListener(v -> {
            next("Spraying");
        });
        disinfect.setOnClickListener(v -> {
            next("Disinfecting");
        });

    }
    private void next(String value){
        String image = getIntent().getStringExtra("image");
        String remark = getIntent().getStringExtra("remark");
        String location = getIntent().getStringExtra("location");
        String rating = getIntent().getStringExtra("rating");
        String location2 = getIntent().getStringExtra("loc");
        String id = String.valueOf(System.currentTimeMillis());
        action = value;

        FeedbackModel feedback = new FeedbackModel();
        feedback.setStatus("Pending");
        feedback.setId(id);
        feedback.setFeedback(remark);
        feedback.setUsersId(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        feedback.setPhoto(image);
        feedback.setAction(action);
        feedback.setLocation(location);
        feedback.setCleanliness(rating);
        feedback.setCoordinates(location2);
        feedback.setTitle(location + ": " + action + " " + rating);

        GiveFeedbackActivity feedbackActivity = new GiveFeedbackActivity();
        Loader loader = new Loader(this);
        loader.show();

        FirebaseDatabase.getInstance().getReference().child("feeds").child(feedback.getId())
                .setValue(feedback)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        loader.dismiss();
                        Toast.makeText(getApplicationContext(),"Feedback has been received",Toast.LENGTH_LONG).show();
                        feedbackActivity.saveNotification();
                        startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loader.dismiss();
                        new MaterialAlertDialogBuilder(ActionActivity.this)
                                .setTitle("Could not save feedback")
                                .setMessage(e.getMessage())
                                .show();
                    }
                })
                ;
    }
}