package com.example.uecfs.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.uecfs.R;
import com.example.uecfs.activity.HotspotDetailActivity;
import com.example.uecfs.adapters.DistributionAdapter;
import com.example.uecfs.models.Distribution;
import com.example.uecfs.models.FeedbackModel;
import com.example.uecfs.models.ProgressModel;
import com.example.uecfs.utils.Utility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import app.futured.donut.DonutProgressView;
import app.futured.donut.DonutSection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HotspotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HotspotFragment extends Fragment {
    DistributionAdapter adapter;


    public HotspotFragment() {
        // Required empty public constructor
    }

    public static HotspotFragment newInstance(String param1, String param2) {
        return new HotspotFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hotspot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<FeedbackModel> feedbackModelArrayList = new ArrayList<>();
        String[] locationsArray = getResources().getStringArray(R.array.locations);
        List<String> locations = new ArrayList<>(Arrays.asList(locationsArray));
        LinearLayout section = view.findViewById(R.id.section);
        ArrayList<Distribution> distributionArrayList = new ArrayList<>();
        PieChart progressView = view.findViewById(R.id.donut_view);
        LinearLayout sectionKey = view.findViewById(R.id.sectionKey);
        ConstraintLayout layoutHide = view.findViewById(R.id.layoutHide);
        MaterialTextView percentageValue = view.findViewById(R.id.percentage);
        ImageButton refresh = view.findViewById(R.id.refresh);
        final int[] count = {0};
        ArrayList<PieModel> donutSections = new ArrayList<>();

        if(this.isAdded()){
            //prevent crashes
            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            TextInputLayout search = view.findViewById(R.id.search);

            refresh.setOnClickListener(v -> {
                progressView.startAnimation();
            });

            search.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(adapter != null){
                        adapter.filter(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            FirebaseDatabase.getInstance().getReference().child("feeds").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<String> coordinates = new ArrayList<>();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        FeedbackModel feedback = dataSnapshot.getValue(FeedbackModel.class);
                        feedbackModelArrayList.add(feedback);

                        if(feedback.getCoordinates() != null){
//                            for(String location : locations){
//                                if(!location.contains(feedback.getCoordinates())){
//                                    coordinates.add(location);
//                                }
//                            }
                            if(!locations.contains(feedback.getCoordinates())){
                                coordinates.add(feedback.getCoordinates());
                            }
                        }


                    }locations.addAll(coordinates);

                    for(String locationName: locations){
                        int totalFeedbacks = feedbackModelArrayList.size();
                        int totalFeedbacksForThisLocation = 0;
                        for(FeedbackModel feedbackModel: feedbackModelArrayList){
                            if(feedbackModel.getLocation().equals(locationName)){
                                totalFeedbacksForThisLocation++;
                            }
                        }
                        Distribution distribution = new Distribution();
                        distribution.setLocation(locationName);
                        float percentage = ((float) totalFeedbacksForThisLocation /totalFeedbacks) * 100;
                        distribution.setPercentage(percentage);
                        if(percentage > 0.1){
                            distributionArrayList.add(distribution);
                        }

                    }

                    Collections.sort(distributionArrayList, new Comparator<Distribution>() {
                        @Override
                        public int compare(Distribution d1, Distribution d2) {
                            return Double.compare(d2.getPercentage(), d1.getPercentage());
                        }
                    });

                    section.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.VISIBLE);
                    sectionKey.setVisibility(View.VISIBLE);
                    layoutHide.setVisibility(View.VISIBLE);
                    List<Distribution> newList = distributionArrayList.subList(0, Math.min(distributionArrayList.size(), 10));

                    int total = feedbackModelArrayList.size();
                    int incomplete = 0;

                    for (FeedbackModel feedback: feedbackModelArrayList) {
                        if(!feedback.getStatus().equals("Reviewed")){
                            incomplete++;
                        }
                    }

                    float percentComplete = (total - incomplete/total) * 100;


                    PieModel donutSection = new PieModel("", percentComplete,Color.CYAN);
                    donutSections.add(donutSection);

                    double percentage = 100 - ((donutSections.size()/31.0) * 100);
                    percentageValue.setText(String.valueOf(percentage).substring(0,4) + " %");
                    adapter = new DistributionAdapter(requireActivity(),newList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false));;
                    new ProgressModel().pieModels((float) percentage).forEach(new Consumer<PieModel>() {
                        @Override
                        public void accept(PieModel pieModel) {
                            progressView.addPieSlice(pieModel);
                        }
                    });
                    progressView.startAnimation();
                    recyclerView.setAdapter(adapter);

                    adapter.setClickListener(new DistributionAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(requireActivity(), HotspotDetailActivity.class);
                            intent.putExtra("location",newList.get(position).getLocation());
                            Toast.makeText(requireActivity(),"" + newList.get(position).getLocation(),Toast.LENGTH_LONG).show();
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}

