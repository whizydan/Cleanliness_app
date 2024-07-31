package com.example.uecfs.models;

import android.graphics.drawable.Drawable;

public class ActionsModel {
    private String action;
    private int icon;

    public ActionsModel(String action, int icon){
        this.icon = icon;
        this.action = action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
