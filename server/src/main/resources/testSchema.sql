DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT PRIMARY KEY AUTO_INCREMENT,
    name  VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    description  VARCHAR(200),
    requester_id BIGINT,
    created      TIMESTAMP,
    FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    available   BOOLEAN      NOT NULL,
    owner_id    BIGINT,
    request_id  BIGINT,
    FOREIGN KEY (owner_id) REFERENCES users (id),
    FOREIGN KEY (request_id) REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_start TIMESTAMP,
    booking_end   TIMESTAMP,
    item_id       BIGINT,
    user_id       BIGINT,
    status        INT,
    FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    text        VARCHAR(200),
    item_id     BIGINT,
    user_id     BIGINT,
    create_time TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);