package com.example.uecfs.dialogs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.uecfs.R;
import com.example.uecfs.utils.TinyDB;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class CoordinatesFinder extends Dialog {
    private FusedLocationProviderClient fusedLocationClient;
    private Activity parent;
    public CoordinatesFinder(@NonNull Context context, Activity activity) {
        super(context);
        parent = activity;
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader_item);
        this.getWindow().getDecorView().setBackground(null);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(parent);
        getLastLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        TinyDB tinyDB = new TinyDB(getContext());
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            tinyDB.putString("loc","Lat:" + latitude + " Long:" + longitude);
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Unable to get location", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }
                });
    }
}
