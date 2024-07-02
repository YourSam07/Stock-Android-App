package com.example.webtech4;

import android.webkit.JavascriptInterface;
public class ChartData {
    String ticker;
    String todt;
    Double dp;

    public ChartData(String ticker, String todt, Double dp) {
        this.ticker = ticker;
        this.todt = todt;
        this.dp = dp;
    }

    @JavascriptInterface
    public String getTicker() {
        return ticker;
    }

    @JavascriptInterface
    public String getTodt(){
        return todt;
    }

    @JavascriptInterface
    public Double getDp(){
        return dp;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
}
