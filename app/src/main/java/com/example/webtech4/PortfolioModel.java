package com.example.webtech4;

import java.text.DecimalFormat;

public class PortfolioModel {
    private String ticker;
    private int quantity;
    private double c;
    private double change;
    private double totCost;
    private double changePer;
    private boolean upDown;
    private int type;

    public PortfolioModel(String ticker, int quantity, double c, double change, double totCost) {
        this.ticker = ticker;
        this.quantity = quantity;
        this.c = c;
        this.change = change;
        this.totCost = totCost;
        this.changePer = (this.change / this.totCost) / 100;
        this.upDown = this.change > 0;
        this.type = 0;
    }

    public String getTicker() {
        return ticker;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getC() {
        return c;
    }

    public double getChange() {
        return change;
    }

    public double getTotCost() { return totCost;}

    public double getChangePer() {
        return changePer;
    }

    public boolean getUpDown(){
        return upDown;
    }

    public int getType() { return type; }

    public static double formatDouble(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(value));
    }
}
