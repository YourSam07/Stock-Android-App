package com.example.webtech4;

public class AutoCModel {
    private String ticker;
    private String desc;

    public AutoCModel(String ticker, String desc) {
        this.ticker = ticker;
        this.desc = desc;
    }

    public String getTicker() {
        return ticker;
    }

    public String getDesc() {
        return desc;
    }
}
