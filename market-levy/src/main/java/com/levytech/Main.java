
package com.levytech;

import com.levytech.db.Database;
import com.levytech.domain.Market;
import com.levytech.domain.Stall;
import com.levytech.domain.Trader;
import com.levytech.exception.ApiConflictException;
import com.levytech.exception.MarketNotFoundException;
import com.levytech.exception.PlanNotFoundException;
import com.levytech.exception.StallNotFoundException;
import com.levytech.exception.StallOccupiedException;
import com.levytech.exception.TraderNotFoundException;
import com.levytech.service.ApiPersistenceException;
import com.levytech.service.MarketService;
import com.levytech.service.PlanService;
import com.levytech.service.StallService;
import com.levytech.service.TraderService;
import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Entry point for the Market Levy REST API.
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws SQLException {
        Database database = new Database();
        database.initializeSchema();
        createApp(database).start(7000);
    }

    public static Javalin createApp(Database database) {
        MarketService markets = new MarketService(database);
        PlanService plans = new PlanService(database);
        TraderService traders = new TraderService(database);
        StallService stalls = new StallService(database);

        // Create the Javalin server and configure its API routes.
        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
            config.staticFiles.add("/public");
        });

        app.exception(BadRequestResponse.class, (e, ctx) ->
                ctx.status(400).json(new ApiError("Invalid request", e.getMessage())));
        app.exception(IllegalArgumentException.class, (e, ctx) ->
                ctx.status(400).json(new ApiError("Invalid request", e.getMessage())));
        app.exception(MarketNotFoundException.class, (e, ctx) ->
                ctx.status(404).json(new ApiError("Not found", e.getMessage())));
        app.exception(TraderNotFoundException.class, (e, ctx) ->
                ctx.status(404).json(new ApiError("Not found", e.getMessage())));
        app.exception(StallNotFoundException.class, (e, ctx) ->
                ctx.status(404).json(new ApiError("Not found", e.getMessage())));
        app.exception(PlanNotFoundException.class, (e, ctx) ->
                ctx.status(404).json(new ApiError("Not found", e.getMessage())));
        app.exception(ApiConflictException.class, (e, ctx) ->
                ctx.status(409).json(new ApiError("Conflict", e.getMessage())));
        app.exception(StallOccupiedException.class, (e, ctx) ->
                ctx.status(409).json(new ApiError("Conflict", e.getMessage())));
        app.exception(ApiPersistenceException.class, (e, ctx) -> {
            LOG.error("API database operation failed", e);
            ctx.status(500).json(new ApiError(
                    "Database error", "Unable to complete the request"));
        });

        // Health endpoint used to verify that the API is running.
        app.get("/api/health", ctx -> {
            ctx.json(new ApiResponse("OK", "Market Levy API is running"));
        });

        app.get("/api/markets", ctx -> ctx.json(markets.findAll()));
        app.get("/api/markets/{id}", ctx ->
                ctx.json(markets.findById(pathId(ctx))));
        app.post("/api/markets", ctx -> {
            MarketRequest request = marketRequest(ctx);
            Market market = markets.create(request.name(), request.location());
            ctx.status(201).json(market);
        });
        app.put("/api/markets/{id}", ctx -> {
            MarketRequest request = marketRequest(ctx);
            ctx.json(markets.update(
                    pathId(ctx), request.name(), request.location()));
        });
        app.delete("/api/markets/{id}", ctx -> {
            markets.delete(pathId(ctx));
            ctx.status(204);
        });

        app.get("/api/plans", ctx -> ctx.json(plans.findAll()));
        app.post("/api/plans", ctx -> {
            PlanRequest request = planRequest(ctx);
            ctx.status(201).json(plans.create(
                    request.name(),
                    request.planType(),
                    request.baseNgwee(),
                    request.surchargeNgwee(),
                    request.graceDays()
            ));
        });

        app.get("/api/traders", ctx -> ctx.json(traders.findAll()));
        app.get("/api/traders/{id}", ctx ->
                ctx.json(traders.findById(pathId(ctx))));
        app.post("/api/traders", ctx -> {
            TraderRequest request = traderRequest(ctx);
            Trader trader = traders.create(
                    request.fullName(),
                    request.phone(),
                    request.productCategory(),
                    request.planId(),
                    request.stallId(),
                    request.active()
            );
            ctx.status(201).json(trader);
        });
        app.put("/api/traders/{id}", ctx -> {
            TraderRequest request = traderRequest(ctx);
            ctx.json(traders.update(
                    pathId(ctx),
                    request.fullName(),
                    request.phone(),
                    request.productCategory(),
                    request.planId(),
                    request.stallId(),
                    request.active()
            ));
        });
        app.delete("/api/traders/{id}", ctx -> {
            traders.delete(pathId(ctx));
            ctx.status(204);
        });

        app.get("/api/stalls", ctx -> ctx.json(stalls.findAll()));
        app.get("/api/stalls/{id}", ctx ->
                ctx.json(stalls.findById(pathId(ctx))));
        app.post("/api/stalls", ctx -> {
            StallRequest request = stallRequest(ctx);
            Stall stall = stalls.create(
                    request.code(), request.section(), request.sizeSqm());
            ctx.status(201).json(stall);
        });
        app.put("/api/stalls/{id}", ctx -> {
            StallRequest request = stallRequest(ctx);
            ctx.json(stalls.update(
                    pathId(ctx), request.code(), request.section(), request.sizeSqm()));
        });
        app.delete("/api/stalls/{id}", ctx -> {
            stalls.delete(pathId(ctx));
            ctx.status(204);
        });

        return app;
    }

    private static int pathId(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (id <= 0) {
                throw new IllegalArgumentException(
                        "ID must be a positive integer");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "ID must be a positive integer", e);
        }
    }

    private static MarketRequest marketRequest(Context ctx) {
        MarketRequest request = ctx.bodyAsClass(MarketRequest.class);
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        return request;
    }

    private static TraderRequest traderRequest(Context ctx) {
        TraderRequest request = ctx.bodyAsClass(TraderRequest.class);
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        return request;
    }

    private static StallRequest stallRequest(Context ctx) {
        StallRequest request = ctx.bodyAsClass(StallRequest.class);
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        return request;
    }

    private static PlanRequest planRequest(Context ctx) {
        PlanRequest request = ctx.bodyAsClass(PlanRequest.class);
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        return request;
    }

    public record MarketRequest(String name, String location) {
    }

    public record TraderRequest(
            String fullName,
            String phone,
            String productCategory,
            int planId,
            Integer stallId,
            Boolean active
    ) {
    }

    public record StallRequest(String code, String section, double sizeSqm) {
    }

    public record PlanRequest(
            String name,
            String planType,
            long baseNgwee,
            long surchargeNgwee,
            int graceDays
    ) {
    }

    public record ApiError(String error, String message) {
    }

    /**
     * Simple response structure returned by the health endpoint.
     */
    public record ApiResponse(String status, String message) {
    }
}
