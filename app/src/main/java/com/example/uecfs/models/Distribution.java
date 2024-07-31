package com.example.uecfs.models;

public class Distribution {
    private String location, status;
    double percentage;

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        // Format the percentage to 2 decimal places
        this.percentage = Math.round(percentage * 100.0) / 100.0;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

