package com.levytech.domain;

import java.time.LocalDate;

public record ArrearsReport(
        int traderId,
        String traderName,
        Integer stallId,
        LocalDate arrearsSince
) {
}