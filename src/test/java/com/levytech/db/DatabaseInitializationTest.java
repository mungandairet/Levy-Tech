package com.levytech.db;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseInitializationTest {

    @Test
    void testDatabaseInitializationIsRepeatable() throws Exception {

        // Initialize the database for the first time.
        Database.init();

        // Record the number of rows in each table.
        Map<String, Integer> firstCounts = getTableCounts();

        // Initialize the database again.
        Database.init();

        // Check that the second initialization added no duplicates.
        Map<String, Integer> secondCounts = getTableCounts();

        assertEquals(firstCounts, secondCounts);
    }

    private Map<String, Integer> getTableCounts() throws Exception {

        Map<String, Integer> counts = new LinkedHashMap<>();

        try (Connection connection =
                     DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            counts.put("plans", getCount(statement, "plans"));
            counts.put("stalls", getCount(statement, "stalls"));
            counts.put("traders", getCount(statement, "traders"));
            counts.put("payments", getCount(statement, "payments"));
            counts.put("ledger_entries",
                    getCount(statement, "ledger_entries"));
        }

        return counts;
    }

    private int getCount(Statement statement, String table)
            throws Exception {

        try (ResultSet resultSet =
                     statement.executeQuery(
                             "SELECT COUNT(*) FROM " + table)) {

            resultSet.next();
            return resultSet.getInt(1);
        }
    }
}