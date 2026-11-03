INSERT OR IGNORE INTO plans (
    name,
    plan_type,
    rate_ngwee,
    surcharge_ngwee,
    grace_days
)
VALUES
    (
        'Flat Levy Plan',
        'FLAT',
        5000,
        0,
        3
    ),
    (
        'Base Plus Surcharge Plan',
        'BASE_PLUS_SURCHARGE',
        5000,
        2000,
        3
    );