package com.levytech.db;

import com.levytech.domain.LedgerEntry;
import com.levytech.domain.LevyPlan;
import com.levytech.domain.Payment;
import com.levytech.domain.Trader;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseIntegrationTest {

    @Test
    void testAllDaosAgainstSqliteDatabase() throws Exception {

        TraderDao traderDao = new TraderDao();
        PlanDao planDao = new PlanDao();
        LedgerDao ledgerDao = new LedgerDao();
        PaymentDao paymentDao = new PaymentDao();

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                // 1. Test TraderDao.findById().
                Trader trader = traderDao.findById(connection, 1);

                assertNotNull(trader);
                assertEquals("John Banda", trader.getFullName());

                long originalBalance = trader.getBalanceNgwee();

                // 2. Test TraderDao.update().
                trader.credit(1);
                traderDao.update(connection, trader);

                Trader updatedTrader =
                        traderDao.findById(connection, 1);

                assertEquals(
                        originalBalance + 1,
                        updatedTrader.getBalanceNgwee()
                );

                // 3. Test PlanDao.findById().
                LevyPlan plan =
                        planDao.findById(connection, trader.getPlanId());

                assertNotNull(plan);

                // 4. Test LedgerDao.cycleAlreadyBilled() before insertion.
                String cycle = "2099-11";

                assertFalse(
                        ledgerDao.cycleAlreadyBilled(
                                connection, trader.getId(), cycle)
                );

                // 5. Test LedgerDao.insert().
                LedgerEntry entry = new LedgerEntry(
                        0,
                        trader.getId(),
                        LedgerEntry.Type.DEDUCTION,
                        500,
                        updatedTrader.getBalanceNgwee(),
                        cycle,
                        LocalDate.of(2099, 11, 1),
                        "DAO integration test"
                );

                ledgerDao.insert(connection, entry);

                assertTrue(
                        ledgerDao.cycleAlreadyBilled(
                                connection, trader.getId(), cycle)
                );

                // 6. Test LedgerDao.findByTrader().
                List<LedgerEntry> entries =
                        ledgerDao.findByTrader(connection, trader.getId());

                assertTrue(
                        entries.stream().anyMatch(
                                e -> cycle.equals(e.getCycle())
                        )
                );

                // 7. Test PaymentDao.insert() and generated ID.
                Payment newPayment = new Payment(
                        0,
                        trader.getId(),
                        1000,
                        LocalDate.of(2026, 11, 4),
                        "DAO-TEST"
                );

                Payment savedPayment =
                        paymentDao.insert(connection, newPayment);

                assertNotNull(savedPayment);
                assertTrue(
                        savedPayment.id() > 0,
                        "SQLite should return a generated payment ID"
                );

                // 8. Test PaymentDao.findByTrader().
                List<Payment> payments =
                        paymentDao.findByTrader(
                                connection, trader.getId());

                assertTrue(
                        payments.stream().anyMatch(
                                p -> p.id() == savedPayment.id()
                                        && p.amountNgwee() == 1000
                                        && "DAO-TEST".equals(p.reference())
                        )
                );

            } finally {
                // Undo every test change, including the trader update.
                connection.rollback();
            }
        }
    }
}
