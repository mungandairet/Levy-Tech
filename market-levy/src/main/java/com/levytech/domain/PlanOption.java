package com.levytech.domain;

public record PlanOption(
        int id,
        String name,
        String planType,
        long rateNgwee,
        long surchargeNgwee,
        int graceDays
) {
}
