
DROP DATABASE IF EXISTS framework_db;
CREATE DATABASE framework_db;


\c framework_db

CREATE TABLE produit (
    id    SERIAL PRIMARY KEY,
    nom   VARCHAR(100) NOT NULL,
    prix  NUMERIC(10,2) NOT NULL
);

INSERT INTO produit (nom, prix) VALUES
    ('Pizza',   12000.00),
    ('Frites',   4500.00),
    ('Boisson',  2000.00),
    ('Burger',   9500.00);

SELECT * FROM produit;
