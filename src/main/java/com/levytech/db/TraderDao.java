package com.levytech.db;

import com.levytech.domain.Trader;
import com.levytech.exception.TraderNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TraderDao {

    public Trader findById(Connection c, int id)
            throws SQLException {

        String sql = """
                SELECT id, full_name, phone, product_category,
                       plan_id, balance_ngwee, arrears_ngwee,
                       arrears_since, stall_id, active
                FROM traders
                WHERE id = ?
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return map(rs);
            }
        }
    }

    public List<Trader> findAll(Connection c)
            throws SQLException {

        String sql = """
                SELECT id, full_name, phone, product_category,
                       plan_id, balance_ngwee, arrears_ngwee,
                       arrears_since, stall_id, active
                FROM traders
                ORDER BY id
                """;

        List<Trader> traders = new ArrayList<>();

        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                traders.add(map(rs));
            }
        }

        return traders;
    }

    public Trader insert(Connection c, Trader trader) throws SQLException {
        String sql = """
                INSERT INTO traders (
                    full_name, phone, product_category, plan_id, balance_ngwee,
                    arrears_ngwee, arrears_since, stall_id, active
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = c.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, trader.getFullName());
            ps.setString(2, trader.getPhone());
            ps.setString(3, trader.getProductCategory());
            ps.setInt(4, trader.getPlanId());
            ps.setLong(5, trader.getBalanceNgwee());
            ps.setLong(6, trader.getArrearsNgwee());
            if (trader.getArrearsSince() == null) {
                ps.setNull(7, Types.VARCHAR);
            } else {
                ps.setString(7, trader.getArrearsSince().toString());
            }
            if (trader.getStallId() == null) {
                ps.setNull(8, Types.INTEGER);
            } else {
                ps.setInt(8, trader.getStallId());
            }
            ps.setInt(9, trader.isActive() ? 1 : 0);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Trader(
                            rs.getInt(1),
                            trader.getFullName(),
                            trader.getPhone(),
                            trader.getProductCategory(),
                            trader.getPlanId(),
                            trader.getBalanceNgwee(),
                            trader.getArrearsNgwee(),
                            trader.getArrearsSince(),
                            trader.getStallId(),
                            trader.isActive()
                    );
                }
            }
        }

        throw new SQLException("Failed to retrieve generated trader ID");
    }

    public void update(Connection c, Trader trader)
            throws SQLException {

        String sql = """
                UPDATE traders
                SET full_name = ?,
                    phone = ?,
                    product_category = ?,
                    plan_id = ?,
                    balance_ngwee = ?,
                    arrears_ngwee = ?,
                    arrears_since = ?,
                    stall_id = ?,
                    active = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, trader.getFullName());
            ps.setString(2, trader.getPhone());
            ps.setString(3, trader.getProductCategory());
            ps.setInt(4, trader.getPlanId());
            ps.setLong(5, trader.getBalanceNgwee());
            ps.setLong(6, trader.getArrearsNgwee());

            if (trader.getArrearsSince() == null) {
                ps.setNull(7, Types.VARCHAR);
            } else {
                ps.setString(7, trader.getArrearsSince().toString());
            }

            if (trader.getStallId() == null) {
                ps.setNull(8, Types.INTEGER);
            } else {
                ps.setInt(8, trader.getStallId());
            }

            ps.setInt(9, trader.isActive() ? 1 : 0);
            ps.setInt(10, trader.getId());

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated == 0) {
                throw new TraderNotFoundException(trader.getId());
            }
        }
    }

    private Trader map(ResultSet rs) throws SQLException {

        int stallIdValue = rs.getInt("stall_id");
        Integer stallId = rs.wasNull() ? null : stallIdValue;

        String arrearsSinceValue = rs.getString("arrears_since");
        LocalDate arrearsSince = arrearsSinceValue == null
                ? null
                : LocalDate.parse(arrearsSinceValue);

        return new Trader(
                rs.getInt("id"),
                rs.getString("full_name"),
                rs.getString("phone"),
                rs.getString("product_category"),
                rs.getInt("plan_id"),
                rs.getLong("balance_ngwee"),
                rs.getLong("arrears_ngwee"),
                arrearsSince,
                stallId,
                rs.getInt("active") == 1
        );
    }
}