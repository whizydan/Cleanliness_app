package com.example.uecfs.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uecfs.R;
import com.example.uecfs.models.Distribution;
import com.example.uecfs.models.FeedbackModel;
import com.example.uecfs.models.HotspotDistributions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LocationDistributionAdapter extends RecyclerView.Adapter<LocationDistributionAdapter.ViewHolder> {
    private List<HotspotDistributions> mData; // List to hold data for the adapter
    private List<HotspotDistributions> filteredList; // List to hold filtered data
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;
    private String locationName;

    // Constructor to initialize the adapter with data
    public LocationDistributionAdapter(Context context, List<HotspotDistributions> data, String location) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.filteredList = new ArrayList<>(data); // Initialize filteredList with mData
        this.mContext = context;
        this.locationName = location;
    }

    // Method to inflate the layout file and create ViewHolder objects
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.hotspot_item_individual, parent, false);
        return new ViewHolder(view);
    }

    // Method to bind data to ViewHolder objects
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HotspotDistributions distribution = filteredList.get(position);
        holder.icon.setImageDrawable(mContext.getResources().getDrawable(distribution.getDrawable()));
        holder.distribution.setText(distribution.getPercentage()+"% Complaints");

        holder.button.setOnClickListener(v->{
            showSheet(distribution.getAction());
        });
    }

    // Method to get the total number of items in the data set
    @Override
    public int getItemCount() {
        return filteredList.size(); // Return size of filteredList
    }

    // ViewHolder class to hold views for each item in the RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView distribution;
        MaterialButton button;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            distribution = itemView.findViewById(R.id.distribution);
            icon = itemView.findViewById(R.id.icon);
            button = itemView.findViewById(R.id.view);
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
    public void filter(String query) {
        filteredList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(mData); // If query is empty, show all data
        } else {
            String searchQuery = query.toLowerCase().trim();
            for (HotspotDistributions item : mData) {
                if (item.getAction().toLowerCase().contains(searchQuery)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter that data set has changed
    }
    private void showSheet(String action){
        BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        dialog.setContentView(R.layout.hotpot_children);
        RecyclerView recyclerView = dialog.findViewById(R.id.items);

        ArrayList<FeedbackModel> feedbackModelArrayList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("feeds").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    FeedbackModel feedback = dataSnapshot.getValue(FeedbackModel.class);
                    if(feedback.getLocation().equals(locationName) && feedback.getAction().equals(action)){
                        feedbackModelArrayList.add(feedback);
                    }
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
                recyclerView.setAdapter(new FeedbackAdapter(mContext,feedbackModelArrayList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dialog.show();
    }
}
