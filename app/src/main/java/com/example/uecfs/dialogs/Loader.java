package com.example.uecfs.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.example.uecfs.R;

public class Loader extends Dialog {
    public Loader(@NonNull Context context) {
        super(context);
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader_item);
        getWindow().getDecorView().setBackground(null);
    }
}
