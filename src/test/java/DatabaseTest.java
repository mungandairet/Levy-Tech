import zm.ac.levytech.db.Database;
import zm.ac.levytech.db.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTest {

    public static void main(String[] args) {
        try {
            // Initialize the database, tables, and sample data.
            Database.init();

            try (Connection connection = DatabaseConnection.getConnection()) {
                checkTable(connection, "plans", 3);
                checkTable(connection, "stalls", 8);
                checkTable(connection, "traders", 5);
                checkTable(connection, "billing_cycles", 0);
                checkTable(connection, "ledger_entries", 0);
            }

            System.out.println("Database initialization test completed.");

        } catch (SQLException | IOException e) {
            System.err.println("Database test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void checkTable(
            Connection connection, String tableName, int expectedCount)
            throws SQLException {

        String sql = "SELECT COUNT(*) FROM " + tableName;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            int actualCount = resultSet.getInt(1);

            if (actualCount != expectedCount) {
                throw new SQLException(
                        tableName + ": expected " + expectedCount
                                + " records but found " + actualCount);
            }

            System.out.println(
                    "PASS: " + tableName + " contains " + actualCount + " records.");
        }
    }
}
