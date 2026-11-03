package com.levytech.db;

import com.levytech.domain.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentDao {

    // Save a payment and return it with its generated ID.
    public Payment insert(Connection c, Payment payment)
            throws SQLException {

        if (payment.paidOn() == null) {
            throw new IllegalArgumentException(
                    "Payment date cannot be null");
        }

        String sql = """
                INSERT INTO payments (
                    trader_id,
                    amount_ngwee,
                    paid_on,
                    cycle
                ) VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = c.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, payment.traderId());
            ps.setLong(2, payment.amountNgwee());
            ps.setString(3, payment.paidOn().toString());
            ps.setString(4, payment.cycle());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Payment(
                            rs.getInt(1),
                            payment.traderId(),
                            payment.amountNgwee(),
                            payment.paidOn(),
                            payment.cycle()
                    );
                }
            }

            throw new SQLException(
                    "Failed to retrieve generated payment ID");
        }
    }

    // Retrieve all payments belonging to a trader.
    public List<Payment> findByTrader(
            Connection c, int traderId) throws SQLException {

        String sql = """
                SELECT id, trader_id, amount_ngwee, paid_on, cycle
                FROM payments
                WHERE trader_id = ?
                ORDER BY id DESC
                """;

        List<Payment> payments = new ArrayList<>();

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, traderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    payments.add(new Payment(
                            rs.getInt("id"),
                            rs.getInt("trader_id"),
                            rs.getLong("amount_ngwee"),
                            LocalDate.parse(rs.getString("paid_on")),
                            rs.getString("cycle")
                    ));
                }
            }
        }

        return payments;
    }
}