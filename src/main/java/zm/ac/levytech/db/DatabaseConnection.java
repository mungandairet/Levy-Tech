package zm.ac.levytech.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static final String DATABASE_URL = "jdbc:sqlite:market.db";

    private DatabaseConnection() {
        // Prevent creating instances of this utility class.
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }
}
