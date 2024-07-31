package com.example.uecfs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uecfs.R;
import com.google.android.material.button.MaterialButton;
import com.hadi.emojiratingbar.EmojiRatingBar;
import com.hadi.emojiratingbar.RateStatus;

public class RatingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rating);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String imageUrl = getIntent().getStringExtra("image");
        String remark = getIntent().getStringExtra("remark");
        String location = getIntent().getStringExtra("location");
        String location2 = getIntent().getStringExtra("loc");

        EmojiRatingBar ratingBar = findViewById(R.id.emoji_rating_bar);
        MaterialButton next = findViewById(R.id.next);

        next.setOnClickListener(view -> {
            if(ratingBar.getCurrentRateStatus() == RateStatus.EMPTY){
                Toast.makeText(this,"Please select a rating",Toast.LENGTH_LONG).show();
            }else{
                Intent intent = new Intent(this, ActionActivity.class);
                intent.putExtra("image", imageUrl);
                intent.putExtra("remark",remark);
                intent.putExtra("location", location);
                intent.putExtra("loc",location2);
                intent.putExtra("rating", ratingBar.getCurrentRateStatus().toString());
                startActivity(intent);
            }
        });
    }
}