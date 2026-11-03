package com.levytech.db;

import com.levytech.domain.LevyPlan;
import com.levytech.domain.LevyPlanFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlanDao {

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
}