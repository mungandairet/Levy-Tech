package com.levytech.domain;

import java.time.LocalDate;

public record Payment(
        int id,
        int traderId,
        long amountNgwee,
        LocalDate paidOn,
        String reference
) {
    public Payment {
        if (id < 0) {
            throw new IllegalArgumentException("Payment ID cannot be negative");
        }

        if (traderId <= 0) {
            throw new IllegalArgumentException("Trader ID must be positive");
        }

        if (amountNgwee <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        if (paidOn == null) {
            throw new IllegalArgumentException("Payment date is required");
        }
    }
}