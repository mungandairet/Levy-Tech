package com.levytech.domain;

public class BasePlusSurchargeLevyPlan implements LevyPlan {

    private final long baseNgwee;
    private final long surchargeNgwee;
    private final int graceDays;

    public BasePlusSurchargeLevyPlan(long baseNgwee, long surchargeNgwee, int graceDays) {
        if (baseNgwee < 0 || surchargeNgwee < 0) {
            throw new IllegalArgumentException("Amounts cannot be negative");
        }

        if (graceDays < 0) {
            throw new IllegalArgumentException("Grace days cannot be negative");
        }

        this.baseNgwee = baseNgwee;
        this.surchargeNgwee = surchargeNgwee;
        this.graceDays = graceDays;
    }

    @Override
    public long calculateChargeNgwee() {
        return Math.addExact(baseNgwee, surchargeNgwee);
    }

    @Override
    public int getGraceDays() {
        return graceDays;
    }
}
