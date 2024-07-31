package com.example.uecfs.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.uecfs.R;
import com.example.uecfs.activity.GiveFeedbackActivity;
import com.example.uecfs.activity.RatingActivity;
import com.example.uecfs.dialogs.CoordinatesFinder;
import com.example.uecfs.dialogs.Loader;
import com.example.uecfs.utils.TinyDB;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GiveFeedbackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GiveFeedbackFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private Uri imageUri;
    private String imageUrl;
    private String remarkValue;
    MaterialButton addImage;
    String locationValue;
    String locationValue2;
    TinyDB tinyDB;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    MaterialButton coordinates;
    private Context mContext;

    public GiveFeedbackFragment(Context context) {
        // Required empty public constructor
        mContext = context;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GiveFeedbackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GiveFeedbackFragment newInstance(String param1, String param2, Context context) {
        GiveFeedbackFragment fragment = new GiveFeedbackFragment(context);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_give_feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton next = view.findViewById(R.id.next);
        Spinner location = view.findViewById(R.id.materialButton2);
        coordinates = view.findViewById(R.id.materialButton3);
        addImage = view.findViewById(R.id.materialButton5);
        MaterialButton remark = view.findViewById(R.id.materialButton4);
        remarkValue = "";
        locationValue = "";
        tinyDB = new TinyDB(requireActivity());

        remark.setOnClickListener(vv  -> {
            BottomSheetDialog dialog = new BottomSheetDialog(requireActivity());
            dialog.setContentView(R.layout.remarks_sheet);

            TextInputLayout remarkInput = dialog.findViewById(R.id.remark);
            MaterialButton submit = dialog.findViewById(R.id.submit);
            if(!TextUtils.isEmpty(remarkValue)){
                remarkInput.getEditText().setText(remarkValue);
            }

            submit.setOnClickListener(v -> {
                if(remarkInput.getEditText().getText().toString().isEmpty()){
                    remarkInput.setError("Enter remark");
                }else{
                    remarkValue = remarkInput.getEditText().getText().toString();
                    remark.setBackgroundColor(Color.GREEN);
                    dialog.dismiss();
                }
            });

            dialog.show();
        });

        addImage.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Select Image Source");
            String[] options = {"Camera", "Gallery"};
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    // Camera option selected
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                    } else {
                        Toast.makeText(mContext, "Camera not available", Toast.LENGTH_SHORT).show();
                    }
                } else if (which == 1) {
                    // Gallery option selected
                    Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    pickImageIntent.setType("image/*");
                    startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);
                }
            });
            builder.show();
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        coordinates.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                CoordinatesFinder dialog = new CoordinatesFinder(requireActivity(),requireActivity());
                dialog.show();
                locationValue2 = tinyDB.getString("loc");
                coordinates.setBackgroundColor(Color.GREEN);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireActivity(),
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

        next.setOnClickListener(v -> {
            if(TextUtils.isEmpty(remarkValue)){
                Toast.makeText(requireActivity(),"Enter remark",Toast.LENGTH_LONG).show();
            }else if (TextUtils.isEmpty(locationValue)){
                Toast.makeText(requireActivity(),"pick a location or share your coordinates", Toast.LENGTH_LONG).show();
            }else{
                RatingFragment ratingFragment = RatingFragment.newInstance(imageUrl,remarkValue,locationValue, locationValue2,mContext);
                getFragmentManager().beginTransaction().replace(R.id.fragmentHolder,ratingFragment).commit();
                //Toast.makeText(requireActivity(),imageUrl + " " + remarkValue + " " + locationValue, Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CoordinatesFinder dialog = new CoordinatesFinder(requireActivity(),requireActivity());
                dialog.show();
                locationValue = tinyDB.getString("loc");
                coordinates.setBackgroundColor(Color.GREEN);
            } else {
                Toast.makeText(requireActivity(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                // Image selected from gallery
                imageUri = data.getData();
                uploadImage();
            } else if (requestCode == CAMERA_REQUEST && data != null && data.getExtras() != null) {
                // Image captured from camera
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageUri = getImageUriFromBitmap(imageBitmap);
                uploadImage();
            }
        }
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, "Image", null);
        return Uri.parse(path);
    }

    private void uploadImage() {
        Loader loader = new Loader(requireActivity());
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
                                    Toast.makeText(requireActivity(), "Upload successful: " + downloadUrl, Toast.LENGTH_LONG).show();
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
                            Toast.makeText(requireActivity(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            loader.dismiss();
            Toast.makeText(requireActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}