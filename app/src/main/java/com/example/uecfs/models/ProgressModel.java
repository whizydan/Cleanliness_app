package com.example.uecfs.models;

import android.graphics.Color;

import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.List;

public class ProgressModel {

    public List<PieModel> pieModels(float amount){
        int colorBackground = Color.GRAY;
        int color = Color.CYAN;
        List<PieModel> pieModels = new ArrayList<>();

        if(amount > 79.0){
            color = Color.parseColor("#00C853");
        }else if(amount > 59.0 && amount < 80.0){
            color = Color.parseColor("#FFD600");
        }else if(amount > 39 && amount < 60.0){
            color = Color.parseColor("#E3B048");
        }else if(amount > 19.0 && amount < 40.0){
            color = Color.parseColor("#FF6D00");
        } else if (amount < 20.0) {
            color = Color.parseColor("#D50000");
        }
        pieModels.add(new PieModel("", (float) amount,color));
        pieModels.add(new PieModel("", (float) 100 - amount,colorBackground));

        return pieModels;
    }
}
