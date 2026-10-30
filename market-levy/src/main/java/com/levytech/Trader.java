package com.levytech;

public class Trader {

    private int id;
    private String name;
    private String phone;
    private String category;
    private int planId;
    private int prepaidBalanceNgwee;

    public Trader(int id, String name, String phone,
                  String category, int planId) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.category = category;
        this.planId = planId;
        this.prepaidBalanceNgwee = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getCategory() {
        return category;
    }

    public int getPlanId() {
        return planId;
    }

    public int getPrepaidBalanceNgwee() {
        return prepaidBalanceNgwee;
    }

    public void addPrepaidBalance(int amount) {
        if (amount > 0) {
            prepaidBalanceNgwee += amount;
        }
    }

    public boolean deductPrepaidBalance(int amount) {
        if (amount <= 0 || amount > prepaidBalanceNgwee) {
            return false;
        }

        prepaidBalanceNgwee -= amount;
        return true;
    }
}
