package com.example.uecfs.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.example.uecfs.R;
import com.example.uecfs.dialogs.Loader;
import com.example.uecfs.models.IndividualDisasterModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textview.MaterialTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.noties.markwon.Markwon;

public class Utility {
    Context mContext;
    public Utility(Context context){
        this.mContext = context;
    }

    public void render(){
        final Markwon markwon = Markwon.create(mContext);
        final Spanned markdown = markwon.toMarkdown(readTextFile(mContext, R.raw.changelog));

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.changelog);

        MaterialTextView changelog = bottomSheetDialog.findViewById(R.id.changelog);

        changelog.setText(markdown);

        bottomSheetDialog.show();
    }

    public static String readTextFile(Context context, int resId) {
        InputStream inputStream = context.getResources().openRawResource(resId);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }
}
