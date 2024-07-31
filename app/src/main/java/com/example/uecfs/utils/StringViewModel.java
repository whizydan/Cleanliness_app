package com.example.uecfs.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StringViewModel extends ViewModel {
    private final MutableLiveData<String> mutableString = new MutableLiveData<>();

    public LiveData<String> getString() {
        return mutableString;
    }

    public void setString(String value) {
        mutableString.setValue(value);
    }
}
