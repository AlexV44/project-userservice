--liquibase formatted sql

--changeset alexv44:001_create_users_table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL CONSTRAINT uk_users_email UNIQUE,
    status VARCHAR(255) NOT NULL DEFAULT 'PENDING',
    birthday DATE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

--rollback DROP TABLE users;