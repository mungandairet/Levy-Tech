package com.levytech.service;

import com.levytech.db.Database;
import com.levytech.db.LedgerDao;
import com.levytech.db.PlanDao;
import com.levytech.db.TraderDao;
import com.levytech.domain.LedgerEntry;
import com.levytech.domain.LevyPlan;
import com.levytech.domain.Trader;
import com.levytech.exception.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;

public class BillingService {
    
    public enum BillingOutcome { DEDUCTED, ARREARS }
    public record BillingResult(BillingOutcome outcome, long charged, long balanceAfter, long arrears, String cycle) {}

    private final Database db;
    private final TraderDao traderDao = new TraderDao();
    private final PlanDao planDao = new PlanDao();
    private final LedgerDao ledgerDao = new LedgerDao();

    public BillingService(Database db) {
        this.db = db;
    }

    public BillingResult runCycle(int traderId, String cycle) {
        if (cycle == null || !cycle.matches("\\d{4}-(0[1-9]|1[0-2])")) {
            throw new InvalidBillingCycleException(cycle);
        }
        
        LocalDate cycleDate = YearMonth.parse(cycle).atDay(1);

        try (Connection c = db.connect()) {
            c.setAutoCommit(false);
            try {
                Trader trader = traderDao.findById(c, traderId);
                if (trader == null) throw new TraderNotFoundException(traderId);
                if (!trader.isActive()) throw new TraderNotBillableException(traderId, "Inactive trader");
                if (trader.getStallId() == null) throw new TraderNotBillableException(traderId, "No stall assigned");

                if (ledgerDao.cycleAlreadyBilled(c, traderId, cycle)) {
                    throw new CycleAlreadyBilledException(traderId, cycle);
                }

                LevyPlan plan = planDao.findById(c, trader.getPlanId());
                if (plan == null) throw new RuntimeException("Plan not found for trader " + traderId);
                long charge = plan.calculateChargeNgwee();

                BillingOutcome outcome;

                if (trader.getBalanceNgwee() >= charge) {
                    trader.debit(charge);
                    LedgerEntry.insert(c, traderId, LedgerEntry.Type.DEDUCTION, charge, trader.getBalanceNgwee(), cycle, cycleDate, "Billing cycle " + cycle);
                    outcome = BillingOutcome.DEDUCTED;
                } else {
                    trader.recordArrears(charge, cycleDate);
                    LedgerEntry.insert(c, traderId, LedgerEntry.Type.ARREARS, charge, trader.getBalanceNgwee(), cycle, cycleDate, "Unpaid billing cycle " + cycle);
                    outcome = BillingOutcome.ARREARS;
                }

                traderDao.update(c, trader);
                c.commit();
                
                return new BillingResult(outcome, charge, trader.getBalanceNgwee(), trader.getArrearsNgwee(), cycle);

            } catch (Exception e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during billing", e);
        }
    }

    public long settleArrears(Connection c, Trader trader) throws SQLException {
        long balance = trader.getBalanceNgwee();
        long arrears = trader.getArrearsNgwee();
        long applied = Math.min(balance, arrears);
        
        if (applied > 0) {
            trader.debit(applied);
            trader.reduceArrears(applied);
            LedgerEntry.insert(c, trader.getId(), LedgerEntry.Type.DEDUCTION, applied, trader.getBalanceNgwee(), null, LocalDate.now(), "Settled arrears");
        }
        return applied;
    }
}