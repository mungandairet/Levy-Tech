package com.levytech.domain;

public class FlatLevyPlan implements LevyPlan{
    private final long rate;
    private final int graceDays;

    public FlatLevyPlan(long rate, int graceDays){
        if(rate < 0 ){
            throw new IllegalArgumentException(" Rate cannot be negative");
        }
        this.rate= rate;
        this.graceDays = graceDays;
    }
    @Override
    public long calculateCharge(){
        return rate;
    }
    @Override
    public int getGraceDays(){
        return graceDays;
    }
}