-- ============================================================
-- V1: Initial Schema
-- Creates core tables: products, product_images, users
-- ============================================================

-- ------------------------------------
-- products
-- Locked columns (erp_id, name, price, stock) are owned by the ERPNext sync job.
-- Editable columns (web_description, is_top_selling) are maintained by the app.
-- ------------------------------------
CREATE TABLE products (
    id              BIGSERIAL      NOT NULL PRIMARY KEY,
    erp_id          VARCHAR(255)   NOT NULL UNIQUE,
    name            VARCHAR(500)   NOT NULL,
    price           NUMERIC(19, 2) NOT NULL,
    stock           INTEGER        NOT NULL DEFAULT 0,
    web_description TEXT,
    is_top_selling  BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_products_erp_id ON products (erp_id);

-- ------------------------------------
-- product_images
-- Stores S3 image URLs (enrichment data) for each product.
-- ------------------------------------
CREATE TABLE product_images (
    product_id BIGINT        NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    image_url  VARCHAR(1024) NOT NULL
);

-- ------------------------------------
-- users
-- Application users authenticated via phone OTP.
-- ------------------------------------
CREATE TABLE users (
    id         BIGSERIAL   NOT NULL PRIMARY KEY,
    phone      VARCHAR(20) NOT NULL UNIQUE,
    role       VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_phone ON users (phone);
