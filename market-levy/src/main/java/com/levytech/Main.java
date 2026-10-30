package com.levytech;

public class Main {

    public static void main(String[] args) {

        // Create a market
        Market market = new Market("LevyTech Central Market");

        // Register traders
        Trader trader1 = new Trader(
                1, "John Banda", "0977000001", "Food", 1
        );

        Trader trader2 = new Trader(
                2, "Mary Phiri", "0966000002", "Clothing", 2
        );

        market.registerTrader(trader1);
        market.registerTrader(trader2);

        // Add market stalls
        Stall stall1 = new Stall(
                101, "A01", "Block A", 500.00
        );

        Stall stall2 = new Stall(
                102, "B01", "Block B", 600.00
        );

        market.addStall(stall1);
        market.addStall(stall2);

        // Assign traders to stalls
        market.assignTraderToStall(1, 101);
        market.assignTraderToStall(2, 102);

        // Display market information
        System.out.println("WELCOME TO " + market.getMarketName());

        market.displayTraders();
        market.displayStalls();
    }
}
