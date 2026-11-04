PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS plans (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    plan_type TEXT NOT NULL
        CHECK (plan_type IN ('FLAT', 'BASE_PLUS_SURCHARGE')),
    rate_ngwee INTEGER NOT NULL CHECK (rate_ngwee >= 0),
    surcharge_ngwee INTEGER NOT NULL DEFAULT 0
        CHECK (surcharge_ngwee >= 0),
    grace_days INTEGER NOT NULL DEFAULT 0
        CHECK (grace_days >= 0)
);

CREATE TABLE IF NOT EXISTS stalls (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code TEXT NOT NULL UNIQUE,
    section TEXT NOT NULL,
    size_sqm REAL NOT NULL CHECK (size_sqm > 0)
);

CREATE TABLE IF NOT EXISTS traders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name TEXT NOT NULL,
    phone TEXT NOT NULL UNIQUE,
    product_category TEXT NOT NULL,
    active INTEGER NOT NULL DEFAULT 1
        CHECK (active IN (0, 1)),
    plan_id INTEGER NOT NULL,
    stall_id INTEGER,
    balance_ngwee INTEGER NOT NULL DEFAULT 0
        CHECK (balance_ngwee >= 0),
    arrears_ngwee INTEGER NOT NULL DEFAULT 0
        CHECK (arrears_ngwee >= 0),
    arrears_since TEXT,
    FOREIGN KEY (plan_id) REFERENCES plans(id),
    FOREIGN KEY (stall_id) REFERENCES stalls(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_traders_active_stall
    ON traders(stall_id)
    WHERE active = 1 AND stall_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS payments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    trader_id INTEGER NOT NULL,
    amount_ngwee INTEGER NOT NULL CHECK (amount_ngwee > 0),
    paid_on TEXT NOT NULL,
    reference TEXT,
    FOREIGN KEY (trader_id) REFERENCES traders(id)
);

CREATE TABLE IF NOT EXISTS ledger_entries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    trader_id INTEGER NOT NULL,
    entry_type TEXT NOT NULL
        CHECK (entry_type IN ('CREDIT', 'DEDUCTION', 'ARREARS')),
    amount_ngwee INTEGER NOT NULL CHECK (amount_ngwee >= 0),
    balance_after_ngwee INTEGER NOT NULL
        CHECK (balance_after_ngwee >= 0),
    cycle TEXT,
    entry_date TEXT NOT NULL,
    description TEXT,
    FOREIGN KEY (trader_id) REFERENCES traders(id)
);

CREATE INDEX IF NOT EXISTS idx_ledger_entries_trader_id
    ON ledger_entries(trader_id);

CREATE INDEX IF NOT EXISTS idx_ledger_entries_cycle
    ON ledger_entries(trader_id, cycle);
