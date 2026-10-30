package com.levytech;

import java.util.ArrayList;
import java.util.List;

public class Market {

    private String marketName;
    private List<Trader> traders;
    private List<Stall> stalls;

    public Market(String marketName) {
        this.marketName = marketName;
        this.traders = new ArrayList<>();
        this.stalls = new ArrayList<>();
    }

    public String getMarketName() {
        return marketName;
    }

    public void registerTrader(Trader trader) {
        if (trader != null) {
            traders.add(trader);
        }
    }

    public void addStall(Stall stall) {
        if (stall != null) {
            stalls.add(stall);
        }
    }

    public Trader findTraderById(int id) {
        for (Trader trader : traders) {
            if (trader.getId() == id) {
                return trader;
            }
        }
        return null;
    }

    public Stall findStallById(int id) {
        for (Stall stall : stalls) {
            if (stall.getId() == id) {
                return stall;
            }
        }
        return null;
    }

    public boolean assignTraderToStall(int traderId, int stallId) {
        Trader trader = findTraderById(traderId);
        Stall stall = findStallById(stallId);

        if (trader == null || stall == null) {
            return false;
        }

        return stall.assignTrader(trader);
    }

    public void displayTraders() {
        System.out.println("\n--- REGISTERED TRADERS ---");

        for (Trader trader : traders) {
            System.out.println(
                    "ID: " + trader.getId() +
                            " | Name: " + trader.getName() +
                            " | Phone: " + trader.getPhone()
            );
        }
    }

    public void displayStalls() {
        System.out.println("\n--- MARKET STALLS ---");

        for (Stall stall : stalls) {
            System.out.println(
                    "Stall: " + stall.getStallNumber() +
                            " | Location: " + stall.getLocation() +
                            " | Occupied: " + stall.isOccupied()
            );
        }
    }
}
