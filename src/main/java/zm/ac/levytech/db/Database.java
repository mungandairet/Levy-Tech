package zm.ac.levytech.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {

    private Database() {
        // Prevent creating instances of this utility class.
    }

    public static void init() throws SQLException, IOException {
        executeSqlResource("/schema.sql");
        executeSqlResource("/seed.sql");
    }

    private static void executeSqlResource(String resourcePath)
            throws SQLException, IOException {

        try (InputStream input = Database.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IOException("Database resource not found: " + resourcePath);
            }

            String sql = new String(input.readAllBytes(), StandardCharsets.UTF_8);

            try (Connection connection = DatabaseConnection.getConnection();
                 Statement statement = connection.createStatement()) {

                for (String command : sql.split(";")) {
                    String trimmedCommand = command.trim();

                    if (!trimmedCommand.isEmpty()) {
                        statement.execute(trimmedCommand);
                    }
                }
            }
        }
    }
}
