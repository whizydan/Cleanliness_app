package com.example.uecfs.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.uecfs.R;
import com.example.uecfs.activity.HomePageActivity;
import com.google.android.material.button.MaterialButton;

public class ContactHotlineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_hotline);
        MaterialButton call = findViewById(R.id.call);
        MaterialButton email = findViewById(R.id.email);

        call.setOnClickListener(v->{
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + "049284000"));

            // Verify that the device can handle the intent (avoid crashing on devices without dialer)
            if (callIntent.resolveActivity(getPackageManager()) != null) {
                // Start the dialer activity
                startActivity(callIntent);
            }
        });
        email.setOnClickListener(v->{
            sendEmail();
        });

    }
    private void sendEmail() {
        String[] recipients = {"suppport@uecfs.com", "disaster@uecfs.org"};
        String subject = "UECFS: Hotline";
        String message = "I would like to report that";

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // Only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No email app installed", Toast.LENGTH_SHORT).show();
        }
    }
}
