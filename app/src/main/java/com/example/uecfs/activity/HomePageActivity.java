package com.example.uecfs.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.uecfs.R;
import com.example.uecfs.fragments.FeedbackFragment;
import com.example.uecfs.fragments.HotspotFragment;
import com.example.uecfs.fragments.MessagesFragment;
import com.example.uecfs.fragments.ProfileFragment;
import com.example.uecfs.utils.TinyDB;
import com.example.uecfs.utils.Utility;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        TinyDB tinyDB = new TinyDB(this);
        if(!tinyDB.getBoolean("changelog-v1.2")){
            Utility utility = new Utility(this);
            utility.render();
            tinyDB.putBoolean("changelog-v1.2",true);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new FeedbackFragment(HomePageActivity.this)).commit();
        BottomNavigationView navigationView = findViewById(R.id.navigationView);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                if(menuItem.getItemId() == R.id.feedback){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new FeedbackFragment(HomePageActivity.this)).commit();
                } else if (menuItem.getItemId() == R.id.messages) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new MessagesFragment()).commit();
                } else if (menuItem.getItemId() == R.id.hotspot) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new HotspotFragment()).commit();
                } else if (menuItem.getItemId() == R.id.profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new ProfileFragment()).commit();
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}