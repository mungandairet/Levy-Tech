package com.levytech.db;

import com.levytech.domain.Market;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MarketDao {

    public List<Market> findAll(Connection c) throws SQLException {
        String sql = """
                SELECT id, name, location
                FROM markets
                ORDER BY id
                """;
        List<Market> markets = new ArrayList<>();

        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                markets.add(map(rs));
            }
        }

        return markets;
    }

    public Market findById(Connection c, int id) throws SQLException {
        String sql = """
                SELECT id, name, location
                FROM markets
                WHERE id = ?
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Market insert(Connection c, Market market) throws SQLException {
        String sql = """
                INSERT INTO markets (name, location)
                VALUES (?, ?)
                """;

        try (PreparedStatement ps = c.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, market.name());
            ps.setString(2, market.location());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Market(
                            rs.getInt(1), market.name(), market.location());
                }
            }
        }

        throw new SQLException("Failed to retrieve generated market ID");
    }

    public boolean update(Connection c, Market market) throws SQLException {
        String sql = """
                UPDATE markets
                SET name = ?, location = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, market.name());
            ps.setString(2, market.location());
            ps.setInt(3, market.id());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(Connection c, int id) throws SQLException {
        String sql = "DELETE FROM markets WHERE id = ?";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Market map(ResultSet rs) throws SQLException {
        return new Market(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("location")
        );
    }
}
