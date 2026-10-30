PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS plans (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    daily_rate_ngwee INTEGER NOT NULL CHECK (daily_rate_ngwee >= 0),
    description TEXT
);

CREATE TABLE IF NOT EXISTS stalls (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    stall_number TEXT NOT NULL UNIQUE,
    location TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'AVAILABLE'
        CHECK (status IN ('AVAILABLE', 'OCCUPIED', 'MAINTENANCE'))
);

CREATE TABLE IF NOT EXISTS traders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name TEXT NOT NULL,
    phone_number TEXT UNIQUE,
    stall_id INTEGER UNIQUE,
    plan_id INTEGER NOT NULL,
    balance_ngwee INTEGER NOT NULL DEFAULT 0,
    arrears_since TEXT,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (stall_id) REFERENCES stalls(id),
    FOREIGN KEY (plan_id) REFERENCES plans(id)
);

CREATE TABLE IF NOT EXISTS billing_cycles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    trader_id INTEGER NOT NULL,
    cycle TEXT NOT NULL,
    charge_ngwee INTEGER NOT NULL CHECK (charge_ngwee >= 0),
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (trader_id, cycle),
    FOREIGN KEY (trader_id) REFERENCES traders(id)
);

CREATE TABLE IF NOT EXISTS ledger_entries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    trader_id INTEGER NOT NULL,
    entry_type TEXT NOT NULL
        CHECK (entry_type IN ('CREDIT', 'DEDUCTION', 'ARREARS')),
    amount_ngwee INTEGER NOT NULL CHECK (amount_ngwee >= 0),
    description TEXT,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trader_id) REFERENCES traders(id)
);

CREATE INDEX IF NOT EXISTS idx_ledger_entries_trader_id
    ON ledger_entries(trader_id);

CREATE INDEX IF NOT EXISTS idx_billing_cycles_trader_id
    ON billing_cycles(trader_id);