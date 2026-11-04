package com.levytech.domain;

public record PlanOption(
        int id,
        String name,
        String planType,
        long baseNgwee,
        long surchargeNgwee,
        int graceDays
) {
}
