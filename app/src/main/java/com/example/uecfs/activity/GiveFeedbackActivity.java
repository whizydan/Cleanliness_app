package com.example.uecfs.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.uecfs.R;
import com.example.uecfs.dialogs.CoordinatesFinder;
import com.example.uecfs.dialogs.Loader;
import com.example.uecfs.models.FeedbackModel;
import com.example.uecfs.models.NotificationModel;
import com.example.uecfs.utils.TinyDB;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class GiveFeedbackActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String imageUrl;
    private String remarkValue;
    MaterialButton addImage;
    String locationValue;
    String locationValue2 = "";
    TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_feedback);
        MaterialButton next = findViewById(R.id.next);
        Spinner location = findViewById(R.id.materialButton2);
        MaterialButton coordinates = findViewById(R.id.materialButton3);
        addImage = findViewById(R.id.materialButton5);
        MaterialButton remark = findViewById(R.id.materialButton4);
        remarkValue = "";
        locationValue = "";
        tinyDB = new TinyDB(this);

        remark.setOnClickListener(v  -> {
            BottomSheetDialog dialog = new BottomSheetDialog(this);
            dialog.setContentView(R.layout.remarks_sheet);

            TextInputLayout remarkInput = dialog.findViewById(R.id.remark);
            MaterialButton submit = dialog.findViewById(R.id.submit);

            submit.setOnClickListener(view -> {
                if(remarkInput.getEditText().getText().toString().isEmpty()){
                    remarkInput.setError("Enter remark");
                }else{
                    remarkValue = remarkInput.getEditText().getText().toString();
                    v.setBackgroundColor(Color.GREEN);
                    dialog.dismiss();
                }
            });

            dialog.show();
        });

        addImage.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        coordinates.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                CoordinatesFinder dialog = new CoordinatesFinder(this,this);
                dialog.show();

                locationValue2 =  tinyDB.getString("loc");
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locations, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locationValue = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        location.setAdapter(adapter);

        next.setOnClickListener(view -> {
            if(TextUtils.isEmpty(remarkValue)){
                Toast.makeText(this,"Enter remark",Toast.LENGTH_LONG).show();
            }else if (TextUtils.isEmpty(locationValue)){
                Toast.makeText(this,"pick a location or share your coordinates", Toast.LENGTH_LONG).show();
            }
            Intent intent = new Intent(this, RatingActivity.class);
            intent.putExtra("image", imageUrl);
            intent.putExtra("remark",remarkValue);
            intent.putExtra("location", locationValue);
            intent.putExtra("loc", locationValue2);
            startActivity(intent);
        });
    }

    public void saveNotification() {
        NotificationModel notification = new NotificationModel();
        notification.setId(String.valueOf(System.currentTimeMillis()));
        notification.setTitle("Feedback has been submitted");
        notification.setMessage("We have received your feedback \nwe will work on it and you should see an update in the history once it is resolved. \nThank You:)");
        notification.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        notification.setDate(dateFormatter.format(new Date()));

        FirebaseDatabase.getInstance().getReference().child("notification").child(notification.getId())
                .setValue(notification);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CoordinatesFinder dialog = new CoordinatesFinder(this,this);
                dialog.show();
                locationValue = tinyDB.getString("loc");
            } else {
                Toast.makeText(GiveFeedbackActivity.this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        Loader loader = new Loader(this);
        loader.show();
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child("uploads/" + System.currentTimeMillis() + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    loader.dismiss();
                                    String downloadUrl = uri.toString();
                                    Toast.makeText(GiveFeedbackActivity.this, "Upload successful: " + downloadUrl, Toast.LENGTH_LONG).show();
                                    // Handle the download URL as needed
                                    imageUrl = downloadUrl;
                                    addImage.setBackgroundColor(Color.GREEN);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loader.dismiss();
                            Toast.makeText(GiveFeedbackActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            loader.dismiss();
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
