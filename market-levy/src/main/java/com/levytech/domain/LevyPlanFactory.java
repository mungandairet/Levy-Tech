package com.levytech.domain;

public final class LevyPlanFactory{
    private LevyPlanFactory(){}

    public static LevyPlan fromRow(String planType, long base, long surcharge, int graceDays){
        if("FLAT".equalsIgnoreCase(planType)){
            return new FlatLevyPlan(base, graceDays);
        } else if("TIERED".equalsIgnoreCase(planType)){
            return new TieredLevyPlan(base, surcharge, graceDays);
        } else {
            throw new IllegalArgumentException("Unknown plan type: " + planType);
        }
    }
}