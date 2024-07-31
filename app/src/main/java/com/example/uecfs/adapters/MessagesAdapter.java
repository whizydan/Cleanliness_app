package com.example.uecfs.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uecfs.R;
import com.example.uecfs.models.MessagesModel;
import com.example.uecfs.models.NotificationModel;
import com.example.uecfs.utils.TinyDB;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private List<MessagesModel> mData; // List to hold data for the adapter
    private List<MessagesModel> filteredList; // List to hold filtered data
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;

    // Constructor to initialize the adapter with data
    public MessagesAdapter(Context context, List<MessagesModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.filteredList = new ArrayList<>(data); // Initialize filteredList with mData
        this.mContext = context;
    }

    // Method to inflate the layout file and create ViewHolder objects
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }

    // Method to bind data to ViewHolder objects
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessagesModel message = filteredList.get(position);
        TinyDB tinyDB = new TinyDB(mContext);
        boolean isAdminLoggedIn = tinyDB.getBoolean("admin");
        if(isAdminLoggedIn){
            if(message.isAdmin()){
                holder.messageRight.setText(message.getMessage());
                holder.messageRight.setVisibility(View.VISIBLE);
            }else{
                holder.messageLeft.setText(message.getMessage());
                holder.messageLeft.setVisibility(View.VISIBLE);
            }
        }else{
            if(!message.isAdmin()){
                holder.messageRight.setText(message.getMessage());
                holder.messageRight.setVisibility(View.VISIBLE);
            }else{
                holder.messageLeft.setText(message.getMessage());
                holder.messageLeft.setVisibility(View.VISIBLE);
            }
        }
    }

    // Method to get the total number of items in the data set
    @Override
    public int getItemCount() {
        return filteredList.size(); // Return size of filteredList
    }

    // ViewHolder class to hold views for each item in the RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView messageLeft, messageRight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageLeft = itemView.findViewById(R.id.messageLeft);
            messageRight = itemView.findViewById(R.id.messageRight);
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
            for (MessagesModel item : mData) {
                if (item.getMessage().toLowerCase().contains(searchQuery)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter that data set has changed
    }
}
