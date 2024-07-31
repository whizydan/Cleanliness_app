package com.example.uecfs.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.uecfs.R;

public class PhoneCallActivity extends AppCompatActivity {

    private TextView phoneNumber1, phoneNumber2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_call);

        // Initialize TextViews
        phoneNumber1 = findViewById(R.id.textView26);
        phoneNumber2 = findViewById(R.id.textView27);

        // Set click listeners
        phoneNumber1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = phoneNumber1.getText().toString();
                dialPhoneNumber(number);
            }
        });

        phoneNumber2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = phoneNumber2.getText().toString();
                dialPhoneNumber(number);
            }
        });
    }

    public void openContactHotlineActivity(View view) {
        Intent intent = new Intent(PhoneCallActivity.this, ContactHotlineActivity.class);
        startActivity(intent);
    }

    private void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
}