package com.levytech.db;

import com.levytech.domain.LedgerEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LedgerDao {

    public boolean cycleAlreadyBilled(
        Connection c, int traderId, String cycle
) throws SQLException {

    String sql = """
            SELECT 1
            FROM ledger_entries
            WHERE trader_id = ?
              AND cycle = ?
              AND entry_type IN ('DEDUCTION', 'ARREARS')
            LIMIT 1
            """;

    try (PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, traderId);
        ps.setString(2, cycle);

        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }
}
public void insert(Connection c, LedgerEntry entry)
        throws SQLException {

    String sql = """
            INSERT INTO ledger_entries (
                trader_id,
                entry_type,
                amount_ngwee,
                balance_after_ngwee,
                cycle,
                entry_date,
                description
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    try (PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, entry.getTraderId());
        ps.setString(2, entry.getType().name());
        ps.setLong(3, entry.getAmountNgwee());
        ps.setLong(4, entry.getBalanceAfterNgwee());
        ps.setString(5, entry.getCycle());
        ps.setString(6, entry.getEntryDate().toString());
        ps.setString(7, entry.getDescription());

        ps.executeUpdate();
    }
}


//Comment here

public List<LedgerEntry> findByTrader(Connection c, int traderId)
        throws SQLException {

    String sql = """
            SELECT id, trader_id, entry_type, amount_ngwee,
                   balance_after_ngwee, cycle, entry_date, description
            FROM ledger_entries
            WHERE trader_id = ?
            ORDER BY id DESC
            """;

    List<LedgerEntry> entries = new ArrayList<>();

    try (PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, traderId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                entries.add(new LedgerEntry(
                        rs.getInt("id"),
                        rs.getInt("trader_id"),
                        LedgerEntry.Type.valueOf(
                                rs.getString("entry_type")),
                        rs.getLong("amount_ngwee"),
                        rs.getLong("balance_after_ngwee"),
                        rs.getString("cycle"),
                        LocalDate.parse(rs.getString("entry_date")),
                        rs.getString("description")
                ));
            }
        }
    }

    return entries;
}

}