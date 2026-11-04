package com.levytech.db;

import com.levytech.domain.LevyPlan;
import com.levytech.domain.LevyPlanFactory;
import com.levytech.domain.PlanOption;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlanDao {

    // Retrieves one levy plan for BillingService.
    public LevyPlan findById(Connection c, int id)
            throws SQLException {

        String sql = """
                SELECT plan_type,
                       rate_ngwee,
                       surcharge_ngwee,
                       grace_days
                FROM plans
                WHERE id = ?
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return LevyPlanFactory.fromRow(
                            rs.getString("plan_type"),
                            rs.getLong("rate_ngwee"),
                            rs.getLong("surcharge_ngwee"),
                            rs.getInt("grace_days")
                    );
                }
            }
        }

        return null;
    }

    // Retrieves all levy plans for PlanService.
    public List<PlanOption> findAll(Connection c)
            throws SQLException {

        String sql = """
                SELECT id, name, plan_type,
                       rate_ngwee, surcharge_ngwee, grace_days
                FROM plans
                ORDER BY id
                """;

        List<PlanOption> plans = new ArrayList<>();

        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                plans.add(new PlanOption(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("plan_type"),
                        rs.getLong("rate_ngwee"),
                        rs.getLong("surcharge_ngwee"),
                        rs.getInt("grace_days")
                ));
            }
        }

        return plans;
    }

    // Inserts a new levy plan and returns it with its generated ID.
    public PlanOption insert(Connection c, PlanOption option)
            throws SQLException {

        String sql = """
                INSERT INTO plans (
                    name, plan_type, rate_ngwee,
                    surcharge_ngwee, grace_days
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = c.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, option.name());
            ps.setString(2, option.planType());
            ps.setLong(3, option.baseNgwee());
            ps.setLong(4, option.surchargeNgwee());
            ps.setInt(5, option.graceDays());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new PlanOption(
                            keys.getInt(1),
                            option.name(),
                            option.planType(),
                            option.baseNgwee(),
                            option.surchargeNgwee(),
                            option.graceDays()
                    );
                }
            }
        }

        throw new SQLException(
                "Creating plan failed: no generated ID returned.");
    }
}