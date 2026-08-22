ALTER TABLE products ADD CONSTRAINT chk_products_stock_non_negative CHECK (stock >= 0);
ALTER TABLE products ADD CONSTRAINT chk_products_price_non_negative CHECK (price_amount >= 0);
ALTER TABLE order_items ADD CONSTRAINT chk_order_items_quantity_positive CHECK (quantity > 0);
