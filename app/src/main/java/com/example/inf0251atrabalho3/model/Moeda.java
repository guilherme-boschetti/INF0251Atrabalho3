package com.example.inf0251atrabalho3.model;

import java.io.Serializable;

public class Moeda implements Serializable {

    private Integer id;
    private String currency;
    private float bidValue;
    private float askValue;
    private String dateHourInclusion;
    private Long dateMillis;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public float getBidValue() {
        return bidValue;
    }

    public void setBidValue(float bidValue) {
        this.bidValue = bidValue;
    }

    public float getAskValue() {
        return askValue;
    }

    public void setAskValue(float askValue) {
        this.askValue = askValue;
    }

    public String getDateHourInclusion() {
        return dateHourInclusion;
    }

    public void setDateHourInclusion(String dateHourInclusion) {
        this.dateHourInclusion = dateHourInclusion;
    }

    public Long getDateMillis() {
        return dateMillis;
    }

    public void setDateMillis(Long dateMillis) {
        this.dateMillis = dateMillis;
    }
}
