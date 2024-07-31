package com.example.uecfs.models;

public class HotspotDistributions {
    private String action;
    private double percentage;
    private int drawable;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setPercentage(double percentage) {
        // Format the percentage to 2 decimal places
        this.percentage = Math.round(percentage * 100.0) / 100.0;
    }

    public double getPercentage() {
        return percentage;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }
}
