package com.example.uecfs.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.uecfs.R;

public class HotspotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot);
    }

    public void openContactHotlineActivity(View view) {
        Intent intent = new Intent(HotspotActivity.this, ContactHotlineActivity.class);
        startActivity(intent);
    }
}