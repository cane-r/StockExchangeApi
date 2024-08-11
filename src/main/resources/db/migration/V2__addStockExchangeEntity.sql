CREATE TABLE stock_exchange_stock
(
    stock_exchange_id BIGINT NOT NULL,
    stock_id          BIGINT NOT NULL,
    PRIMARY KEY (stock_exchange_id, stock_id),
    FOREIGN KEY (stock_exchange_id) REFERENCES stock_exchange (id),
    FOREIGN KEY (stock_id) REFERENCES stock (id)
);
