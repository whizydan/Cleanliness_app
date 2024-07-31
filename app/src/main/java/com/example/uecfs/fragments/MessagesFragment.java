package com.example.uecfs.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uecfs.activity.MessageActivity;
import com.example.uecfs.R;
import com.example.uecfs.adapters.AdminChatAdapter;
import com.example.uecfs.utils.TinyDB;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessagesFragment extends Fragment {

    // TODO: Reaname parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MessagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessagesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessagesFragment newInstance(String param1, String param2) {
        MessagesFragment fragment = new MessagesFragment();
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
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton message = view.findViewById(R.id.message);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        MaterialButton chatMessage = view.findViewById(R.id.chatMessage);
        TinyDB tinyDB = new TinyDB(requireActivity());
        boolean isAdmin = tinyDB.getBoolean("admin");
        ArrayList<String> chatIds = new ArrayList<>();
        TextView hiddenText1 = view.findViewById(R.id.textView4);
        TextView hiddenText2 = view.findViewById(R.id.textView5);
        ImageView hiddenImage = view.findViewById(R.id.imageView);

        if(isAdmin){
            chatMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            hiddenImage.setVisibility(View.GONE);
            hiddenText1.setVisibility(View.GONE);
            hiddenText2.setVisibility(View.GONE);
            FirebaseDatabase.getInstance().getReference().child("chats").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatIds.clear();
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String chatId = dataSnapshot.getKey();
                        chatIds.add(chatId);
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false));
                    recyclerView.setAdapter(new AdminChatAdapter(requireActivity(),chatIds));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireActivity(),"Failed loading chats",Toast.LENGTH_LONG).show();
                }
            });
        }

        chatMessage.setOnClickListener(v->{
            startActivity(new Intent(requireActivity(), MessageActivity.class));
        });
    }
}