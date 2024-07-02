package com.example.webtech4;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

public class CustomToast {

    public CustomToast(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayoutCompat layout = (LinearLayoutCompat) inflater.inflate(R.layout.custom_toast, null);
        TextView textView = layout.findViewById(R.id.message);

        layout.setBackground(AppCompatResources.getDrawable(context, R.drawable.custom_toast_bg));


        textView.setText(message);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}