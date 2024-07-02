package com.example.webtech4;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomNewsDialog extends Dialog {
    // Constructor
    private String title;
    private String headline;
    private String date;
    private String summary;
    private String url;
    private Context ctx;

    public CustomNewsDialog(Context context, String title, String date, String headline, String summary, String url) {
        super(context);
        this.ctx = context;
        this.date = date;
        this.title = title;
        this.headline = headline;
        this.summary = summary;
        this.url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set custom dialog layout
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.news_modal);

        TextView t = findViewById(R.id.newsModalTitle);
        TextView h = findViewById(R.id.newsModalHeadline);
        TextView d = findViewById(R.id.newsModalDate);
        TextView s = findViewById(R.id.newsModalSummary);
        ImageView c = findViewById(R.id.chrome);
        ImageView x = findViewById(R.id.twitter);
        ImageView fb = findViewById(R.id.fb);
        t.setText(title);
        h.setText(headline);
        d.setText(date);
        s.setText(summary);
        c.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            ctx.startActivity(intent);
        });
        x.setOnClickListener(v -> {
            String xUrl = "https://twitter.com/intent/tweet?text="+String.valueOf(headline)+" "+String.valueOf(url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(xUrl));
            ctx.startActivity(intent);
        });
        fb.setOnClickListener(v -> {
            String fbUrl = "https://www.facebook.com/sharer/sharer.php?u="+url+"&amp;src=sdkpreparse";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(fbUrl));
            ctx.startActivity(intent);
        });
    }

}