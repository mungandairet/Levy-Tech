package com.levytech.domain;

public class TieredLevyPlan implements LevyPlan{
    private final long base;
    private final long surcharge;
    private final int graceDays;

    public TieredLevyPlan(long base, long surcharge, int graceDays){
        this.base = base;
        this.surcharge = surcharge;
        this.graceDays = graceDays;
    }
    @Override
    public long calculateCharge(){
        return base + surcharge;
    }
    @Override
    public int getGraceDays(){
        return graceDays;
    }
}
    
