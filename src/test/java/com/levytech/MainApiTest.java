package com.levytech;

import com.levytech.db.Database;
import io.javalin.Javalin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainApiTest {

    @TempDir
    Path tempDir;

    private Javalin app;
    private HttpClient client;
    private String baseUrl;

    @BeforeEach
    void startApi() throws Exception {
        Database database = new Database(
                "jdbc:sqlite:" + tempDir.resolve("api-test.db"));
        database.initializeSchema();
        try (Connection connection = database.connect();
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    INSERT INTO plans (name, plan_type, rate_ngwee, surcharge_ngwee, grace_days)
                    VALUES ('Standard', 'FLAT', 1000, 0, 0)
                    """);
        }

        int port;
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }
        app = Main.createApp(database);
        app.start(port);
        client = HttpClient.newHttpClient();
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void stopApi() {
        if (app != null) {
            app.stop();
        }
    }

    @Test
    void servesFrontendAndMarketTraderStallEndpoints() throws Exception {
        HttpResponse<String> dashboard = get("/");
        assertEquals(200, dashboard.statusCode());
        assertTrue(dashboard.body().contains("Market at a glance"));
        assertTrue(dashboard.body().contains("href=\"/css/style.css\""));
        assertEquals(200, get("/css/style.css").statusCode());
        assertEquals(200, get("/js/markets.js").statusCode());
        assertEquals(200, get("/plans.html").statusCode());
        assertEquals(200, get("/api/plans").statusCode());
        String premiumPlanRequest =
                "{\"name\":\"Premium\",\"planType\":\"BASE_PLUS_SURCHARGE\","
                        + "\"baseNgwee\":2000,\"surchargeNgwee\":500,\"graceDays\":3}";
        assertEquals(201, send(
                "POST", "/api/plans",
                premiumPlanRequest).statusCode());
        assertTrue(get("/api/plans").body().contains("Premium"));
        assertEquals(409, send("POST", "/api/plans", premiumPlanRequest).statusCode());
        assertEquals("[]", get("/api/markets").body());

        HttpResponse<String> marketCreated = send(
                "POST", "/api/markets",
                "{\"name\":\"Central Market\",\"location\":\"Ndola\"}");
        assertEquals(201, marketCreated.statusCode());
        int marketId = jsonId(marketCreated.body());
        assertTrue(get("/api/markets").body().contains("Central Market"));
        assertEquals(200, get("/api/markets/" + marketId).statusCode());
        assertEquals(200, send(
                "PUT", "/api/markets/" + marketId,
                "{\"name\":\"Town Market\",\"location\":\"Kitwe\"}").statusCode());
        assertEquals(204, send("DELETE", "/api/markets/" + marketId, null).statusCode());
        assertEquals(404, get("/api/markets/" + marketId).statusCode());
        assertEquals(400, send(
                "POST", "/api/markets", "{\"name\":\" \",\"location\":\"Ndola\"}")
                .statusCode());

        HttpResponse<String> stallCreated = send(
                "POST", "/api/stalls",
                "{\"code\":\"A-01\",\"section\":\"Produce\",\"sizeSqm\":2.5}");
        assertEquals(201, stallCreated.statusCode());
        int stallId = jsonId(stallCreated.body());
        assertTrue(get("/api/stalls").body().contains("A-01"));
        assertEquals(200, send(
                "PUT", "/api/stalls/" + stallId,
                "{\"code\":\"A-01\",\"section\":\"Produce\",\"sizeSqm\":3.0}")
                .statusCode());

        HttpResponse<String> traderCreated = send(
                "POST", "/api/traders",
                "{\"fullName\":\"Amina Banda\",\"phone\":\"0970000000\","
                        + "\"productCategory\":\"Produce\",\"planId\":1,"
                        + "\"stallId\":" + stallId + ",\"active\":true}");
        assertEquals(201, traderCreated.statusCode());
        int traderId = jsonId(traderCreated.body());
        assertTrue(get("/api/traders").body().contains("Amina Banda"));
        assertEquals(200, get("/api/traders/" + traderId).statusCode());

        assertEquals(409, send(
                "POST", "/api/traders",
                "{\"fullName\":\"Second Trader\",\"productCategory\":\"Produce\","
                        + "\"planId\":1,\"stallId\":" + stallId + "}")
                .statusCode());
        assertTrue(get("/api/stalls/" + stallId).body()
                .contains("\"occupiedByTraderId\":" + traderId));
        assertEquals(409, send("DELETE", "/api/stalls/" + stallId, null).statusCode());

        HttpResponse<String> updatedTrader = send(
                "PUT", "/api/traders/" + traderId,
                "{\"fullName\":\"Amina B.\",\"phone\":\"0970000000\","
                        + "\"productCategory\":\"Produce\",\"planId\":1,"
                        + "\"stallId\":" + stallId + ",\"active\":true}");
        assertEquals(200, updatedTrader.statusCode());
        assertTrue(updatedTrader.body().contains("Amina B."));

        assertEquals(204, send("DELETE", "/api/traders/" + traderId, null).statusCode());
        assertEquals(200, get("/api/stalls/" + stallId).statusCode());
        assertTrue(get("/api/stalls/" + stallId).body().contains("\"occupiedByTraderId\":null"));
        assertEquals(204, send("DELETE", "/api/stalls/" + stallId, null).statusCode());
    }

    private HttpResponse<String> get(String path) throws Exception {
        return client.send(
                HttpRequest.newBuilder(URI.create(baseUrl + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> send(String method, String path, String body)
            throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(baseUrl + path));
        if (body == null) {
            request.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            request.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(body));
        }
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private int jsonId(String body) {
        return Integer.parseInt(body.replaceAll("(?s).*\"id\"\\s*:\\s*(\\d+).*", "$1"));
    }
}
