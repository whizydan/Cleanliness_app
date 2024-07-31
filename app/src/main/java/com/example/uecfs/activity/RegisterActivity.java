package com.example.uecfs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uecfs.R;
import com.example.uecfs.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final String[] checked = {""};
        final boolean[] account = {false};

        RadioGroup gender = findViewById(R.id.gender);
        RadioGroup accountType = findViewById(R.id.type);
        EditText name = findViewById(R.id.name);
        EditText email = findViewById(R.id.email);
        EditText matrciNo = findViewById(R.id.matric);
        EditText password = findViewById(R.id.password);
        MaterialButton signUp = findViewById(R.id.sign_up);

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.male){
                    checked[0] = "Male";
                }else{
                    checked[0] = "Female";
                }
            }
        });
        accountType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                account[0] = checkedId == R.id.staff;
            }
        });

        signUp.setOnClickListener(v ->{
            if(TextUtils.isEmpty(name.getText().toString())){
                name.setError("Enter name");
            }else if(TextUtils.isEmpty(email.getText().toString())){
                email.setError("enter email");
            }else if(TextUtils.isEmpty(matrciNo.getText().toString())){
                matrciNo.setError("Enter Matric no.");
            }else if(TextUtils.isEmpty(password.getText().toString())){
                password.setError("Enter password");
            }else if(checked[0] == ""){
                Toast.makeText(RegisterActivity.this,"select your gender", Toast.LENGTH_LONG).show();
            }else{
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
                UserModel user = new UserModel();
                user.setEmail(email.getText().toString());
                user.setGender(checked[0]);
                user.setName(name.getText().toString());
                user.setPassword(password.getText().toString());
                user.setMetricNo(matrciNo.getText().toString());
                user.setStaff(account[0]);

                mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                reference.child(authResult.getUser().getUid().toString()).setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                FirebaseAuth.getInstance().signOut();
                                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                showMessage("Could not save user", e.getLocalizedMessage());
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessage("failed to Register", e.getLocalizedMessage());
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
