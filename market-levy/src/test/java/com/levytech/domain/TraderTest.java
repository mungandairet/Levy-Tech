package com.levytech.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class TraderTest {

    private static final LocalDate NOV = LocalDate.of(2026, 11, 1);
    private static final LocalDate DEC = LocalDate.of(2026, 12, 1);

    private Trader trader;

    @BeforeEach
    void setUp() {
        trader = new Trader(1, "John", "097", "Food", 1, 0, 0, null, null, true);
    }

    @Test
    void creditAndDebit() {
        trader.credit(5000);
        trader.debit(2000);
        assertEquals(3000, trader.getBalanceNgwee());
    }

    @Test
    void creditRejectsBadAmount() {
        assertThrows(IllegalArgumentException.class, () -> trader.credit(0));
        assertThrows(IllegalArgumentException.class, () -> trader.credit(-500));
    }

    @Test
    void failedDebitKeepsBalance() {
        trader.credit(3000);
        assertThrows(IllegalArgumentException.class, () -> trader.debit(5000));
        assertEquals(3000, trader.getBalanceNgwee());
    }

    @Test
    void arrearsKeepFirstDate() {
        trader.recordArrears(5000, NOV);
        trader.recordArrears(2000, DEC);
        assertEquals(7000, trader.getArrearsNgwee());
        assertEquals(NOV, trader.getArrearsSince());
    }

    @Test
    void partialPaymentKeepsDate() {
        trader.recordArrears(5000, NOV);
        trader.reduceArrears(3000);
        assertEquals(2000, trader.getArrearsNgwee());
        assertEquals(NOV, trader.getArrearsSince());
    }

    @Test
    void overpaymentClearsArrears() {
        trader.recordArrears(5000, NOV);
        trader.reduceArrears(6000);
        assertEquals(0, trader.getArrearsNgwee());
        assertNull(trader.getArrearsSince());
    }

    @Test
    void reduceRejectsNegative() {
        trader.recordArrears(5000, NOV);
        assertThrows(IllegalArgumentException.class, () -> trader.reduceArrears(-500));
    }

    @Test
    void deactivateFreesStall() {
        trader.assignStall(12);
        trader.deactivate();
        assertFalse(trader.isActive());
        assertNull(trader.getStallId());
    }
}