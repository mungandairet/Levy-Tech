package com.levytech.service;

import com.levytech.db.Database;
import com.levytech.db.TraderDao;
import com.levytech.db.PaymentDao;
import com.levytech.db.LedgerDao;

import com.levytech.domain.Trader;
import com.levytech.domain.Payment;
import com.levytech.domain.LedgerEntry;

import com.levytech.exception.InvalidPaymentException;
import com.levytech.exception.TraderNotFoundException;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class PaymentService {

    public record PaymentResult(
            int paymentId,
            long amountNgwee,
            long balanceAfterNgwee,
            long arrearsAfterNgwee,
            long appliedToArrearsNgwee
    ) {}

    private final Database db;
    private final TraderDao traderDao = new TraderDao();
    private final PaymentDao paymentDao = new PaymentDao();
    private final LedgerDao ledgerDao = new LedgerDao();
    private final BillingService billingService;

    public PaymentService(
            Database db,
            BillingService billingService
    ) {
        this.db = db;
        this.billingService = billingService;
    }

    public PaymentResult recordPayment(
            int traderId,
            long amountNgwee,
            String reference
    ) throws Exception {

        if (amountNgwee <= 0) {
            throw new InvalidPaymentException(
                    "Payment amount must be greater than zero"
            );
        }

        LocalDate today = LocalDate.now();

        try (Connection c = db.connect()) {
            c.setAutoCommit(false);

            try {
                Trader trader = traderDao.findById(c, traderId);

                if (trader == null) {
                    throw new TraderNotFoundException(traderId);
                }

                trader.credit(amountNgwee);

                Payment payment = new Payment(
                        0,
                        traderId,
                        amountNgwee,
                        today,
                        reference
                );

                payment = paymentDao.insert(c, payment);

                LedgerEntry creditEntry = new LedgerEntry(
                        0,
                        traderId,
                        LedgerEntry.Type.CREDIT,
                        amountNgwee,
                        trader.getBalanceNgwee(),
                        null,
                        today,
                        "Payment received: " + reference
                );

                ledgerDao.insert(c, creditEntry);

                long appliedToArrears =
                        billingService.settleArrears(c, trader);

                traderDao.update(c, trader);

                c.commit();

                return new PaymentResult(
                        payment.id(),
                        amountNgwee,
                        trader.getBalanceNgwee(),
                        trader.getArrearsNgwee(),
                        appliedToArrears
                );

            } catch (Exception e) {
                try {
                    c.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }
        }
    }
}