package com.levytech.domain;

public final class LevyPlanFactory {
    private LevyPlanFactory() {}

    public static LevyPlan fromRow(String planType, long baseNgwee, long surchargeNgwee, int graceDays) {
        if ("FLAT".equalsIgnoreCase(planType)) {
            return new FlatLevyPlan(baseNgwee, graceDays);
        } 
        else if ("BASE_PLUS_SURCHARGE".equalsIgnoreCase(planType)) {
            return new BasePlusSurchargeLevyPlan(baseNgwee, surchargeNgwee, graceDays);
        } 
        else {
            throw new IllegalArgumentException("Unknown plan type: " + planType);
        }
    }
}