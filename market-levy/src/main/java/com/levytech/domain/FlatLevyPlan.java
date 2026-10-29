package com.levytech.domain;

public class FlatLevy implements LevyPlan{
    private final long rate;
    private final int graceDays;

    public FlatLevyPlan(long rate, int graceDays){
        this.rate= rate;
        this.graceDays = graceDays;
    }
    @override
    public long calculateCharge(){
        return rate;
    }
    @Override
    public int getGraceDays(){
        return graceDays;
    }
}