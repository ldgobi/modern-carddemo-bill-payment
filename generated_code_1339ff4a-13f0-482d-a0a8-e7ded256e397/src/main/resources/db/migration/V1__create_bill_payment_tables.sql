-- Create accounts table
CREATE TABLE accounts (
    account_id VARCHAR(11) NOT NULL PRIMARY KEY,
    current_balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create card_cross_reference table
CREATE TABLE card_cross_reference (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    account_id VARCHAR(11) NOT NULL,
    card_number VARCHAR(16) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_card_xref_account_id ON card_cross_reference(account_id);

-- Create transactions table
CREATE TABLE transactions (
    transaction_id VARCHAR(16) NOT NULL PRIMARY KEY,
    transaction_type_code VARCHAR(2) NOT NULL,
    transaction_category_code INT NOT NULL,
    transaction_source VARCHAR(10) NOT NULL,
    transaction_description VARCHAR(100) NOT NULL,
    transaction_amount DECIMAL(19, 2) NOT NULL,
    card_number VARCHAR(16) NOT NULL,
    merchant_id BIGINT NOT NULL,
    merchant_name VARCHAR(50) NOT NULL,
    merchant_city VARCHAR(50) NOT NULL,
    merchant_zip VARCHAR(10) NOT NULL,
    origin_timestamp TIMESTAMP NOT NULL,
    process_timestamp TIMESTAMP NOT NULL,
    account_id VARCHAR(11) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transactions_account_id ON transactions(account_id);

-- Add foreign key constraints
ALTER TABLE card_cross_reference
    ADD CONSTRAINT fk_card_xref_account
    FOREIGN KEY (account_id) REFERENCES accounts(account_id);

ALTER TABLE transactions
    ADD CONSTRAINT fk_transaction_account
    FOREIGN KEY (account_id) REFERENCES accounts(account_id);
