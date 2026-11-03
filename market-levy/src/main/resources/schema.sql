-- Stores market locations managed by the API.
CREATE TABLE IF NOT EXISTS markets (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    location TEXT NOT NULL
);

-- Stores the levy plans available to traders.
CREATE TABLE IF NOT EXISTS plans (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    plan_type TEXT NOT NULL
        CHECK (plan_type IN ('FLAT', 'BASE_PLUS_SURCHARGE')),
    base_ngwee INTEGER NOT NULL CHECK (base_ngwee >= 0),
    surcharge_ngwee INTEGER NOT NULL DEFAULT 0
        CHECK (surcharge_ngwee >= 0),
    grace_days INTEGER NOT NULL DEFAULT 0 CHECK (grace_days >= 0)
);

-- Stores market stalls. Current occupancy is derived from traders.stall_id.
CREATE TABLE IF NOT EXISTS stalls (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code TEXT NOT NULL UNIQUE,
    section TEXT NOT NULL,
    size_sqm REAL NOT NULL CHECK (size_sqm > 0)
);

-- Stores trader details, their assigned plan and stall,
-- prepaid balance, arrears, and account status.
CREATE TABLE IF NOT EXISTS traders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name TEXT NOT NULL,
    phone TEXT UNIQUE,
    product_category TEXT NOT NULL,
    plan_id INTEGER NOT NULL,
    balance_ngwee INTEGER NOT NULL DEFAULT 0
        CHECK (balance_ngwee >= 0),
    arrears_ngwee INTEGER NOT NULL DEFAULT 0
        CHECK (arrears_ngwee >= 0),
    arrears_since TEXT,
    stall_id INTEGER UNIQUE,
    active INTEGER NOT NULL DEFAULT 1 CHECK (active IN (0, 1)),
    FOREIGN KEY (plan_id) REFERENCES plans(id),
    FOREIGN KEY (stall_id) REFERENCES stalls(id)
);

-- Records payments received from traders.
CREATE TABLE IF NOT EXISTS payments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    trader_id INTEGER NOT NULL,
    amount_ngwee INTEGER NOT NULL CHECK (amount_ngwee > 0),
    paid_on TEXT NOT NULL,
    cycle TEXT NOT NULL,
    FOREIGN KEY (trader_id) REFERENCES traders(id)
);

-- Maintains the financial history of credits, deductions,
-- and unpaid billing charges.
CREATE TABLE IF NOT EXISTS ledger_entries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    trader_id INTEGER NOT NULL,
    entry_type TEXT NOT NULL
        CHECK (entry_type IN ('CREDIT', 'DEDUCTION', 'ARREARS')),
    amount_ngwee INTEGER NOT NULL CHECK (amount_ngwee >= 0),
    balance_after_ngwee INTEGER NOT NULL CHECK (balance_after_ngwee >= 0),
    cycle TEXT,
    entry_date TEXT NOT NULL,
    description TEXT NOT NULL,
    FOREIGN KEY (trader_id) REFERENCES traders(id)
);

-- Prevents a trader from receiving duplicate billing entries
-- for the same cycle while allowing non-billing entries.
CREATE UNIQUE INDEX IF NOT EXISTS uq_ledger_billing_cycle
ON ledger_entries(trader_id, cycle)
WHERE cycle IS NOT NULL
  AND entry_type IN ('DEDUCTION', 'ARREARS');