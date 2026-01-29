CREATE TABLE users
(
    id_user    UUID PRIMARY KEY,
    username   VARCHAR(255) UNIQUE NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE artist
(
    id_artist UUID PRIMARY KEY,
    name      VARCHAR(255) NOT NULL
);

CREATE TABLE album
(
    id_album UUID PRIMARY KEY,
    title    VARCHAR(255) NOT NULL
);

CREATE TABLE artist_album
(
    id_artist UUID NOT NULL,
    id_album  UUID NOT NULL,
    PRIMARY KEY (id_artist, id_album),
    FOREIGN KEY (id_artist) REFERENCES artist(id_artist) ON DELETE CASCADE,
    FOREIGN KEY (id_album) REFERENCES album(id_album) ON DELETE CASCADE
);

CREATE TABLE regional(
    id_regional INTEGER PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    ativo BOOLEAN NOT NULL
);