package com.levytech.service;

import com.levytech.db.Database;
import com.levytech.db.StallDao;
import com.levytech.domain.Stall;
import com.levytech.exception.ApiConflictException;
import com.levytech.exception.StallNotFoundException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class StallService {

    private final Database db;
    private final StallDao stallDao = new StallDao();

    public StallService(Database db) {
        this.db = db;
    }

    public List<Stall> findAll() {
        try (Connection c = db.connect()) {
            return stallDao.findAll(c);
        } catch (SQLException e) {
            throw new ApiPersistenceException("Unable to retrieve stalls", e);
        }
    }

    public Stall findById(int id) {
        validateId(id);
        try (Connection c = db.connect()) {
            Stall stall = stallDao.findById(c, id);
            if (stall == null) {
                throw new StallNotFoundException(id);
            }
            return stall;
        } catch (SQLException e) {
            throw new ApiPersistenceException("Unable to retrieve stall", e);
        }
    }

    public Stall create(String code, String section, double sizeSqm) {
        validate(code, section, sizeSqm);
        try (Connection c = db.connect()) {
            return stallDao.insert(c, code.trim(), section.trim(), sizeSqm);
        } catch (SQLException e) {
            if (isConstraintViolation(e)) {
                throw new ApiConflictException("A stall with this code already exists");
            }
            throw new ApiPersistenceException("Unable to create stall", e);
        }
    }

    public Stall update(int id, String code, String section, double sizeSqm) {
        validateId(id);
        validate(code, section, sizeSqm);
        Stall updated = new Stall(id, code.trim(), section.trim(), sizeSqm, null);
        try (Connection c = db.connect()) {
            if (stallDao.findById(c, id) == null) {
                throw new StallNotFoundException(id);
            }
            if (!stallDao.update(c, updated)) {
                throw new StallNotFoundException(id);
            }
            return stallDao.findById(c, id);
        } catch (SQLException e) {
            if (isConstraintViolation(e)) {
                throw new ApiConflictException("A stall with this code already exists");
            }
            throw new ApiPersistenceException("Unable to update stall", e);
        }
    }

    public void delete(int id) {
        validateId(id);
        try (Connection c = db.connect()) {
            Stall stall = stallDao.findById(c, id);
            if (stall == null) {
                throw new StallNotFoundException(id);
            }
            if (stallDao.isAssigned(c, id)) {
                throw new ApiConflictException(
                        "Cannot delete a stall while it is assigned to a trader");
            }
            if (!stallDao.delete(c, id)) {
                throw new ApiConflictException(
                        "Cannot delete a stall while it is assigned to a trader");
            }
        } catch (SQLException e) {
            throw new ApiPersistenceException("Unable to delete stall", e);
        }
    }

    private static void validate(
            String code, String section, double sizeSqm) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Stall code is required");
        }
        if (section == null || section.isBlank()) {
            throw new IllegalArgumentException("Stall section is required");
        }
        if (!Double.isFinite(sizeSqm) || sizeSqm <= 0) {
            throw new IllegalArgumentException(
                    "Stall size must be a positive finite number");
        }
    }

    private static void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Stall ID must be positive");
        }
    }

    private static boolean isConstraintViolation(SQLException e) {
        return e.getErrorCode() == 19;
    }
}
