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
import android.widget.Toast;

import com.example.uecfs.R;
import com.example.uecfs.activity.ActionActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hadi.emojiratingbar.EmojiRatingBar;
import com.hadi.emojiratingbar.RateStatus;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RatingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_IMAGE = "param1";
    private static final String ARG_REMARK = "param2";
    private static final String ARG_LOCATION = "param3";
    private static final String ARG_COORDINATE = "param4";
    private Context mContext;

    // TODO: Rename and change types of parameters
    private String mImage;
    private String mRemark;
    private String mLocation;
    private String mCoordinates;

    public RatingFragment(Context context) {
        // Required empty public constructor
        mContext = context;
    }
    public static RatingFragment newInstance(String image, String remark, String location, String coordinates,Context context) {
        RatingFragment fragment = new RatingFragment(context);
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE, image);
        args.putString(ARG_REMARK, remark);
        args.putString(ARG_LOCATION, location);
        args.getString(ARG_COORDINATE, coordinates);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImage = getArguments().getString(ARG_IMAGE);
            mRemark = getArguments().getString(ARG_REMARK);
            mLocation = getArguments().getString(ARG_LOCATION);
            mCoordinates = getArguments().getString(ARG_COORDINATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EmojiRatingBar ratingBar = view.findViewById(R.id.emoji_rating_bar);
        MaterialButton next = view.findViewById(R.id.next);

        next.setOnClickListener(v -> {
            if(ratingBar.getCurrentRateStatus() == RateStatus.EMPTY){
                Toast.makeText(requireActivity(),"Please select a rating",Toast.LENGTH_LONG).show();
            }else{
                ActionFragment actionFragment = ActionFragment.newInstance(mRemark,mLocation, ratingBar.getCurrentRateStatus().toString(),mImage, mCoordinates,mContext);
                getFragmentManager().beginTransaction().replace(R.id.fragmentHolder,actionFragment).commit();
//                new MaterialAlertDialogBuilder(requireContext())
//                        .setMessage(
//                                "Remark: " + mRemark + "\n" +
//                                "Location: " + mLocation + "\n" +
//                                "Image: " + mImage + "\n" +
//                                "Rating: " + ratingBar.getCurrentRateStatus()
//                        )
//                        .show();
            }
        });
    }
}