package com.example.inf0251atrabalho3.repository;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Currency {

    @SerializedName("bid")
    @Expose
    private float bid;

    @SerializedName("ask")
    @Expose
    private float ask;

    public float getBid() {
        return bid;
    }

    public void setBid(float bid) {
        this.bid = bid;
    }

    public float getAsk() {
        return ask;
    }

    public void setAsk(float ask) {
        this.ask = ask;
    }
}
