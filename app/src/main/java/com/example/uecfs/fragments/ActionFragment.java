package com.example.uecfs.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.uecfs.R;
import com.example.uecfs.activity.ActionActivity;
import com.example.uecfs.activity.GiveFeedbackActivity;
import com.example.uecfs.activity.HomePageActivity;
import com.example.uecfs.dialogs.Loader;
import com.example.uecfs.models.FeedbackModel;
import com.example.uecfs.utils.TinyDB;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_IMAGE = "param1";
    private static final String ARG_REMARK = "param2";
    private static final String ARG_LOCATION = "param3";
    private static final String ARG_RATING = "param4";
    private static final String ARG_COORDINATES = "param5";

    // TODO: Rename and change types of parameters
    private String mImage;
    private String mLocation;
    private String mRemark;
    private String mRating;
    private String action;
    private String mCoordinates;
    private Context mContext;

    public ActionFragment(Context context) {
        // Required empty public constructor
        mContext = context;
    }

    // TODO: Rename and change types and number of parameters
    public static ActionFragment newInstance(String remark, String location, String rating, String image, String coordinates, Context context) {
        ActionFragment fragment = new ActionFragment(context);
        Bundle args = new Bundle();
        args.putString(ARG_REMARK, remark);
        args.putString(ARG_IMAGE, image);
        args.putString(ARG_LOCATION, location);
        args.putString(ARG_RATING, rating);
        args.putString(ARG_COORDINATES, coordinates);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRemark = getArguments().getString(ARG_REMARK);
            mLocation = getArguments().getString(ARG_LOCATION);
            mImage = getArguments().getString(ARG_IMAGE);
            mRating = getArguments().getString(ARG_RATING);
            mCoordinates = getArguments().getString(ARG_COORDINATES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        action = "";
        ImageButton wipe = view.findViewById(R.id.wipe);
        ImageButton mop = view.findViewById(R.id.mop);
        ImageButton vacuum = view.findViewById(R.id.vacuum);
        ImageButton sweep = view.findViewById(R.id.sweep);
        ImageButton wash = view.findViewById(R.id.wash);
        ImageButton flowering = view.findViewById(R.id.flower);
        ImageButton spray = view.findViewById(R.id.spray);
        ImageButton disinfect = view.findViewById(R.id.disinfect);
        ImageButton none = view.findViewById(R.id.none);

        wipe.setOnClickListener(v -> {
            next("Wiping");
        });
        mop.setOnClickListener(v -> {
            next("Mopping");
        });
        vacuum.setOnClickListener(v -> {
            next("Vacuum");
        });
        sweep.setOnClickListener(v -> {
            next("Sweeping");
        });
        wash.setOnClickListener(v -> {
            next("Hand Washing");
        });
        flowering.setOnClickListener(v -> {
            next("Flowering");
        });
        spray.setOnClickListener(v -> {
            next("Spraying");
        });
        disinfect.setOnClickListener(v -> {
            next("Disinfecting");
        });
        none.setOnClickListener(v -> {
            next("None");
        });
    }

    private void next(String value){
        String id = String.valueOf(System.currentTimeMillis());
        TinyDB tinyDB = new TinyDB(requireActivity());
        action = value;
        FeedbackModel feedback = new FeedbackModel();
        feedback.setStatus("Pending");
        feedback.setId(id);
        feedback.setFeedback(mRemark);
        feedback.setUsersId(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        feedback.setPhoto(mImage);
        feedback.setAction(action);
        feedback.setLocation(mLocation);
        feedback.setCleanliness(mRating);
        feedback.setCoordinates(tinyDB.getString("loc"));
        feedback.setTitle(mLocation + ": " + action + " " + mRating);

        GiveFeedbackActivity feedbackActivity = new GiveFeedbackActivity();
        Loader loader = new Loader(requireActivity());
        loader.show();

        FirebaseDatabase.getInstance().getReference().child("feeds").child(feedback.getId())
                .setValue(feedback)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        loader.dismiss();
                        Toast.makeText(requireActivity(),"Feedback has been received",Toast.LENGTH_LONG).show();
                        feedbackActivity.saveNotification();
                        //getFragmentManager().beginTransaction().replace(R.id.fragmentHolder,new FeedbackFragment()).commit();

                        startActivity(new Intent(mContext.getApplicationContext(), HomePageActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loader.dismiss();
                        new MaterialAlertDialogBuilder(requireActivity())
                                .setTitle("Could not save feedback")
                                .setMessage(e.getMessage())
                                .show();
                    }
                })
        ;
    }
}