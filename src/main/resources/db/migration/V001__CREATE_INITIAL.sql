CREATE TABLE users
(
    id               UUID                        NOT NULL,
    identity_subject TEXT                        NOT NULL,
    email            TEXT                        NOT NULL,
    first_name       TEXT,
    last_name        TEXT,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE user_addresses
(
    id             UUID                        NOT NULL,
    address_type   VARCHAR(100)                NOT NULL,
    label          TEXT,
    recipient_name TEXT,
    phone          TEXT,
    address_line1  TEXT                        NOT NULL,
    address_line2  TEXT,
    city           TEXT                        NOT NULL,
    zip_code       TEXT                        NOT NULL,
    province       TEXT,
    country        VARCHAR(2)                  NOT NULL,
    latitude       DOUBLE PRECISION,
    longitude      DOUBLE PRECISION,
    is_default     BOOLEAN                     NOT NULL,
    user_id        UUID,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT user_addresses_pkey PRIMARY KEY (id)
);

CREATE TABLE business_profiles
(
    id                      UUID                        NOT NULL,
    vat_number              VARCHAR(11)                 NOT NULL,
    vat_verification_status VARCHAR(100)                NOT NULL,
    vat_verified_at         TIMESTAMP WITHOUT TIME ZONE,
    user_id                 UUID                        NOT NULL,
    created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT business_profiles_pkey PRIMARY KEY (id)
);

CREATE TABLE stores
(
    id            UUID                        NOT NULL,
    name          TEXT                        NOT NULL,
    description   TEXT,
    address_line1 TEXT                        NOT NULL,
    address_line2 TEXT,
    city          TEXT                        NOT NULL,
    zip_code      TEXT                        NOT NULL,
    province      VARCHAR(255),
    country       VARCHAR(2)                  NOT NULL,
    latitude      DOUBLE PRECISION,
    longitude     DOUBLE PRECISION,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT stores_pkey PRIMARY KEY (id)
);

CREATE TABLE products
(
    id               UUID                        NOT NULL,
    title            TEXT                        NOT NULL,
    description      TEXT,
    is_active        BOOLEAN                     NOT NULL,
    product_stock_id UUID                        NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT products_pkey PRIMARY KEY (id)
);

CREATE TABLE product_categories
(
    id         UUID                        NOT NULL,
    name       VARCHAR(255)                NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT product_categories_pkey PRIMARY KEY (id)
);

CREATE TABLE product_stocks
(
    id                 UUID                        NOT NULL,
    quantity_available INTEGER                     NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT product_stocks_pkey PRIMARY KEY (id)
);

CREATE TABLE orders
(
    id             UUID                        NOT NULL,
    order_type     VARCHAR(100)                NOT NULL,
    order_status   VARCHAR(100)                NOT NULL,
    expires_at     TIMESTAMP WITHOUT TIME ZONE,
    notes          VARCHAR(255),
    subtotal_cents INTEGER                     NOT NULL,
    shipping_cents INTEGER                     NOT NULL,
    discount_cents INTEGER                     NOT NULL,
    total_cents    INTEGER                     NOT NULL,
    currency       VARCHAR(3)                  NOT NULL,
    store_id       UUID                        NOT NULL,
    user_id        UUID                        NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT orders_pkey PRIMARY KEY (id)
);

CREATE TABLE order_items
(
    id               UUID                        NOT NULL,
    quantity         INTEGER                     NOT NULL,
    unit_price_cents INTEGER                     NOT NULL,
    currency         VARCHAR(3)                  NOT NULL,
    order_id         UUID                        NOT NULL,
    product_id       UUID                        NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT order_items_pkey PRIMARY KEY (id)
);

CREATE TABLE order_fulfillments
(
    id              UUID                        NOT NULL,
    method          VARCHAR(100)                NOT NULL,
    pickup_code     VARCHAR(255),
    pickup_deadline TIMESTAMP WITHOUT TIME ZONE,
    order_id        UUID                        NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT order_fulfillments_pkey PRIMARY KEY (id)
);

CREATE TABLE shipments
(
    id                  UUID                        NOT NULL,
    recipient_name      VARCHAR(255)                NOT NULL,
    phone               VARCHAR(255),
    address_line1       VARCHAR(255)                NOT NULL,
    address_line2       VARCHAR(255),
    city                VARCHAR(255)                NOT NULL,
    zip_code            VARCHAR(255)                NOT NULL,
    province            VARCHAR(255),
    country             VARCHAR(2)                  NOT NULL,
    carrier             VARCHAR(255),
    tracking_code       VARCHAR(255),
    shipping_cost_cents INTEGER                     NOT NULL,
    status              VARCHAR(100)                NOT NULL,
    order_id            UUID                        NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT shipments_pkey PRIMARY KEY (id)
);

CREATE TABLE loyalty_accounts
(
    id             UUID                        NOT NULL,
    points_balance INTEGER                     NOT NULL,
    user_id        UUID                        NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT loyalty_accounts_pkey PRIMARY KEY (id)
);

CREATE TABLE loyalty_point_transactions
(
    id         UUID                        NOT NULL,
    tx_type    VARCHAR(100)                NOT NULL,
    points     INTEGER                     NOT NULL,
    reason     TEXT,
    user_id    UUID                        NOT NULL,
    order_id   UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT loyalty_point_transactions_pkey PRIMARY KEY (id)
);

CREATE TABLE store_members
(
    store_id UUID NOT NULL,
    user_id  UUID NOT NULL
);

CREATE TABLE product_classifications
(
    product_id          UUID NOT NULL,
    product_category_id UUID NOT NULL
);

ALTER TABLE users
    ADD CONSTRAINT unique_users_identity_subject UNIQUE (identity_subject);

ALTER TABLE users
    ADD CONSTRAINT unique_users_email UNIQUE (email);

ALTER TABLE user_addresses
    ADD CONSTRAINT fk_user_addresses_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE business_profiles
    ADD CONSTRAINT fk_business_profiles_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE business_profiles
    ADD CONSTRAINT unique_business_profiles_vat_number UNIQUE (vat_number);

ALTER TABLE business_profiles
    ADD CONSTRAINT unique_business_profiles_user_id UNIQUE (user_id);

ALTER TABLE products
    ADD CONSTRAINT fk_products_product_stock_id FOREIGN KEY (product_stock_id) REFERENCES product_stocks (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE products
    ADD CONSTRAINT unique_products_product_stock_id UNIQUE (product_stock_id);

ALTER TABLE product_categories
    ADD CONSTRAINT unique_product_categories_name UNIQUE (name);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_store_id FOREIGN KEY (store_id) REFERENCES stores (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_order_id FOREIGN KEY (order_id) REFERENCES orders (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_product_id FOREIGN KEY (product_id) REFERENCES products (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE order_fulfillments
    ADD CONSTRAINT fk_order_fulfillments_order_id FOREIGN KEY (order_id) REFERENCES orders (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE shipments
    ADD CONSTRAINT fk_shipments_order_id FOREIGN KEY (order_id) REFERENCES orders (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE shipments
    ADD CONSTRAINT unique_shipments_order_id UNIQUE (order_id);

ALTER TABLE loyalty_accounts
    ADD CONSTRAINT fk_loyalty_accounts_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE loyalty_accounts
    ADD CONSTRAINT unique_loyalty_accounts_user_id UNIQUE (user_id);

ALTER TABLE loyalty_point_transactions
    ADD CONSTRAINT fk_loyalty_point_transactions_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE loyalty_point_transactions
    ADD CONSTRAINT fk_loyalty_point_transactions_order_id FOREIGN KEY (order_id) REFERENCES orders (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE store_members
    ADD CONSTRAINT pk_store_members PRIMARY KEY (store_id, user_id);

ALTER TABLE store_members
    ADD CONSTRAINT fk_store_members_store_id FOREIGN KEY (store_id) REFERENCES stores (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE store_members
    ADD CONSTRAINT fk_store_members_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE product_classifications
    ADD CONSTRAINT pk_product_classifications PRIMARY KEY (product_id, product_category_id);

ALTER TABLE product_classifications
    ADD CONSTRAINT fk_product_classifications_product_id FOREIGN KEY (product_id) REFERENCES products (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE product_classifications
    ADD CONSTRAINT fk_product_classifications_product_category_id FOREIGN KEY (product_category_id) REFERENCES product_categories (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
