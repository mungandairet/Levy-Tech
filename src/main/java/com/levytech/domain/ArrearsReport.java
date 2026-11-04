package com.levytech.domain;

public record ArrearsReport(
    int traderId,
     String traderName,
     Integer stallId,
     long arrearsSince) {
}
