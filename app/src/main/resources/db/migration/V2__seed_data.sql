INSERT INTO users (id, email, name, password_hash, role, created_at) VALUES
    ('00000000-0000-0000-0000-000000000001',
     'demo@example.com',
     'Demo User',
     '$2a$10$rN6NY395V9uszxxN3QEGu.maNnA7xSWa6oEmHZe6DBNryOXAfKVd.',
     'CUSTOMER',
     NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO products (id, name, description, price_amount, price_currency, stock) VALUES
    ('10000000-0000-0000-0000-000000000001', 'Espresso',
     'Single origin espresso shot', 12.50, 'USD', 100),
    ('10000000-0000-0000-0000-000000000002', 'Cappuccino',
     'Espresso with steamed milk foam', 4.50, 'USD', 100),
    ('10000000-0000-0000-0000-000000000003', 'Croissant',
     'Buttery French croissant', 3.75, 'USD', 50),
    ('10000000-0000-0000-0000-000000000004', 'Green Tea',
     'Premium loose-leaf green tea', 3.00, 'USD', 80)
ON CONFLICT (id) DO NOTHING;
