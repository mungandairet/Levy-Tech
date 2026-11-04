package com.levytech.db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseConnection {

    // The URL tells Java which SQLite database file to connect to.
    // If market.db does not exist, SQLite will create it.
    private static final String DATABASE_URL = "jdbc:sqlite:market.db";


    private DatabaseConnection() {
    }

    // Opens and returns a connection to the SQLite database.
    public static Connection getConnection() throws SQLException {

        // Establish the connection using the database URL.
        Connection connection =
                DriverManager.getConnection(DATABASE_URL);

        try (Statement statement = connection.createStatement()) {

            // SQLite does not enforce foreign keys by default.
            // This enables foreign-key checking for this connection.
            statement.execute("PRAGMA foreign_keys = ON");

            // Return the connection so DAOs and services can use it.
            return connection;

        } catch (SQLException e) {

            // to avoid leaving an unused connection open.
            connection.close();

            // Pass the original database error to the caller.
            throw e;
        }
    }
}
