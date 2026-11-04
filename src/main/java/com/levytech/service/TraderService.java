package com.levytech.service;

import com.levytech.db.Database;
import com.levytech.db.PlanDao;
import com.levytech.db.StallDao;
import com.levytech.db.TraderDao;
import com.levytech.domain.Trader;
import com.levytech.exception.ApiConflictException;
import com.levytech.exception.PlanNotFoundException;
import com.levytech.exception.StallNotFoundException;
import com.levytech.exception.StallOccupiedException;
import com.levytech.exception.TraderNotFoundException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TraderService {

    private final Database db;
    private final TraderDao traderDao = new TraderDao();
    private final PlanDao planDao = new PlanDao();
    private final StallDao stallDao = new StallDao();

    public TraderService(Database db) {
        this.db = db;
    }

    public List<Trader> findAll() {
        try (Connection c = db.connect()) {
            return traderDao.findAll(c);
        } catch (SQLException e) {
            throw new ApiPersistenceException("Unable to retrieve traders", e);
        }
    }

    public Trader findById(int id) {
        validateId(id);
        try (Connection c = db.connect()) {
            Trader trader = traderDao.findById(c, id);
            if (trader == null) {
                throw new TraderNotFoundException(id);
            }
            return trader;
        } catch (SQLException e) {
            throw new ApiPersistenceException("Unable to retrieve trader", e);
        }
    }

    public Trader create(
            String fullName, String phone, String productCategory, int planId,
            Integer stallId, Boolean active) {
        validateProfile(fullName, productCategory, planId);
        boolean isActive = active == null || active;
        validateOptionalId(stallId, "Stall");
        Integer assignedStall = isActive ? stallId : null;
        String normalizedPhone = normalizePhone(phone);

        try (Connection c = db.connect()) {
            c.setAutoCommit(false);
            try {
                requirePlan(c, planId);
                requireAvailableStall(c, assignedStall, 0);

                Trader trader = new Trader(
                        0,
                        fullName.trim(),
                        normalizedPhone,
                        productCategory.trim(),
                        planId,
                        0,
                        0,
                        null,
                        assignedStall,
                        isActive
                );
                Trader created = traderDao.insert(c, trader);
                c.commit();
                return created;
            } catch (SQLException | RuntimeException e) {
                rollback(c, e);
                throw e;
            }
        } catch (SQLException e) {
            if (isConstraintViolation(e)) {
                throw new ApiConflictException(
                        "A trader with this phone number or stall assignment already exists");
            }
            throw new ApiPersistenceException("Unable to create trader", e);
        }
    }

    public Trader update(
            int id, String fullName, String phone, String productCategory,
            int planId, Integer stallId, Boolean active) {
        validateId(id);
        validateProfile(fullName, productCategory, planId);
        validateOptionalId(stallId, "Stall");
        String normalizedPhone = normalizePhone(phone);

        try (Connection c = db.connect()) {
            c.setAutoCommit(false);
            try {
                Trader existing = traderDao.findById(c, id);
                if (existing == null) {
                    throw new TraderNotFoundException(id);
                }

                requirePlan(c, planId);
                boolean isActive = active == null ? existing.isActive() : active;
                Integer assignedStall = isActive ? stallId : null;
                requireAvailableStall(c, assignedStall, id);

                Trader updated = new Trader(
                        id,
                        fullName.trim(),
                        normalizedPhone,
                        productCategory.trim(),
                        planId,
                        existing.getBalanceNgwee(),
                        existing.getArrearsNgwee(),
                        existing.getArrearsSince(),
                        assignedStall,
                        isActive
                );
                traderDao.update(c, updated);
                c.commit();
                return updated;
            } catch (SQLException | RuntimeException e) {
                rollback(c, e);
                throw e;
            }
        } catch (SQLException e) {
            if (isConstraintViolation(e)) {
                throw new ApiConflictException(
                        "A trader with this phone number or stall assignment already exists");
            }
            throw new ApiPersistenceException("Unable to update trader", e);
        }
    }

    public void delete(int id) {
        validateId(id);
        try (Connection c = db.connect()) {
            c.setAutoCommit(false);
            try {
                Trader trader = traderDao.findById(c, id);
                if (trader == null) {
                    throw new TraderNotFoundException(id);
                }
                trader.deactivate();
                traderDao.update(c, trader);
                c.commit();
            } catch (SQLException | RuntimeException e) {
                rollback(c, e);
                throw e;
            }
        } catch (SQLException e) {
            throw new ApiPersistenceException("Unable to delete trader", e);
        }
    }

    private void requirePlan(Connection c, int planId) throws SQLException {
        if (planDao.findById(c, planId) == null) {
            throw new PlanNotFoundException(planId);
        }
    }

    private void requireAvailableStall(
            Connection c, Integer stallId, int traderId) throws SQLException {
        if (stallId == null) {
            return;
        }
        if (stallDao.findById(c, stallId) == null) {
            throw new StallNotFoundException(stallId);
        }
        if (stallDao.isAssignedToOtherTrader(c, stallId, traderId)) {
            throw new StallOccupiedException(stallId);
        }
    }

    private static void validateProfile(
            String fullName, String productCategory, int planId) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Trader name is required");
        }
        if (productCategory == null || productCategory.isBlank()) {
            throw new IllegalArgumentException("Product category is required");
        }
        if (planId <= 0) {
            throw new IllegalArgumentException("Plan ID must be positive");
        }
    }

    private static String normalizePhone(String phone) {
        return phone == null || phone.isBlank() ? null : phone.trim();
    }

    private static void validateId(int id) {
        validateOptionalId(id, "Trader");
    }

    private static void validateOptionalId(Integer id, String label) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException(label + " ID must be positive");
        }
    }

    private static boolean isConstraintViolation(SQLException e) {
        return e.getErrorCode() == 19;
    }

    private static void rollback(Connection c, Exception cause) {
        try {
            c.rollback();
        } catch (SQLException rollbackError) {
            cause.addSuppressed(rollbackError);
        }
    }
}
