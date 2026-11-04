package com.levytech.db;

import com.levytech.domain.Stall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StallDao {

    public List<Stall> findAll(Connection c) throws SQLException {
        String sql = """
                SELECT s.id, s.code, s.section, s.size_sqm, t.id AS trader_id
                FROM stalls s
                LEFT JOIN traders t ON t.stall_id = s.id AND t.active = 1
                ORDER BY s.id
                """;
        List<Stall> stalls = new ArrayList<>();

        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                stalls.add(map(rs));
            }
        }

        return stalls;
    }

    public Stall findById(Connection c, int id) throws SQLException {
        String sql = """
                SELECT s.id, s.code, s.section, s.size_sqm, t.id AS trader_id
                FROM stalls s
                LEFT JOIN traders t ON t.stall_id = s.id AND t.active = 1
                WHERE s.id = ?
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Stall insert(
            Connection c, String code, String section, double sizeSqm)
            throws SQLException {
        String sql = """
                INSERT INTO stalls (code, section, size_sqm)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = c.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, code);
            ps.setString(2, section);
            ps.setDouble(3, sizeSqm);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Stall(
                            rs.getInt(1), code, section, sizeSqm, null);
                }
            }
        }

        throw new SQLException("Failed to retrieve generated stall ID");
    }

    public boolean update(Connection c, Stall stall) throws SQLException {
        String sql = """
                UPDATE stalls
                SET code = ?, section = ?, size_sqm = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, stall.getCode());
            ps.setString(2, stall.getSection());
            ps.setDouble(3, stall.getSizeSqm());
            ps.setInt(4, stall.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(Connection c, int id) throws SQLException {
        String sql = """
                DELETE FROM stalls
                WHERE id = ?
                  AND NOT EXISTS (
                      SELECT 1 FROM traders WHERE stall_id = ?
                  )
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean isAssignedToOtherTrader(
            Connection c, int stallId, int traderId) throws SQLException {
        String sql = """
                SELECT 1 FROM traders
                WHERE stall_id = ? AND id != ?
                LIMIT 1
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, stallId);
            ps.setInt(2, traderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isAssigned(Connection c, int stallId) throws SQLException {
        String sql = "SELECT 1 FROM traders WHERE stall_id = ? LIMIT 1";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, stallId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Stall map(ResultSet rs) throws SQLException {
        int traderIdValue = rs.getInt("trader_id");
        Integer traderId = rs.wasNull() ? null : traderIdValue;
        return new Stall(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getString("section"),
                rs.getDouble("size_sqm"),
                traderId
        );
    }
}
