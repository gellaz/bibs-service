-- ============================================================
-- TABLES
-- ============================================================

CREATE TABLE users
(
    id               UUID                        NOT NULL DEFAULT gen_random_uuid(),
    identity_subject TEXT                        NOT NULL,
    email            TEXT                        NOT NULL,
    first_name       TEXT,
    last_name        TEXT,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT unique_users_identity_subject UNIQUE (identity_subject),
    CONSTRAINT unique_users_email UNIQUE (email)
);

CREATE TABLE user_addresses
(
    id             UUID                        NOT NULL DEFAULT gen_random_uuid(),
    address_type   VARCHAR(100)                NOT NULL,
    label          TEXT,
    recipient_name TEXT,
    phone          TEXT,
    address_line1  TEXT                        NOT NULL,
    address_line2  TEXT,
    city           TEXT                        NOT NULL,
    zip_code       TEXT                        NOT NULL,
    province       TEXT,
    country        VARCHAR(2)                  NOT NULL DEFAULT 'IT',
    latitude       DOUBLE PRECISION,
    longitude      DOUBLE PRECISION,
    is_default     BOOLEAN                     NOT NULL,
    user_id        UUID,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_user_addresses PRIMARY KEY (id)
);

CREATE TABLE business_profiles
(
    id                      UUID                        NOT NULL DEFAULT gen_random_uuid(),
    vat_number              VARCHAR(11)                 NOT NULL,
    vat_verification_status VARCHAR(100)                NOT NULL DEFAULT 'PENDING',
    vat_verified_at         TIMESTAMP WITHOUT TIME ZONE,
    user_id                 UUID                        NOT NULL,
    created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_business_profiles PRIMARY KEY (id),
    CONSTRAINT unique_business_profiles_vat_number UNIQUE (vat_number),
    CONSTRAINT check_business_profiles_vat_status CHECK (vat_verification_status IN ('PENDING', 'VERIFIED', 'REJECTED'))
);

CREATE TABLE stores
(
    id            UUID                        NOT NULL DEFAULT gen_random_uuid(),
    name          TEXT                        NOT NULL,
    description   TEXT,
    address_line1 TEXT                        NOT NULL,
    address_line2 TEXT,
    city          TEXT                        NOT NULL,
    zip_code      TEXT                        NOT NULL,
    province      TEXT,
    country       VARCHAR(2)                  NOT NULL DEFAULT 'IT',
    latitude      DOUBLE PRECISION,
    longitude     DOUBLE PRECISION,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_stores PRIMARY KEY (id)
);

CREATE TABLE store_members
(
    store_id UUID NOT NULL,
    user_id  UUID NOT NULL,
    CONSTRAINT pk_store_members PRIMARY KEY (store_id, user_id)
);

CREATE TABLE products
(
    id               UUID                        NOT NULL DEFAULT gen_random_uuid(),
    title            TEXT                        NOT NULL,
    description      TEXT,
    is_active        BOOLEAN                     NOT NULL DEFAULT TRUE,
    product_stock_id UUID                        NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_products PRIMARY KEY (id),
    CONSTRAINT unique_products_product_stock_id UNIQUE (product_stock_id)
);

CREATE TABLE product_categories
(
    id         UUID                        NOT NULL DEFAULT gen_random_uuid(),
    name       TEXT                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_product_categories PRIMARY KEY (id),
    CONSTRAINT unique_product_categories_name UNIQUE (name)
);

CREATE TABLE product_classifications
(
    product_id          UUID NOT NULL,
    product_category_id UUID NOT NULL,
    CONSTRAINT pk_product_classifications PRIMARY KEY (product_id, product_category_id)
);

CREATE TABLE product_stocks
(
    id                 UUID                        NOT NULL DEFAULT gen_random_uuid(),
    quantity_available INTEGER                     NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_product_stocks PRIMARY KEY (id)
);

