package com.example.uecfs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uecfs.App;
import com.example.uecfs.R;
import com.example.uecfs.models.UserModel;
import com.example.uecfs.utils.TinyDB;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton signIn = findViewById(R.id.sign_in);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        TextView signUp = findViewById(R.id.sign_up);
        TextView forgotPassword = findViewById(R.id.forgot_password);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(this, HomePageActivity.class));
            finish();
        }
        forgotPassword.setOnClickListener(view ->{
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });
        signUp.setOnClickListener(view ->{
            startActivity(new Intent(this, RegisterActivity.class));
        });

        signIn.setOnClickListener(view ->{
            if(TextUtils.isEmpty(email.getText().toString())){
                email.setError("Enter email");
            }else if(TextUtils.isEmpty(password.getText().toString())){
                password.setError("Enter password");
            }else{
                mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                                getData(intent, authResult.getUser().getUid());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessage("Login failed", e.getLocalizedMessage());
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

    private void getData(Intent intent, String uid){
        TinyDB tinyDB = new TinyDB(this);
        FirebaseDatabase.getInstance().getReference("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                tinyDB.putBoolean("admin",user.isStaff());
                tinyDB.putString("name",user.getName());
                tinyDB.putString("gender",user.getGender());
                tinyDB.putString("matricNo",user.getMetricNo());
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage("Failed getting user data", error.getMessage());
                FirebaseAuth.getInstance().signOut();
            }
        });


        startActivity(intent);
        finish();
    }

}
