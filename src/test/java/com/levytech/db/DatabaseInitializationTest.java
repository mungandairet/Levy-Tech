package com.levytech.db;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseInitializationTest {

    @Test
    void testDatabaseInitializationIsRepeatable() throws Exception {

        Database.init();
        Database.init();

        try (Connection connection =
                     DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            assertEquals(2, getCount(statement, "plans"));
            assertEquals(8, getCount(statement, "stalls"));
            assertEquals(5, getCount(statement, "traders"));
            assertEquals(0, getCount(statement, "payments"));
            assertEquals(0, getCount(statement, "ledger_entries"));
        }
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
