public class MarketManualTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {

        System.out.println("================================");
        System.out.println("   MARKET SYSTEM TESTS");
        System.out.println("================================");

        testTrader();
        testStall();
        testMarket();

        System.out.println("\n================================");
        System.out.println("TEST SUMMARY");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("================================");
    }

    private static void check(String testName, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + testName);
        } else {
            failed++;
            System.out.println("[FAIL] " + testName);
        }
    }

    private static void testTrader() {
        System.out.println("\n--- TRADER TESTS ---");

        Trader trader = new Trader(
                1, "John Banda", "0977000000", "GENERAL", 1
        );

        check("Trader ID", trader.getId() == 1);
        check("Trader name", trader.getName().equals("John Banda"));
        check("Trader starts active", trader.isActive());
        check("Initial prepaid balance is zero",
                trader.getPrepaidBalanceNgwee() == 0);

        trader.credit(10000);

        check("Credit increases balance",
                trader.getPrepaidBalanceNgwee() == 10000);

        trader.debit(3000);

        check("Debit reduces balance",
                trader.getPrepaidBalanceNgwee() == 7000);

        trader.recordArrears(2000);

        check("Arrears recorded",
                trader.getArrearsNgwee() == 2000);

        trader.reduceArrears(500);

        check("Arrears reduced",
                trader.getArrearsNgwee() == 1500);

        trader.deactivate();

        check("Trader deactivated", !trader.isActive());

        try {
            trader.debit(100000);
            check("Insufficient balance rejected", false);
        } catch (IllegalStateException e) {
            check("Insufficient balance rejected", true);
        }
    }

    private static void testStall() {
        System.out.println("\n--- STALL TESTS ---");

        Stall stall = new Stall(1, "A-01");

        check("Stall ID", stall.getId() == 1);
        check("Stall number",
                stall.getStallNumber().equals("A-01"));
        check("Stall starts vacant", !stall.isOccupied());
        check("No trader initially assigned",
                stall.getTraderId() == -1);

        stall.occupy(1);

        check("Stall becomes occupied", stall.isOccupied());
        check("Trader assigned correctly",
                stall.getTraderId() == 1);

        try {
            stall.occupy(2);
            check("Occupied stall rejects another trader", false);
        } catch (IllegalStateException e) {
            check("Occupied stall rejects another trader", true);
        }

        stall.vacate();

        check("Stall becomes vacant", !stall.isOccupied());
        check("Trader assignment is cleared",
                stall.getTraderId() == -1);
    }

    private static void testMarket() {
        System.out.println("\n--- MARKET TESTS ---");

        Market market = new Market();

        Trader trader = new Trader(
                1, "Mary Phiri", "0966000000", "GENERAL", 1
        );

        Stall stall = new Stall(1, "B-01");

        market.addTrader(trader);
        market.addStall(stall);

        check("Trader registered",
                market.getTrader(1) == trader);

        check("Stall registered",
                market.getStall(1) == stall);

        check("Trader count", market.getTraderCount() == 1);
        check("Stall count", market.getStallCount() == 1);

        market.assignTraderToStall(1, 1);

        check("Market assigns trader to stall",
                stall.isOccupied() && stall.getTraderId() == 1);

        market.vacateStall(1);

        check("Market vacates stall", !stall.isOccupied());

        Trader inactiveTrader = new Trader(
                2, "Peter Zulu", "0955000000", "GENERAL", 1
        );

        inactiveTrader.deactivate();
        market.addTrader(inactiveTrader);

        Stall secondStall = new Stall(2, "B-02");
        market.addStall(secondStall);

        try {
            market.assignTraderToStall(2, 2);
            check("Inactive trader assignment rejected", false);
        } catch (IllegalStateException e) {
            check("Inactive trader assignment rejected", true);
        }
    }
}