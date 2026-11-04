package com.levytech.db;

import com.levytech.domain.Market;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarketDaoTest {

    @Test
    void supportsMarketCrud() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE markets (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        location TEXT NOT NULL
                    )
                    """);

            MarketDao dao = new MarketDao();
            Market created = dao.insert(
                    connection, new Market(0, "  Central Market ", " Ndola "));

            assertTrue(created.id() > 0);
            assertEquals("Central Market", created.name());
            assertEquals("Ndola", created.location());
            assertEquals(created, dao.findById(connection, created.id()));
            assertEquals(List.of(created), dao.findAll(connection));

            Market updated = new Market(created.id(), "Town Market", "Kitwe");
            assertTrue(dao.update(connection, updated));
            assertEquals(updated, dao.findById(connection, created.id()));

            assertTrue(dao.delete(connection, created.id()));
            assertNull(dao.findById(connection, created.id()));
            assertFalse(dao.delete(connection, created.id()));
        }
    }
}
