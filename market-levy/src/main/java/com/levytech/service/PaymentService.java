// The result object to return to the API route
public record PaymentResult(int paymentId, long amountNgwee, long balanceAfterNgwee, long arrearsAfterNgwee, long appliedToArrearsNgwee) {}

private final Database db;
private final TraderDao traderDao = new TraderDao();
private final PaymentDao paymentDao = new PaymentDao();
private final BillingService billingService;

public PaymentService(Database db, BillingService billingService) {
    this.db = db;
    this.billingService = billingService;
}

public PaymentResult recordPayment(int traderId, long amountNgwee, String reference) throws Exception {
    // 1. Validate the amount
    if (amountNgwee <= 0) {
        throw new InvalidPaymentException("Payment amount must be greater than zero");
    }

    try (Connection c = db.connect()) {
        c.setAutoCommit(false); // Start transaction
        try {
            // 2. Find the Trader
            Trader trader = traderDao.findById(c, traderId);
            if (trader == null) {
                throw new TraderNotFoundException(traderId);
            }

            // 3. Credit the balance
            trader.credit(amountNgwee);
            
            // 4. Save the payment record
            Payment payment = new Payment(0, traderId, amountNgwee, LocalDate.now(), reference);
            payment = paymentDao.insert(c, payment); // Returns the payment with a generated ID

            // 5. Write the CREDIT entry to the ledger
            LedgerEntry.insert(c, traderId, LedgerEntry.Type.CREDIT, amountNgwee, trader.getBalanceNgwee(), null, LocalDate.now(), "Payment received: " + reference);

            // 6. Settle any arrears using the new balance
            long appliedToArrears = billingService.settleArrears(c, trader);

            // 7. Save the updated trader
            traderDao.update(c, trader);

            c.commit(); // Save everything to the database

            return new PaymentResult(payment.id(), amountNgwee, trader.getBalanceNgwee(), trader.getArrearsNgwee(), appliedToArrears);

        } catch (Exception e) {
            c.rollback(); // Undo everything if an error occurs
            throw e;
        }
    }
}