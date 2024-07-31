package com.example.uecfs.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uecfs.R;
import com.example.uecfs.models.Distribution;
import com.example.uecfs.models.NotificationModel;

import java.util.ArrayList;
import java.util.List;

public class DistributionAdapter extends RecyclerView.Adapter<DistributionAdapter.ViewHolder> {
    private List<Distribution> mData; // List to hold data for the adapter
    private List<Distribution> filteredList; // List to hold filtered data
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;

    // Constructor to initialize the adapter with data
    public DistributionAdapter(Context context, List<Distribution> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.filteredList = new ArrayList<>(data); // Initialize filteredList with mData
        this.mContext = context;
    }

    // Method to inflate the layout file and create ViewHolder objects
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.hotspot_item, parent, false);
        return new ViewHolder(view);
    }

    // Method to bind data to ViewHolder objects
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Distribution distribution = filteredList.get(position);
        holder.location.setText(distribution.getLocation());
        holder.percentage.setText(String.valueOf(distribution.getPercentage()+"%"));

        if(distribution.getPercentage() > 70){
            holder.percentage.setTextColor(Color.RED);
        } else if (distribution.getPercentage() > 40) {
            holder.percentage.setTextColor(Color.MAGENTA);
        } else if (distribution.getPercentage() < 40) {
            holder.percentage.setTextColor(Color.GREEN);
        }
    }

    // Method to get the total number of items in the data set
    @Override
    public int getItemCount() {
        return filteredList.size(); // Return size of filteredList
    }

    // ViewHolder class to hold views for each item in the RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView distribution, location, percentage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            distribution = itemView.findViewById(R.id.distribution);
            location = itemView.findViewById(R.id.location);
            percentage = itemView.findViewById(R.id.pass);
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
            for (Distribution item : mData) {
                if (item.getLocation().toLowerCase().contains(searchQuery)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter that data set has changed
    }
}
