package com.example.uecfs.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uecfs.activity.MessageActivity;
import com.example.uecfs.R;
import com.example.uecfs.models.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminChatAdapter extends RecyclerView.Adapter<AdminChatAdapter.ViewHolder> {
    private List<String> mData; // List to hold data for the adapter
    private List<String> filteredList; // List to hold filtered data
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;

    // Constructor to initialize the adapter with data
    public AdminChatAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.filteredList = new ArrayList<>(data); // Initialize filteredList with mData
        this.mContext = context;

    }

    // Method to inflate the layout file and create ViewHolder objects
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    // Method to bind data to ViewHolder objects
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String chatId = filteredList.get(position);
        holder.message.setVisibility(View.GONE);
        final UserModel[] user = {new UserModel()};

        FirebaseDatabase.getInstance().getReference("users").child(chatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user[0] = snapshot.getValue(UserModel.class);
                holder.title.setText(user[0].getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.title.setText(chatId + "\nCould not resolve Name! user may have been deleted");
            }
        });

        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(mContext, MessageActivity.class);
            intent.putExtra("id",chatId);
            intent.putExtra("name", user[0].getName());
            mContext.startActivity(intent);
        });
    }

    // Method to get the total number of items in the data set
    @Override
    public int getItemCount() {
        return filteredList.size(); // Return size of filteredList
    }

    // ViewHolder class to hold views for each item in the RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            message = itemView.findViewById(R.id.message);
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
            for (String item : mData) {
                if (item.toLowerCase().contains(searchQuery)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter that data set has changed
    }
}
