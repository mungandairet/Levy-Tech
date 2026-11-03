package com.levytech.domain;

import java.time.LocalDate;

public class Trader {

    private int id;
    private String fullName;
    private String phone;
    private String productCategory;
    private int planId;
    private long balanceNgwee;
    private long arrearsNgwee;
    private LocalDate arrearsSince;
    private Integer stallId;
    private boolean active;

    public Trader(int id, String fullName, String phone,
                  String productCategory, int planId,
                  long balanceNgwee, long arrearsNgwee,
                  LocalDate arrearsSince, Integer stallId,
                  boolean active) {

        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.productCategory = productCategory;
        this.planId = planId;
        this.balanceNgwee = balanceNgwee;
        this.arrearsNgwee = arrearsNgwee;
        this.arrearsSince = arrearsSince;
        this.stallId = stallId;
        this.active = active;
    }

    // Getters
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getProductCategory() { return productCategory; }
    public int getPlanId() { return planId; }
    public long getBalanceNgwee() { return balanceNgwee; }
    public long getArrearsNgwee() { return arrearsNgwee; }
    public LocalDate getArrearsSince() { return arrearsSince; }
    public Integer getStallId() { return stallId; }
    public boolean isActive() { return active; }

    // Business Logic

    public void credit(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        this.balanceNgwee = Math.addExact(this.balanceNgwee, amount);
    }

    public void debit(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (amount > balanceNgwee) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        this.balanceNgwee -= amount;
    }

    public void recordArrears(long amount, LocalDate cycleDate) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (cycleDate == null) {
            throw new IllegalArgumentException("Cycle date cannot be null");
        }

        long updatedArrears = Math.addExact(this.arrearsNgwee, amount);

        if (this.arrearsSince == null) {
            this.arrearsSince = cycleDate;
        }

        this.arrearsNgwee = updatedArrears;
    }

    public void reduceArrears(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (amount > this.arrearsNgwee) {
            throw new IllegalArgumentException(
                "Cannot reduce arrears by more than the outstanding amount"
            );
        }

        this.arrearsNgwee -= amount;

        if (this.arrearsNgwee == 0) {
            this.arrearsSince = null;
        }
    }

    public void assignStall(Integer newStallId) {
        this.stallId = newStallId;
    }

    public void deactivate() {
        this.active = false;
        this.stallId = null;
    }
}
