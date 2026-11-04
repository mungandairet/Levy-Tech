package com.levytech.service;

import com.levytech.db.Database;
import com.levytech.db.PlanDao;
import com.levytech.domain.LevyPlanFactory;
import com.levytech.domain.PlanOption;
import com.levytech.exception.ApiConflictException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

public class PlanService {

    private final Database db;
    private final PlanDao planDao = new PlanDao();

    public PlanService(Database db) {
        this.db = db;
    }

    public List<PlanOption> findAll() {
        try (Connection c = db.connect()) {
            return planDao.findAll(c);
        } catch (SQLException e) {
            throw new ApiPersistenceException("Unable to retrieve levy plans", e);
        }
    }

    public PlanOption create(
            String name, String planType, long rateNgwee,
            long surchargeNgwee, int graceDays) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Plan name is required");
        }
        if (planType == null || planType.isBlank()) {
            throw new IllegalArgumentException("Plan type is required");
        }
        if (rateNgwee < 0 || surchargeNgwee < 0 || graceDays < 0) {
            throw new IllegalArgumentException(
                    "Plan amounts and grace days cannot be negative");
        }

        String normalizedType = planType.trim().toUpperCase(Locale.ROOT);
        if ("FLAT".equals(normalizedType) && surchargeNgwee != 0) {
            throw new IllegalArgumentException(
                    "Flat plans cannot have a surcharge");
        }
        LevyPlanFactory.fromRow(
                normalizedType, rateNgwee, surchargeNgwee, graceDays);

        try (Connection c = db.connect()) {
            return planDao.insert(c, new PlanOption(
                    0,
                    name.trim(),
                    normalizedType,
                    rateNgwee,
                    surchargeNgwee,
                    graceDays
            ));
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                throw new ApiConflictException("A levy plan with this name already exists");
            }
            throw new ApiPersistenceException("Unable to create levy plan", e);
        }
    }
}
