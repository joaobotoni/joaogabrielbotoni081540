CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(255) UNIQUE NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE refresh_token
(
    uuid       UUID PRIMARY KEY,
    id_user    UUID         NOT NULL,
    token      VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (id_user) REFERENCES users (id)
);