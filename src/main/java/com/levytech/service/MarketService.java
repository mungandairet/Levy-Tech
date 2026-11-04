package com.levytech.service;

import com.levytech.db.Database;
import com.levytech.db.MarketDao;
import com.levytech.domain.Market;
import com.levytech.exception.MarketNotFoundException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MarketService {

    private final Database db;
    private final MarketDao marketDao = new MarketDao();

    public MarketService(Database db) {
        this.db = db;
    }

    public List<Market> findAll() {
        try (Connection c = db.connect()) {
            return marketDao.findAll(c);
        } catch (SQLException e) {
            throw new ApiPersistenceException("Unable to retrieve markets", e);
        }
    }

    public Market findById(int id) {
        validateId(id);

        try (Connection c = db.connect()) {
            Market market = marketDao.findById(c, id);
            if (market == null) {
                throw new MarketNotFoundException(id);
            }
            return market;
        } catch (SQLException e) {
            throw new ApiPersistenceException("Unable to retrieve market", e);
        }
    }

    public Market create(String name, String location) {
        Market market = new Market(0, name, location);

        try (Connection c = db.connect()) {
            return marketDao.insert(c, market);
        } catch (SQLException e) {
            throw new ApiPersistenceException("Unable to create market", e);
        }
    }

    public Market update(int id, String name, String location) {
        validateId(id);
        Market market = new Market(id, name, location);

        try (Connection c = db.connect()) {
            if (!marketDao.update(c, market)) {
                throw new MarketNotFoundException(id);
            }
            return market;
        } catch (SQLException e) {
            throw new ApiPersistenceException("Unable to update market", e);
        }
    }

    public void delete(int id) {
        validateId(id);

        try (Connection c = db.connect()) {
            if (!marketDao.delete(c, id)) {
                throw new MarketNotFoundException(id);
            }
        } catch (SQLException e) {
            throw new ApiPersistenceException("Unable to delete market", e);
        }
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Market ID must be positive");
        }
    }

}
