package com.example.webtech4;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.json.JSONException;

public class HistChartTab extends Fragment {

    WebView histChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hist_chart_tab, container, false);
//        histChart = view.findViewById(R.id.histChartTab);
//        initializeWebView("file:///android_asset/hourchart.html");
        return view;
    }
//
//    private void initializeWebView(String url) {
//        WebSettings webSettings = histChart.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        histChart.clearCache(true);
//        try {
////            ChartData data = new ChartData(getIntent().getStringExtra("ticker"), quoteData.getString("t"), quoteData.getDouble("dp"));
////            histChart.addJavascriptInterface(data, "ChartData");
////            histChart.loadUrl(filename);
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
