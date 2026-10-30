public class Trader {
    private final int id;
    private final String name;
    private final String phone;
    private final String category;
    private final int planId;

    private boolean active;
    private long prepaidBalanceNgwee;
    private long arrearsNgwee;

    public Trader(int id, String name, String phone, String category, int planId) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.category = category;
        this.planId = planId;
        this.active = true;
        this.prepaidBalanceNgwee = 0;
        this.arrearsNgwee = 0;
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

    public boolean isActive() {
        return active;
    }

    public long getPrepaidBalanceNgwee() {
        return prepaidBalanceNgwee;
    }

    public long getArrearsNgwee() {
        return arrearsNgwee;
    }

    public void deactivate() {
        this.active = false;
    }

    public void credit(long amountNgwee) {
        if (amountNgwee <= 0) {
            throw new IllegalArgumentException(
                    "Credit amount must be greater than zero."
            );
        }

        prepaidBalanceNgwee = Math.addExact(
                prepaidBalanceNgwee, amountNgwee
        );
    }

    public void debit(long amountNgwee) {
        if (amountNgwee <= 0) {
            throw new IllegalArgumentException(
                    "Debit amount must be greater than zero."
            );
        }

        if (amountNgwee > prepaidBalanceNgwee) {
            throw new IllegalStateException(
                    "Insufficient prepaid balance."
            );
        }

        prepaidBalanceNgwee -= amountNgwee;
    }

    public void recordArrears(long amountNgwee) {
        if (amountNgwee <= 0) {
            throw new IllegalArgumentException(
                    "Arrears amount must be greater than zero."
            );
        }

        arrearsNgwee = Math.addExact(arrearsNgwee, amountNgwee);
    }

    public void reduceArrears(long amountNgwee) {
        if (amountNgwee <= 0) {
            throw new IllegalArgumentException(
                    "Reduction amount must be greater than zero."
            );
        }

        if (amountNgwee > arrearsNgwee) {
            throw new IllegalStateException(
                    "Reduction cannot exceed outstanding arrears."
            );
        }

        arrearsNgwee -= amountNgwee;
    }
}
