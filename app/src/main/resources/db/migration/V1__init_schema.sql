CREATE TABLE users (
    id            VARCHAR(36) PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    name          VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(32)  NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE products (
    id             VARCHAR(36) PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    description    VARCHAR(2000) NOT NULL,
    price_amount   NUMERIC(19, 2) NOT NULL,
    price_currency VARCHAR(3)   NOT NULL,
    stock          INT          NOT NULL
);

CREATE TABLE orders (
    id             VARCHAR(36) PRIMARY KEY,
    user_id        VARCHAR(36)  NOT NULL,
    status         VARCHAR(32)  NOT NULL,
    total_amount   NUMERIC(19, 2) NOT NULL,
    total_currency VARCHAR(3)   NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_orders_user_id ON orders (user_id);

CREATE TABLE order_items (
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
