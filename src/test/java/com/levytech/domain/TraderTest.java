package com.levytech.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TraderTest {

    private Trader trader;

    private static final LocalDate NOV =
            LocalDate.of(2026, 11, 1);

    @BeforeEach
    void setUp() {
        trader = new Trader(
                1,
                "John",
                "097",
                "Food",
                1,
                0,
                0,
                null,
                null,
                true
        );
    }

    @Test
    void creditIncreasesBalance() {
        trader.credit(5000);

        assertEquals(5000, trader.getBalanceNgwee());
    }

    @Test
    void creditRejectsZeroOrNegativeAmount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> trader.credit(0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> trader.credit(-100)
        );
    }

    @Test
    void debitReducesBalance() {
        trader.credit(10000);
        trader.debit(4000);

        assertEquals(6000, trader.getBalanceNgwee());
    }

    @Test
    void debitRejectsInsufficientBalance() {
        trader.credit(3000);

        assertThrows(
                IllegalArgumentException.class,
                () -> trader.debit(5000)
        );

        assertEquals(3000, trader.getBalanceNgwee());
    }

    @Test
    void debitRejectsZeroOrNegativeAmount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> trader.debit(0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> trader.debit(-100)
        );
    }

    @Test
    void recordArrearsIncreasesOutstandingAmount() {
        trader.recordArrears(5000, NOV);

        assertEquals(5000, trader.getArrearsNgwee());
        assertEquals(NOV, trader.getArrearsSince());
    }

    @Test
    void recordAdditionalArrearsPreservesOriginalDate() {
        LocalDate firstDate = LocalDate.of(2026, 11, 1);
        LocalDate secondDate = LocalDate.of(2026, 11, 2);

        trader.recordArrears(5000, firstDate);
        trader.recordArrears(2000, secondDate);

        assertEquals(7000, trader.getArrearsNgwee());
        assertEquals(firstDate, trader.getArrearsSince());
    }

    @Test
    void reduceArrearsReducesOutstandingAmount() {
        trader.recordArrears(5000, NOV);
        trader.reduceArrears(2000);

        assertEquals(3000, trader.getArrearsNgwee());
        assertEquals(NOV, trader.getArrearsSince());
    }

    @Test
    void fullPaymentClearsArrears() {
        trader.recordArrears(5000, NOV);
        trader.reduceArrears(5000);

        assertEquals(0, trader.getArrearsNgwee());
        assertNull(trader.getArrearsSince());
    }

    @Test
    void reduceArrearsRejectsAmountGreaterThanOutstanding() {
        trader.recordArrears(5000, NOV);

        assertThrows(
                IllegalArgumentException.class,
                () -> trader.reduceArrears(6000)
        );

        assertEquals(5000, trader.getArrearsNgwee());
        assertEquals(NOV, trader.getArrearsSince());
    }

    @Test
    void assignStallUpdatesStallId() {
        trader.assignStall(10);

        assertEquals(10, trader.getStallId());
    }

    @Test
    void assignNullStallRemovesStallAssignment() {
        trader.assignStall(10);
        trader.assignStall(null);

        assertNull(trader.getStallId());
    }

    @Test
    void deactivateMakesTraderInactiveAndRemovesStall() {
        trader.assignStall(10);

        trader.deactivate();

        assertFalse(trader.isActive());
        assertNull(trader.getStallId());
    }
}