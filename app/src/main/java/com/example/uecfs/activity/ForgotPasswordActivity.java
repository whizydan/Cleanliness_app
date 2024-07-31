package com.example.uecfs.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uecfs.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        EditText email = findViewById(R.id.email);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        MaterialButton reset = findViewById(R.id.reset);

        reset.setOnClickListener(view ->{
            if(TextUtils.isEmpty(email.getText())){
                email.setError("Enter email");
            }else{
                mAuth.sendPasswordResetEmail(email.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                showMessage("Success", "An email with instructions to reset your password has been to: " + email.getText().toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessage("Failed", e.getLocalizedMessage());
                            }
                        });
            }
        });

    }
    private void showMessage(String title, String message){
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .show();
    }
}