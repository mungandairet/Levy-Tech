package com.levytech.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {

    private final String databaseUrl;

    public Database() {
        this("jdbc:sqlite:market.db");
    }

    public Database(String databaseUrl) {
        if (databaseUrl == null || databaseUrl.isBlank()) {
            throw new IllegalArgumentException("Database URL is required");
        }
        this.databaseUrl = databaseUrl;
    }

    public Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection(databaseUrl);

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            return connection;
        } catch (SQLException e) {
            connection.close();
            throw e;
        }
    }

    public void initializeSchema() throws SQLException {
        try {
            executeSqlResource("/schema.sql");
        } catch (IOException e) {
            throw new SQLException("Unable to read database schema", e);
        }
    }

    public static void init() throws SQLException, IOException {
        Database database = new Database();
        database.executeSqlResource("/schema.sql");
        database.executeSqlResource("/seed.sql");
    }

    private void executeSqlResource(String resourcePath)
            throws SQLException, IOException {

        try (InputStream input =
                     Database.class.getResourceAsStream(resourcePath)) {

            if (input == null) {
                throw new IOException(
                        "Database resource not found: " + resourcePath);
            }

            String sql = new String(
                    input.readAllBytes(),
                    StandardCharsets.UTF_8);

            try (Connection connection = connect();
                 Statement statement = connection.createStatement()) {

                String[] commands = sql.split(";");

                for (String command : commands) {
                    String trimmedCommand = command.trim();

                    if (!trimmedCommand.isEmpty()) {
                        statement.execute(trimmedCommand);
                    }
                }
            }
        }
    }
}