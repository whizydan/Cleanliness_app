package com.example.uecfs.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.uecfs.R;
import com.example.uecfs.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        EditText username = findViewById(R.id.editTextUsername);
        EditText matricNo = findViewById(R.id.editTextMatricNo);
        EditText password = findViewById(R.id.editTextConfirmPassword);
        EditText cPassword = findViewById(R.id.editTextPassword);
        Button save = findViewById(R.id.buttonSave);
        final UserModel[] user = new UserModel[1];

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user[0] = snapshot.getValue(UserModel.class);
                username.setText(user[0].getName());
                matricNo.setText(user[0].getMetricNo());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, error.getDetails(),Toast.LENGTH_LONG).show();
            }
        });

        save.setOnClickListener(v->{
            if(TextUtils.isEmpty(username.getText().toString())){
                username.setError("Enter username");
            }else if(TextUtils.isEmpty(matricNo.getText().toString())){
                matricNo.setError("Enter Matric No");
            }else if(TextUtils.isEmpty(password.getText().toString())){
                password.setError("Enter password");
            }else if(TextUtils.isEmpty(cPassword.getText().toString())){
                cPassword.setError("Confirm password");
            }else if(!password.getText().toString().equals(cPassword.getText().toString())){
                cPassword.setError("Passwords do not match");
            }else{
                user[0].setName(username.getText().toString());
                user[0].setMetricNo(matricNo.getText().toString());
                user[0].setPassword(password.getText().toString());
                reference.setValue(user[0]).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ProfileActivity.this,"Profile updated",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
}
