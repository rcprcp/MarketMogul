package com.cottagecoders.marketmogul;

/**
 * Created by Marij on 4/20/2016.
 */
public class Security {
    String ticker;
    String time;
    double currPrice;
    double change;
    double highPrice;
    double lowPrice;
    double volume;
    boolean isDelayed;
    boolean isOnStatus;

    public Security() {
        this.ticker = "";
        this.time = "";
        this.currPrice = 0.0;
        this.change = 0.0;
        this.highPrice = 0.0;
        this.lowPrice = 0.0;
        this.volume = 0.0;
        this.isDelayed = false;
        this.isOnStatus = false;

    }

    public Security(String ticker, String time,
                    double currPrice, double change, double highPrice, double lowPrice, double volume,
                    boolean isDelayed, boolean isOnStatus) {

        this.ticker = ticker;
        this.time = time;
        this.change = change;
        this.currPrice = currPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.volume = volume;
        this.isDelayed = isDelayed;
        this.isOnStatus = isOnStatus;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getCurrPrice() {
        return currPrice;
    }

    public void setCurrPrice(double currPrice) {
        this.currPrice = currPrice;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public boolean isDelayed() {
        return isDelayed;
    }

    public void setDelayed(boolean delayed) {
        isDelayed = delayed;
    }

    public boolean isOnStatus() {
        return isOnStatus;
    }

    public void setOnStatus(boolean onStatus) {
        isOnStatus = onStatus;
    }
}

