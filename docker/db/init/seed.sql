CREATE TABLE IF NOT EXISTS users (
    id            VARCHAR(36) PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    name          VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(32)  NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    id             VARCHAR(36) PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    description    VARCHAR(2000) NOT NULL,
    price_amount   NUMERIC(19, 2) NOT NULL,
    price_currency VARCHAR(3)   NOT NULL,
    stock          INT          NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id             VARCHAR(36) PRIMARY KEY,
    user_id        VARCHAR(36)  NOT NULL,
    status         VARCHAR(32)  NOT NULL,
    total_amount   NUMERIC(19, 2) NOT NULL,
    total_currency VARCHAR(3)   NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders (user_id);

CREATE TABLE IF NOT EXISTS order_items (
    id                 BIGSERIAL PRIMARY KEY,
    order_id           VARCHAR(36)  NOT NULL,
    product_id         VARCHAR(36)  NOT NULL,
    name               VARCHAR(255) NOT NULL,
    quantity           INT          NOT NULL,
    unit_price_amount  NUMERIC(19, 2) NOT NULL,
    unit_price_currency VARCHAR(3)   NOT NULL,
    subtotal_amount    NUMERIC(19, 2) NOT NULL,
    subtotal_currency  VARCHAR(3)   NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id)
);

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
