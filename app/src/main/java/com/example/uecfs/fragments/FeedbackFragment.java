package com.example.uecfs.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uecfs.activity.GiveFeedbackActivity;
import com.example.uecfs.R;
import com.example.uecfs.adapters.FeedbackAdapter;
import com.example.uecfs.models.FeedbackModel;
import com.example.uecfs.utils.TinyDB;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

public class FeedbackFragment extends Fragment {

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri imageUri;
    RecyclerView feedback;
    FeedbackAdapter adapter;
    ArrayList<FeedbackModel> feedbackModelArrayList;
    Spinner status;
    Spinner location;
    int count = 0;
    LinearLayout section;
    private Context mContext;
    ExtendedFloatingActionButton addFeedback;

    public FeedbackFragment(Context context) {
        // Required empty public constructor
        mContext = context;
    }

    public static FeedbackFragment newInstance(String param1, String param2,Context context) {
        return new FeedbackFragment(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        location = view.findViewById(R.id.location);
        status = view.findViewById(R.id.status);
        TextView name = view.findViewById(R.id.username);
        TinyDB tinyDB = new TinyDB(requireActivity());
        name.setText("HI, " + tinyDB.getString("name"));

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        imageUri = getImageUri(requireContext(), imageBitmap);
                        uploadImageToFirebase(imageUri);
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        uploadImageToFirebase(imageUri);
                    }
                });

        feedback = view.findViewById(R.id.feedback);
        addFeedback = view.findViewById(R.id.floatingActionButton);
        feedbackModelArrayList = new ArrayList<>();

        new SimpleTooltip.Builder(requireActivity())
                .anchorView(addFeedback)
                .text("click me to complain")
                .gravity(Gravity.TOP)
                .animated(true)
                .transparentOverlay(true)
                .build()
                .show();

        addFeedback.setOnClickListener(v->{
            //startActivity(new Intent(requireActivity(), GiveFeedbackActivity.class));
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder,new GiveFeedbackFragment(mContext)).commit();
        });

        updateUi();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location.setAdapter(adapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(statusAdapter);

        location.setSelection(0);
        status.setSelection(0);

        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        //updateUi();
    }

    private void filter(){
        if(adapter != null && count == 0){
            adapter.filter(location.getSelectedItem().toString(),status.getSelectedItem().toString());
        }
    }

    private void updateUi(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("feeds");
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        TinyDB tinyDB = new TinyDB(mContext);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feedbackModelArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    FeedbackModel feedBack = dataSnapshot.getValue(FeedbackModel.class);
                    if(tinyDB.getBoolean("admin")){
                        feedbackModelArrayList.add(feedBack);
                    }else{
                        if (feedBack != null && feedBack.getUsersId().equals(userId)) {
                            feedbackModelArrayList.add(feedBack);
                        }
                    }
                }

                feedback.setVisibility(View.VISIBLE);
                FeedbackAdapter feedbackAdapter = new FeedbackAdapter(mContext, feedbackModelArrayList);
                feedbackAdapter.cameraLauncher = cameraLauncher;
                feedbackAdapter.galleryLauncher = galleryLauncher;
                adapter = feedbackAdapter;
                feedback.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false));
                feedback.setAdapter(adapter);

                feedback.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        addFeedback.setExtended(false);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println(error.getDetails());
            }
        });
    }

    private Uri getImageUri(Context context, Bitmap imageBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), imageBitmap, "Title", null);
        return Uri.parse(path);
    }

    private void uploadImageToFirebase(Uri uri) {
        if (uri != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("feeds").child(adapter.id);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference fileReference = storageReference.child("uploads/" + System.currentTimeMillis() + ".jpg");

            fileReference.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                        String downloadUrl = uri1.toString();
                        adapter.downloadUrl = downloadUrl;
                        Toast.makeText(requireContext(), "Upload successful" + downloadUrl, Toast.LENGTH_SHORT).show();
                        adapter.submitEvidence.setVisibility(View.GONE);
                        adapter.resolvedButton.setVisibility(View.VISIBLE);
                        reference.child("responseImage").setValue(downloadUrl);
                        // Handle the download URL as needed
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        adapter.uploaded = "2";
                    });
        }
    }

    public ActivityResultLauncher<Intent> getCameraLauncher() {
        return cameraLauncher;
    }

    public ActivityResultLauncher<Intent> getGalleryLauncher() {
        return galleryLauncher;
    }

}
