package com.levytech.db;

// Import classes for reading SQL files from the resources folder.
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

// Import JDBC classes for database operations.
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {

    // Public constructor allows services to receive a Database object.
    public Database() {
    }

    // Gives services such as BillingService and PaymentService
    // a way to obtain a database connection.
    public Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    // Initializes the database by creating tables first
    // and then inserting the initial seed data.
    public static void init() throws SQLException, IOException {

        // Create the database tables.
        executeSqlResource("/schema.sql");

        // Insert the initial plans and other starting records.
        executeSqlResource("/seed.sql");
    }

    // Reads an SQL file from src/main/resources and executes its commands.
    private static void executeSqlResource(String resourcePath)
            throws SQLException, IOException {

        // Open the requested SQL resource.
        // try-with-resources automatically closes the input stream.
        try (InputStream input =
                     Database.class.getResourceAsStream(resourcePath)) {

            // If the file cannot be found, report a clear error.
            if (input == null) {
                throw new IOException(
                        "Database resource not found: " + resourcePath);
            }

            // Read the entire SQL file and convert its bytes into text.
            String sql = new String(
                    input.readAllBytes(),
                    StandardCharsets.UTF_8);

            // Open a database connection and create a Statement
            // that will execute the SQL commands.
            try (Connection connection =
                         DatabaseConnection.getConnection();
                 Statement statement = connection.createStatement()) {

                // Split the SQL script at semicolons because each
                // semicolon marks the end of an SQL statement.
                String[] commands = sql.split(";");

                // Go through each SQL command one at a time.
                for (String command : commands) {

                    // Remove unnecessary spaces and newlines.
                    String trimmedCommand = command.trim();

                    // Ignore empty commands and execute valid ones.
                    if (!trimmedCommand.isEmpty()) {
                        statement.execute(trimmedCommand);
                    }
                }
            }
        }
    }
}