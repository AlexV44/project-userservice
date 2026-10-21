--liquibase formatted sql

--changeset alexv44:002_create_payment_cards_table
CREATE TABLE payment_cards (
    id UUID PRIMARY KEY,
    number VARCHAR(255) NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    holder VARCHAR(255) NOT NULL,
    expiration_date DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_payment_cards_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_payment_cards_user_id ON payment_cards(user_id);

--rollback DROP TABLE payment_cards;