CREATE TABLE orders
(
    id             UUID                        NOT NULL DEFAULT gen_random_uuid(),
    order_type     VARCHAR(100)                NOT NULL,
    order_status   VARCHAR(100)                NOT NULL DEFAULT 'CREATED',
    subtotal_cents INTEGER                     NOT NULL DEFAULT 0,
    shipping_cents INTEGER                     NOT NULL DEFAULT 0,
    discount_cents INTEGER                     NOT NULL DEFAULT 0,
    total_cents    INTEGER                     NOT NULL DEFAULT 0,
    currency       VARCHAR(3)                  NOT NULL DEFAULT 'EUR',
    store_id       UUID                        NOT NULL,
    user_id        UUID                        NOT NULL,
    notes          TEXT,
    expires_at     TIMESTAMP WITHOUT TIME ZONE,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_orders PRIMARY KEY (id),
    CONSTRAINT check_orders_order_type CHECK (order_type IN ('DIRECT_PURCHASE', 'RESERVE_AND_PICKUP', 'PAY_AND_PICKUP',
                                                             'PAY_AND_SHIP')),
    CONSTRAINT check_orders_order_status CHECK (order_status IN
                                                ('CREATED', 'RESERVED', 'PAID', 'READY_FOR_PICKUP', 'COMPLETED',
                                                 'CANCELLED', 'EXPIRED', 'REFUNDED')),
    CONSTRAINT check_orders_subtotal_cents CHECK (subtotal_cents >= 0),
    CONSTRAINT check_orders_shipping_cents CHECK (shipping_cents >= 0),
    CONSTRAINT check_orders_discount_cents CHECK (discount_cents >= 0),
    CONSTRAINT check_orders_total_cents CHECK (total_cents >= 0),
    CONSTRAINT chk_orders_totals CHECK (total_cents = subtotal_cents + shipping_cents - discount_cents AND
                                        total_cents >= 0)
);

CREATE TABLE order_items
(
    id               UUID                        NOT NULL DEFAULT gen_random_uuid(),
    quantity         INTEGER                     NOT NULL,
    unit_price_cents INTEGER                     NOT NULL,
    currency         VARCHAR(3)                  NOT NULL DEFAULT 'EUR',
    order_id         UUID                        NOT NULL,
    product_id       UUID                        NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_order_items PRIMARY KEY (id),
    CONSTRAINT check_order_items_quantity CHECK (quantity > 0),
    CONSTRAINT check_order_items_unit_price_cents CHECK (unit_price_cents >= 0)
);

CREATE TABLE order_fulfillments
(
    id              UUID                        NOT NULL DEFAULT gen_random_uuid(),
    method          VARCHAR(100)                NOT NULL,
    pickup_code     VARCHAR(255),
    pickup_deadline TIMESTAMP WITHOUT TIME ZONE,
    order_id        UUID                        NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_order_fulfillments PRIMARY KEY (id),
    CONSTRAINT chk_fulfillment_method CHECK (method IN ('PICKUP', 'SHIPMENT'))
);

CREATE TABLE shipments
(
    id                  UUID                        NOT NULL DEFAULT gen_random_uuid(),
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
    created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_shipments PRIMARY KEY (id),
    CONSTRAINT unique_shipments_order_id UNIQUE (order_id)

);

CREATE TABLE loyalty_accounts
(
    id             UUID                        NOT NULL DEFAULT gen_random_uuid(),
    points_balance INTEGER                     NOT NULL,
    user_id        UUID                        NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT loyalty_accounts_pkey PRIMARY KEY (id),
    CONSTRAINT unique_loyalty_accounts_user_id UNIQUE (user_id)
);

CREATE TABLE loyalty_point_transactions
(
    id         UUID                        NOT NULL DEFAULT gen_random_uuid(),
    tx_type    VARCHAR(100)                NOT NULL,
    points     INTEGER                     NOT NULL,
    reason     TEXT,
    user_id    UUID                        NOT NULL,
    order_id   UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT loyalty_point_transactions_pkey PRIMARY KEY (id),
    CONSTRAINT check_loyalty_point_transactions_tx_type CHECK (tx_type IN ('EARN', 'SPEND', 'ADJUST', 'EXPIRE')),
    CONSTRAINT check_loyalty_point_transactions_points CHECK (points <> 0)
);

-- ============================================================
-- FOREIGN KEY CONSTRAINTS
-- ============================================================

ALTER TABLE user_addresses
    ADD CONSTRAINT fk_user_addresses_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE business_profiles
    ADD CONSTRAINT fk_business_profiles_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE business_profiles
    ADD CONSTRAINT unique_business_profiles_user_id UNIQUE (user_id);

ALTER TABLE store_members
    ADD CONSTRAINT fk_store_members_store_id FOREIGN KEY (store_id) REFERENCES stores (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE store_members
    ADD CONSTRAINT fk_store_members_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE products
    ADD CONSTRAINT fk_products_product_stock_id FOREIGN KEY (product_stock_id) REFERENCES product_stocks (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE product_classifications
    ADD CONSTRAINT fk_product_classifications_product_id FOREIGN KEY (product_id) REFERENCES products (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE product_classifications
    ADD CONSTRAINT fk_product_classifications_product_category_id FOREIGN KEY (product_category_id) REFERENCES product_categories (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

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

ALTER TABLE loyalty_accounts
    ADD CONSTRAINT fk_loyalty_accounts_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE loyalty_point_transactions
    ADD CONSTRAINT fk_loyalty_point_transactions_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE loyalty_point_transactions
    ADD CONSTRAINT fk_loyalty_point_transactions_order_id FOREIGN KEY (order_id) REFERENCES orders (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
