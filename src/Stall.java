public class Stall {
    private final int id;
    private final String stallNumber;
    private boolean occupied;
    private int traderId;

    public Stall(int id, String stallNumber) {
        this.id = id;
        this.stallNumber = stallNumber;
        this.occupied = false;
        this.traderId = -1;
    }

    public int getId() {
        return id;
    }

    public String getStallNumber() {
        return stallNumber;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public int getTraderId() {
        return traderId;
    }

    public void occupy(int traderId) {
        if (occupied) {
            throw new IllegalStateException(
                    "This stall is already occupied."
            );
        }

        if (traderId <= 0) {
            throw new IllegalArgumentException(
                    "Trader ID must be positive."
            );
        }

        this.traderId = traderId;
        this.occupied = true;
    }

    public void vacate() {
        this.traderId = -1;
        this.occupied = false;
    }
}
