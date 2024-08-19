CREATE TABLE currencies
(
    id         BIGSERIAL PRIMARY KEY,
    code       VARCHAR(3) UNIQUE,
    amount     DECIMAL(8, 6),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE subscriptions
(
    id                     BIGSERIAL PRIMARY KEY,
    name                   VARCHAR(255)  NOT NULL,
    currency               VARCHAR(10)   NOT NULL,
    amount                 DECIMAL(8, 2) NOT NULL,
    converted_amount       DECIMAL(8, 2) NOT NULL,
    billing_cycle          INTEGER       NOT NULL,
    subscription_date      DATE          NOT NULL,
    next_subscription_date DATE          NOT NULL,
    created_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
