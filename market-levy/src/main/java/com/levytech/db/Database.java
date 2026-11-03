
package com.levytech.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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

    public void initializeSchema() throws SQLException {
        try (InputStream schema = Database.class.getResourceAsStream("/schema.sql")) {
            if (schema == null) {
                throw new SQLException("Database schema resource /schema.sql was not found");
            }

            String[] statements = new String(schema.readAllBytes(), StandardCharsets.UTF_8)
                    .split(";");
            try (Connection connection = connect();
                 var statement = connection.createStatement()) {
                for (String sql : statements) {
                    if (!sql.isBlank()) {
                        statement.execute(sql);
                    }
                }
            }
        } catch (IOException e) {
            throw new SQLException("Unable to read database schema", e);
        }
    }

    public Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection(databaseUrl);

        try (var statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }
}
