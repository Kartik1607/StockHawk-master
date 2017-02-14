package com.sam_chordas.android.stockhawk.rest;

/**
 * Created by Kartik Sharma on 07/12/16.
 */
public class SingleStockData {

    private String symbol, bid_price, percent_change, open, low, high, mkt_cap, pe_ratio, div_yield;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getBid_price() {
        return bid_price;
    }

    public void setBid_price(String bid_price) {
        this.bid_price = bid_price;
    }

    public String getPercent_change() {
        return percent_change;
    }

    public void setPercent_change(String percent_change) {
        this.percent_change = percent_change;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getMkt_cap() {
        return mkt_cap;
    }

    public void setMkt_cap(String mkt_cap) {
        this.mkt_cap = mkt_cap;
    }

    public String getPe_ratio() {
        return pe_ratio;
    }

    public void setPe_ratio(String pe_ratio) {
        this.pe_ratio = pe_ratio;
    }

    public String getDiv_yield() {
        return div_yield;
    }

    public void setDiv_yield(String div_yield) {
        this.div_yield = div_yield;
    }
}
