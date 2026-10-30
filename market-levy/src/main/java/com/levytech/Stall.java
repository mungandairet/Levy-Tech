package com.levytech;

public class Stall {

    private int id;
    private String stallNumber;
    private String location;
    private double monthlyLevy;
    private Trader assignedTrader;

    public Stall(int id, String stallNumber,
                 String location, double monthlyLevy) {
        this.id = id;
        this.stallNumber = stallNumber;
        this.location = location;
        this.monthlyLevy = monthlyLevy;
        this.assignedTrader = null;
    }

    public int getId() {
        return id;
    }

    public String getStallNumber() {
        return stallNumber;
    }

    public String getLocation() {
        return location;
    }

    public double getMonthlyLevy() {
        return monthlyLevy;
    }

    public Trader getAssignedTrader() {
        return assignedTrader;
    }

    public boolean assignTrader(Trader trader) {
        if (trader == null || assignedTrader != null) {
            return false;
        }

        assignedTrader = trader;
        return true;
    }

    public void removeTrader() {
        assignedTrader = null;
    }

    public boolean isOccupied() {
        return assignedTrader != null;
    }
}
