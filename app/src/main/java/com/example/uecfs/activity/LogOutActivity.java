package com.example.uecfs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uecfs.R;
import com.google.firebase.auth.FirebaseAuth;

public class LogOutActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_out);

        mAuth = FirebaseAuth.getInstance();
    }

    public void performLogout(View view) {
        mAuth.signOut(); // Logout from Firebase

        // Redirect the user back to the main activity after logout
        openMainActivity();

        // Close the current activity
        finish();
    }

    private void openMainActivity() {
        Intent intent = new Intent(LogOutActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
