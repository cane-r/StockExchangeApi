INSERT INTO stock (NAME, DESCRIPTION, PRICE, CREATED_DATE)
VALUES ('ING', 'ING Bank', 22.22, CURRENT_TIMESTAMP),
       ('TCELL', 'Turkcell A.Ş', 333.00, CURRENT_TIMESTAMP),
       ('THY', 'Turkish Airlines', 333.00, CURRENT_TIMESTAMP),
       ('CCOLA', 'Coca Cola Inc.', 444.00, CURRENT_TIMESTAMP),
       ('ASELS', 'Aselsan A.Ş', 555.00, CURRENT_TIMESTAMP),
       ('HEPS', 'HepsiBurada', 666.66, CURRENT_TIMESTAMP),
       ('TSLA', 'Tesla Inc.', 1222.21, CURRENT_TIMESTAMP),
       ('MSFT', 'Microsoft Corporation', 4551.11, CURRENT_TIMESTAMP),
       ('GOOG', 'Alphabet Inc.', 0.12356, CURRENT_TIMESTAMP);

INSERT INTO stock_exchange (NAME, DESCRIPTION,CREATED_DATE)
VALUES ('BIST30', 'BIST30 Borsa Istanbul Exchange',CURRENT_TIMESTAMP),
       ('NASDAQ', 'US Exchange',CURRENT_TIMESTAMP ),
       ('Nikkei225', 'Japan Exchange', CURRENT_TIMESTAMP);

INSERT INTO stock_exchange_stock (stock_exchange_id, stock_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (2, 7),
       (2, 8),
       (2, 9);
