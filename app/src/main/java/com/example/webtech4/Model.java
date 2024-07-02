package com.example.webtech4;
import java.text.DecimalFormat;

public class Model {
    private String name;
    private String ticker;
    private double c;
    private double d;
    private double dp;
    private boolean upDown;
    private int type;

    public Model(String name, String ticker, double c, double d, double dp) {
        this.name = name;
        this.ticker = ticker;
        this.c = c;
        this.d = d;
        this.dp = dp;
        this.upDown = this.dp > 0;
        this.type = 1;
    }

    public String getName() {
        return name;
    }

    public String getTicker() {
        return ticker;
    }

    public double getC() {
        return c;
    }

    public double getD() {
        return d;
    }

    public double getDp() {
        return dp;
    }

    public boolean getUpDown(){
        return upDown;
    }
    public int getType() { return type;}

    public static double formatDouble(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(value));
    }
}
