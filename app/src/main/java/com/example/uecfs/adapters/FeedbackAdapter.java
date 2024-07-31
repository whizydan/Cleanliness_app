package com.example.uecfs.adapters;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uecfs.models.FeedbackModel;
import com.example.uecfs.models.UserModel;
import com.example.uecfs.utils.StringViewModel;
import com.example.uecfs.utils.TinyDB;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.example.uecfs.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hadi.emojiratingbar.EmojiRatingBar;
import com.hadi.emojiratingbar.RateStatus;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {
    private List<FeedbackModel> mData; // List to hold data for the adapter
    private List<FeedbackModel> filteredList; // List to hold filtered data
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;
    public ActivityResultLauncher<Intent> cameraLauncher;
    public ActivityResultLauncher<Intent> galleryLauncher;
    public String downloadUrl = "null";
    public String uploaded = "0";
    public String id = "";
    boolean saved = false;
    public MaterialButton resolvedButton;
    public MaterialButton submitEvidence;

    // Constructor to initialize the adapter with data
    public FeedbackAdapter(Context context, List<FeedbackModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.filteredList = new ArrayList<>(data); // Initialize filteredList with mData
        this.mContext = context;
    }

    public static void FeedbackAdapterHome(Context context, List<FeedbackModel> data,ActivityResultLauncher<Intent> cameraLauncher, ActivityResultLauncher<Intent> galleryLauncher){
        FeedbackAdapter adapter = new FeedbackAdapter(context,data);
        adapter.mInflater = LayoutInflater.from(context);
        adapter.mData = data;
        adapter.filteredList = new ArrayList<>(data); // Initialize filteredList with mData
        adapter.mContext = context;
        adapter.cameraLauncher = cameraLauncher;
        adapter.galleryLauncher = galleryLauncher;
    }

    // Method to inflate the layout file and create ViewHolder objects
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_feedback, parent, false);
        return new ViewHolder(view);
    }

    // Method to bind data to ViewHolder objects
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeedbackModel feedback = filteredList.get(position); // Use filteredList instead of mData
        holder.title.setText(feedback.getTitle());
        holder.feedback.setText(feedback.getFeedback());
        holder.locationNtime.setText(feedback.getLocation());
        holder.status.setText(feedback.getStatus());
        try{
            Glide.with(mContext)
                    .load(feedback.getPhoto())
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(holder.photo);
        }catch (Exception ignored){}

        if(feedback.getStatus().equals("Pending")){
            holder.body.setCardBackgroundColor(Color.YELLOW);
        } else if (feedback.getStatus().equals("Completed")) {
            holder.body.setCardBackgroundColor(Color.GREEN);
            try{
                Glide.with(mContext)
                        .load("https://cdn3.iconfinder.com/data/icons/basicolor-arrows-checks/24/155_check_ok_sticker_success-512.png")
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .into(holder.photo);
            }catch (Exception ignored){}
        }
        else if(feedback.getStatus().equals("Reviewed")){
            holder.body.setCardBackgroundColor(Color.rgb(255,140,0));
        }

        TinyDB tinyDB = new TinyDB(mContext);

        holder.itemView.setOnLongClickListener(v -> {
            new MaterialAlertDialogBuilder(mContext).setMessage(feedback.toString()).show();
            return false;
        });


        holder.itemView.setOnClickListener(v->{
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
            View bottomSheetView = bottomSheetDialog.getLayoutInflater().inflate(R.layout.hotspot_info, null);
            bottomSheetDialog.setContentView(bottomSheetView);
            MaterialButton buttonToggle = bottomSheetDialog.findViewById(R.id.buttonToggle);
            ImageView imageView = bottomSheetDialog.findViewById(R.id.image);
            MaterialTextView userId = bottomSheetDialog.findViewById(R.id.userId);
            MaterialTextView location = bottomSheetDialog.findViewById(R.id.location);
            MaterialTextView status = bottomSheetDialog.findViewById(R.id.status);
            MaterialTextView feedbackTextview = bottomSheetDialog.findViewById(R.id.feedback);
            MaterialTextView action = bottomSheetDialog.findViewById(R.id.action);
            EmojiRatingBar ratingBar = bottomSheetDialog.findViewById(R.id.emoji_rating_bar);
            MaterialButton uploadEvidence = bottomSheetDialog.findViewById(R.id.uploadEvidence);
            LinearLayout ratingButtons = bottomSheetDialog.findViewById(R.id.rating);
            MaterialTextView ratingDesc = bottomSheetDialog.findViewById(R.id.desc);
            MaterialTextView coordinates = bottomSheetDialog.findViewById(R.id.coordinates);
            ImageButton like = bottomSheetDialog.findViewById(R.id.like);
            ImageButton dislike = bottomSheetDialog.findViewById(R.id.dislike);
            ImageView responseImage = bottomSheetDialog.findViewById(R.id.response);
            String rating = feedback.getCleanliness();
            resolvedButton = buttonToggle;
            submitEvidence = uploadEvidence;
            id = feedback.getId();

            bottomSheetDialog.setOnShowListener(dialog -> {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheetInternal != null) {
                    BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetInternal);
                    bottomSheetInternal.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            });

            Glide.with(mContext)
                    .load(feedback.getPhoto())
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(imageView);
            userId.setText("UserId: " + feedback.getUsersId());
            location.setText("Location:" + feedback.getLocation());
            status.setText("Status:" + feedback.getStatus());
            feedbackTextview.setText("Complaint:" + feedback.getFeedback());
            action.setText("Action Required:" + feedback.getAction());
            coordinates.setText("Coordinates: " + feedback.getCoordinates());

            FirebaseDatabase.getInstance().getReference("users").child(feedback.getUsersId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    userId.setText("Username: " + userModel.getName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if(rating.equals("GOOD")){
                ratingBar.setCurrentRateStatus(RateStatus.GOOD);
            }else if(rating.equals("BAD")){
                ratingBar.setCurrentRateStatus(RateStatus.BAD);
            }else if(rating.equals("AWFUL")){
                ratingBar.setCurrentRateStatus(RateStatus.AWFUL);
            }else if(rating.equals("GREAT")){
                ratingBar.setCurrentRateStatus(RateStatus.GREAT);
            }else if(rating.equals("OKAY")){
                ratingBar.setCurrentRateStatus(RateStatus.OKAY);
            }

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("feeds").child(feedback.getId());

            like.setOnClickListener(im -> {
                reference.child("like").setValue("like").addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext,"Failed " + e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(mContext,"Updated",Toast.LENGTH_LONG).show();
                        like.setBackgroundColor(Color.GREEN);
                        dislike.setBackgroundColor(Color.TRANSPARENT);
                    }
                });
                reference.child("status").setValue("Reviewed");
            });
            dislike.setOnClickListener(im -> {
                reference.child("like").setValue("dislike").addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext,"Failed " + e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(mContext,"Updated",Toast.LENGTH_LONG).show();
                        dislike.setBackgroundColor(Color.RED);
                        like.setBackgroundColor(Color.TRANSPARENT);
                    }
                });
                reference.child("status").setValue("Reviewed");
            });

            uploadEvidence.setOnClickListener(v1 -> {
                selectImage();
                id = feedback.getId();
            });

            ratingBar.setReadOnly(true);
            if(tinyDB.getBoolean("admin")){
                like.setOnClickListener(null);
                dislike.setOnClickListener(null);
                uploadEvidence.setVisibility(View.VISIBLE);
            }else{
                if(feedback.getStatus().equals("Completed")){
                    ratingButtons.setVisibility(View.VISIBLE);
                    ratingDesc.setVisibility(View.VISIBLE);
                }
            }

            if(feedback.getStatus().equals("Completed")){
                buttonToggle.setVisibility(View.GONE);
                uploadEvidence.setVisibility(View.GONE);
                responseImage.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(feedback.getResponseImage())
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .into(responseImage);


            }else{
                ratingButtons.setVisibility(View.INVISIBLE);
                ratingDesc.setVisibility(View.INVISIBLE);
            }
            if(feedback.getStatus().equals("Reviewed")){
                buttonToggle.setVisibility(View.GONE);
                if(feedback.getLike().equals("like")){
                    like.setBackgroundColor(Color.GREEN);
                }else{
                    dislike.setBackgroundColor(Color.RED);
                }
                ratingButtons.setVisibility(View.VISIBLE);
                ratingDesc.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(feedback.getResponseImage())
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .into(responseImage);
                responseImage.setVisibility(View.VISIBLE);
            }

            buttonToggle.setOnClickListener(v1 -> {
                feedback.setStatus("Completed");
                FirebaseDatabase.getInstance().getReference("feeds").child(feedback.getId())
                        .setValue(feedback)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext,"Error: " + e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                bottomSheetDialog.dismiss();
                            }
                        });
            });

            bottomSheetDialog.show();
        });
    }

    // Method to get the total number of items in the data set
    @Override
    public int getItemCount() {
        return filteredList.size(); // Return size of filteredList
    }

    // ViewHolder class to hold views for each item in the RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, feedback, locationNtime, status;
        ImageView photo;
        MaterialCardView body;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            feedback = itemView.findViewById(R.id.feedback);
            locationNtime = itemView.findViewById(R.id.location);
            photo = itemView.findViewById(R.id.photo);
            status = itemView.findViewById(R.id.status);
            body = itemView.findViewById(R.id.body);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // Interface for click listener callback
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // Method to set click listener for items in the RecyclerView
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // Method to filter the data based on a search query
    public void filter(String location, String status) {
        filteredList.clear();
        for (FeedbackModel item : mData) {
            if(item.getLocation().equalsIgnoreCase("all") || item.getStatus().equalsIgnoreCase("all")){
                filteredList.add(item);
            }else{
                if (item.getLocation().equals(location) && item.getStatus().equals(status)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter that data set has changed
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Add Photo");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePicture.resolveActivity(mContext.getPackageManager()) != null) {
                    cameraLauncher.launch(takePicture);
                }
            } else if (options[item].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(pickPhoto);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
