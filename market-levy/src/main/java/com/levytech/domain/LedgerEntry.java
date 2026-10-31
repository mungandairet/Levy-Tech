package com.levytech.domain;

import java.time.LocalDate;

public record LedgerEntry(
        int id,
        int traderId,
        Type type,
        long amountNgwee,
        long balanceAfterNgwee,
        String cycle,
        LocalDate entryDate,
        String description
) {
    public enum Type {
        CREDIT,
        DEDUCTION,
        ARREARS
    }

    public LedgerEntry {
        if (id < 0) {
            throw new IllegalArgumentException("Ledger entry ID cannot be negative");
        }

        if (traderId <= 0) {
            throw new IllegalArgumentException("Trader ID must be positive");
        }

        if (type == null) {
            throw new IllegalArgumentException("Transaction type is required");
        }

        if (amountNgwee <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }

        if (balanceAfterNgwee < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }

        if (entryDate == null) {
            throw new IllegalArgumentException("Entry date is required");
        }
    }
}