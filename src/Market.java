import java.util.HashMap;
import java.util.Map;

public class Market {
    private final Map<Integer, Trader> traders;
    private final Map<Integer, Stall> stalls;

    public Market() {
        this.traders = new HashMap<>();
        this.stalls = new HashMap<>();
    }

    public void addTrader(Trader trader) {
        if (trader == null) {
            throw new IllegalArgumentException(
                    "Trader cannot be null."
            );
        }

        if (traders.containsKey(trader.getId())) {
            throw new IllegalArgumentException(
                    "Trader ID already exists."
            );
        }

        traders.put(trader.getId(), trader);
    }

    public void addStall(Stall stall) {
        if (stall == null) {
            throw new IllegalArgumentException(
                    "Stall cannot be null."
            );
        }

        if (stalls.containsKey(stall.getId())) {
            throw new IllegalArgumentException(
                    "Stall ID already exists."
            );
        }

        stalls.put(stall.getId(), stall);
    }

    public Trader getTrader(int traderId) {
        return traders.get(traderId);
    }

    public Stall getStall(int stallId) {
        return stalls.get(stallId);
    }

    public void assignTraderToStall(int traderId, int stallId) {
        Trader trader = traders.get(traderId);
        Stall stall = stalls.get(stallId);

        if (trader == null) {
            throw new IllegalArgumentException(
                    "Trader not found."
            );
        }

        if (stall == null) {
            throw new IllegalArgumentException(
                    "Stall not found."
            );
        }

        if (!trader.isActive()) {
            throw new IllegalStateException(
                    "Cannot assign an inactive trader."
            );
        }

        stall.occupy(traderId);
    }

    public void vacateStall(int stallId) {
        Stall stall = stalls.get(stallId);

        if (stall == null) {
            throw new IllegalArgumentException(
                    "Stall not found."
            );
        }

        stall.vacate();
    }

    public int getTraderCount() {
        return traders.size();
    }

    public int getStallCount() {
        return stalls.size();
    }
}
