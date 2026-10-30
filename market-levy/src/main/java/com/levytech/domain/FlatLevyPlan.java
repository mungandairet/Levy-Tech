package com.levytech.domain;

public class FlatLevyPlan implements LevyPlan{
    private final long rateNgwee;
    private final int graceDays;

    public FlatLevyPlan(long rateNgwee, int graceDays){
        if(rateNgwee < 0 ){
            throw new IllegalArgumentException(" Rate cannot be negative");
        }
        this.rateNgwee= rateNgwee;
        this.graceDays = graceDays;
    }
    @Override
    public long calculateChargeNgwee(){
        return rateNgwee;
    }
    @Override
    public int getGraceDays(){
        return graceDays;
    }
}