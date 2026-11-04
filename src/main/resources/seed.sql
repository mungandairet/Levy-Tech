-- Insert the two levy plans required by the domain classes.
INSERT OR IGNORE INTO plans (
    name, plan_type, rate_ngwee, surcharge_ngwee, grace_days
)
VALUES
    ('Flat Levy Plan', 'FLAT', 5000, 0, 3),
    ('Base Plus Surcharge Plan', 'BASE_PLUS_SURCHARGE', 5000, 2000, 3);

-- Insert 8 market stalls.
INSERT OR IGNORE INTO stalls (code, section, size_sqm)
VALUES
    ('S001', 'Block A', 6.0),
    ('S002', 'Block A', 6.0),
    ('S003', 'Block A', 6.0),
    ('S004', 'Block B', 6.0),
    ('S005', 'Block B', 6.0),
    ('S006', 'Block B', 6.0),
    ('S007', 'Block C', 6.0),
    ('S008', 'Block C', 6.0);

-- Insert 5 sample traders.
INSERT OR IGNORE INTO traders (
    full_name,
    phone,
    product_category,
    active,
    plan_id,
    stall_id,
    balance_ngwee,
    arrears_ngwee,
    arrears_since
)
VALUES
    (
        'John Banda', '0970000001', 'Vegetables', 1,
        (SELECT id FROM plans WHERE name = 'Flat Levy Plan'),
        (SELECT id FROM stalls WHERE code = 'S001'),
        10000, 0, NULL
    ),
    (
        'Mary Phiri', '0970000002', 'Groceries', 1,
        (SELECT id FROM plans WHERE name = 'Base Plus Surcharge Plan'),
        (SELECT id FROM stalls WHERE code = 'S002'),
        20000, 0, NULL
    ),
    (
        'Peter Zulu', '0970000003', 'Fish', 1,
        (SELECT id FROM plans WHERE name = 'Flat Levy Plan'),
        (SELECT id FROM stalls WHERE code = 'S003'),
        15000, 0, NULL
    ),
    (
        'Grace Tembo', '0970000004', 'Clothing', 1,
        (SELECT id FROM plans WHERE name = 'Base Plus Surcharge Plan'),
        (SELECT id FROM stalls WHERE code = 'S004'),
        30000, 0, NULL
    ),
    (
        'David Mwila', '0970000005', 'Fruits', 1,
        (SELECT id FROM plans WHERE name = 'Flat Levy Plan'),
        (SELECT id FROM stalls WHERE code = 'S005'),
        25000, 0, NULL
    );
