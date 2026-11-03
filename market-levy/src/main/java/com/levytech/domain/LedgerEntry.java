package com.levytech.domain;

import java.time.LocalDate;

public class LedgerEntry {

    public enum Type {
        CREDIT,
        DEDUCTION,
        ARREARS
    }

    private final int id;
    private final int traderId;
    private final Type type;
    private final long amountNgwee;
    private final long balanceAfterNgwee;
    private final String cycle;
    private final LocalDate entryDate;
    private final String description;

    public LedgerEntry(
            int id,
            int traderId,
            Type type,
            long amountNgwee,
            long balanceAfterNgwee,
            String cycle,
            LocalDate entryDate,
            String description) {

        if (id < 0) {
            throw new IllegalArgumentException("ID cannot be negative");
        }
        if (traderId <= 0) {
            throw new IllegalArgumentException("Trader ID must be positive");
        }
        if (type == null) {
            throw new IllegalArgumentException("Ledger type cannot be null");
        }
        if (amountNgwee < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (balanceAfterNgwee < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        if (entryDate == null) {
            throw new IllegalArgumentException("Entry date cannot be null");
        }

        this.id = id;
        this.traderId = traderId;
        this.type = type;
        this.amountNgwee = amountNgwee;
        this.balanceAfterNgwee = balanceAfterNgwee;
        this.cycle = cycle;
        this.entryDate = entryDate;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public int getTraderId() {
        return traderId;
    }

    public Type getType() {
        return type;
    }

    public long getAmountNgwee() {
        return amountNgwee;
    }

    public long getBalanceAfterNgwee() {
        return balanceAfterNgwee;
    }

    public String getCycle() {
        return cycle;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public String getDescription() {
        return description;
    }
}