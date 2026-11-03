Project Contract: Market Levy & Stall Management System

This document is the single source of truth for the technical implementation. If code contradicts this contract, the code must change.

1. Project Requirements (What the system does)

- Register traders and allocate stalls (no two active traders in one stall).
- Support different levy plans (Flat and Base-Plus-Surcharge).
- Record prepaid balances and payments.
- Automatically calculate billing charges per cycle.
- Track arrears when traders cannot pay.
- Maintain a ledger of all transactions.
- Generate arrears reports.

2. Technical Rules (How we build it)

- Money: All money is stored as long representing Ngwee (100 ngwee = K1). Never use double or float.
- Billing Cycle: Uses the YYYY-MM format (e.g., 2026-11).
- Duplicate Billing: A trader cannot be billed twice for the same cycle. The system throws CycleAlreadyBilledException and returns HTTP 409.
- Failed Billing: If the balance is insufficient, the balance remains unchanged, and the full charge is recorded as arrears.
- Arrears Dates: Preserve the original arrears_since date when new arrears are added.
- Payments & Settling: Payments credit the balance and settle arrears via BillingService.settleArrears(). The settlement creates a single aggregate DEDUCTION ledger entry.
- Transactions: DAOs take a Connection but never commit. Service classes (BillingService, PaymentService) own the transaction and handle commit() and rollback().

3. Team Responsibilities

- Tatenda: LevyPlan, LevyPlanFactory, BillingService, PaymentService.
- Emmanuel: Database schema, seed data, database connections, and DAOs.
- Elijah: Market class, API Server, API routes.
- Sichela: Exceptions, LedgerEntry, Payment, ArrearsReport.
- Troy: Frontend HTML/JS, Reports, README, Video.

4. Exact Method Names & Signatures (Domain Classes)

Because these classes are shared across the team, the method names are locked.

Trader (Owner: Tatenda / Elijah)

- long getBalanceNgwee()
- void credit(long amount)
- void debit(long amount)
- void recordArrears(long amount, LocalDate cycleDate)
- void reduceArrears(long amount)

Stall (Owner: Tatenda / Elijah)

- boolean isOccupied()
- void occupy(int traderId) throws StallOccupiedException
- void vacate()

LevyPlan Interface (Owner: Tatenda)

- long calculateChargeNgwee()
- int getGraceDays()

5. Database Schema (Owner: Emmanuel)

- plans: id, name, plan_type (FLAT or BASE_PLUS_SURCHARGE), rate_ngwee, surcharge_ngwee, grace_days
- traders: id, full_name, phone, product_category, active, plan_id, stall_id, balance_ngwee, arrears_ngwee, arrears_since
- stalls: id, code, section, size_sqm (Occupancy is determined by traders.stall_id, not a status column).
- payments: id, trader_id, amount_ngwee, paid_on, reference
- ledger_entries: id, trader_id, entry_type (CREDIT, DEDUCTION, ARREARS), amount_ngwee, balance_after_ngwee, cycle, entry_date, description

6. DAO Methods (Owner: Emmanuel)

All DAO methods take Connection c as the first parameter and never commit.

- TraderDao.findById(c, id)
- TraderDao.update(c, trader)
- PlanDao.findById(c, id)
- LedgerDao.cycleAlreadyBilled(c, traderId, cycle)
- LedgerDao.insert(c, entry)
- PaymentDao.insert(c, payment)

7. Market API

- Markets have an auto-generated integer `id` and required `name` and
  `location` string fields.
- `GET /api/markets` returns all markets ordered by ID with HTTP 200.
- `GET /api/markets/{id}` returns one market with HTTP 200, or HTTP 404 if
  it does not exist.
- `POST /api/markets` accepts a JSON object with `name` and `location`,
  returning the created market with HTTP 201.
- `PUT /api/markets/{id}` accepts the same JSON object and returns the
  updated market with HTTP 200, or HTTP 404 if it does not exist.
- `DELETE /api/markets/{id}` returns HTTP 204 on success, or HTTP 404 if it
  does not exist.
- Invalid IDs and missing or blank fields return HTTP 400. Error responses
  are JSON objects containing `error` and `message`.

8. Levy Plan, Trader, and Stall APIs

- `GET /api/plans` returns configured levy plan options for trader forms.
- `POST /api/plans` accepts `name`, `planType` (`FLAT` or
  `BASE_PLUS_SURCHARGE`), `baseNgwee`, `surchargeNgwee`, and `graceDays`,
  returning the created plan with HTTP 201. Flat plans require a zero
  surcharge. Plan amounts are whole Ngwee.
- Trader requests use `fullName`, optional `phone`, `productCategory`,
  `planId`, optional `stallId`, and optional `active`.
- `GET /api/traders` and `GET /api/traders/{id}` return trader records.
- `POST /api/traders` creates a trader and returns HTTP 201. Account balance
  and arrears start at zero and are not writable through profile requests.
- `PUT /api/traders/{id}` updates profile and assignment fields, preserving
  account balance and arrears.
- `DELETE /api/traders/{id}` deactivates the trader, releases their stall,
  and returns HTTP 204 so payment and ledger history remain intact.
- An active trader can be assigned with `stallId`; an occupied stall returns
  HTTP 409. Setting a trader inactive clears their stall assignment.
- Stall requests use `code`, `section`, and positive `sizeSqm` fields.
  Occupancy is derived from the active trader's `stallId`.
- Stall routes support `GET /api/stalls`, `GET /api/stalls/{id}`,
  `POST /api/stalls`, `PUT /api/stalls/{id}`, and
  `DELETE /api/stalls/{id}`. Create returns HTTP 201, update returns HTTP 200,
  and delete returns HTTP 204.
- Deleting an assigned stall returns HTTP 409. Unknown resources return
  HTTP 404; malformed or invalid request fields return HTTP 400.

9. Running the application

- From the `market-levy` directory, run `mvn clean test` to compile and test.
- Run `mvn compile exec:java` or launch `com.levytech.Main` from the IDE after
  building the Maven project. The compile step copies the frontend files into
  the runtime classpath used by Javalin's static file handler.
- The API and frontend are served from `http://localhost:7000`; the UI uses
  same-origin API requests and does not require CORS configuration.
- Create a levy plan from the Plans page before registering traders.