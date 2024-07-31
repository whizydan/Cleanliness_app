package com.example.uecfs.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.uecfs.activity.ContactHotlineActivity;
import com.example.uecfs.activity.NotificationActivity;
import com.example.uecfs.activity.ProfileActivity;
import com.example.uecfs.R;
import com.example.uecfs.activity.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton logout = view.findViewById(R.id.logout);
        MaterialButton hotline = view.findViewById(R.id.hotline);
        MaterialButton notification = view.findViewById(R.id.notification);
        TextView username = view.findViewById(R.id.username);
        MaterialButton editProfile = view.findViewById(R.id.editProfile);

        username.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());

        logout.setOnClickListener(v ->{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(requireActivity(), MainActivity.class));
            requireActivity().finish();
        });
        hotline.setOnClickListener(v->{
            startActivity(new Intent(requireActivity(), ContactHotlineActivity.class));
        });
        notification.setOnClickListener(v->{
            startActivity(new Intent(requireActivity(), NotificationActivity.class));
        });
        editProfile.setOnClickListener(v->{
            startActivity(new Intent(requireActivity(), ProfileActivity.class));
        });
    }
}