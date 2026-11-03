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

    public PlanOption insert(Connection c, PlanOption plan) throws SQLException {
        String sql = """
                INSERT INTO plans (
                    name, plan_type, base_ngwee, surcharge_ngwee, grace_days
                ) VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = c.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, plan.name());
            ps.setString(2, plan.planType());
            ps.setLong(3, plan.baseNgwee());
            ps.setLong(4, plan.surchargeNgwee());
            ps.setInt(5, plan.graceDays());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new PlanOption(
                            rs.getInt(1),
                            plan.name(),
                            plan.planType(),
                            plan.baseNgwee(),
                            plan.surchargeNgwee(),
                            plan.graceDays()
                    );
                }
            }
        }

        throw new SQLException("Failed to retrieve generated plan ID");
    }

    public List<PlanOption> findAll(Connection c)
            throws SQLException {
        String sql = """
                SELECT id, name, plan_type, base_ngwee, surcharge_ngwee, grace_days
                FROM plans
                ORDER BY name
                """;
        List<PlanOption> plans = new ArrayList<>();

        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                plans.add(new PlanOption(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("plan_type"),
                        rs.getLong("base_ngwee"),
                        rs.getLong("surcharge_ngwee"),
                        rs.getInt("grace_days")
                ));
            }
        }

        return plans;
    }

    public LevyPlan findById(Connection c, int id) throws SQLException {
        String sql = """
                SELECT plan_type, base_ngwee, surcharge_ngwee, grace_days
                FROM plans
                WHERE id = ?
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return LevyPlanFactory.fromRow(
                        rs.getString("plan_type"),
                        rs.getLong("base_ngwee"),
                        rs.getLong("surcharge_ngwee"),
                        rs.getInt("grace_days")
                );
            }
        }
    }
}