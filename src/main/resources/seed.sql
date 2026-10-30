-- Insert 3 levy plans
INSERT OR IGNORE INTO plans (id, name, daily_rate_ngwee, description)
VALUES
    (1, 'Basic', 500, 'Basic daily market levy'),
    (2, 'Standard', 1000, 'Standard daily market levy'),
    (3, 'Premium', 1500, 'Premium daily market levy');

-- Insert 8 market stalls
INSERT OR IGNORE INTO stalls (id, stall_number, location, status)
VALUES
    (1, 'S001', 'Block A', 'OCCUPIED'),
    (2, 'S002', 'Block A', 'OCCUPIED'),
    (3, 'S003', 'Block A', 'OCCUPIED'),
    (4, 'S004', 'Block B', 'OCCUPIED'),
    (5, 'S005', 'Block B', 'OCCUPIED'),
    (6, 'S006', 'Block B', 'AVAILABLE'),
    (7, 'S007', 'Block C', 'AVAILABLE'),
    (8, 'S008', 'Block C', 'AVAILABLE');

-- Insert 5 sample traders
INSERT OR IGNORE INTO traders
    (id, full_name, phone_number, stall_id, plan_id, balance_ngwee)
VALUES
    (1, 'John Banda', '0970000001', 1, 1, 10000),
    (2, 'Mary Phiri', '0970000002', 2, 2, 20000),
    (3, 'Peter Zulu', '0970000003', 3, 1, 15000),
    (4, 'Grace Tembo', '0970000004', 4, 3, 30000),
    (5, 'David Mwila', '0970000005', 5, 2, 25000);
