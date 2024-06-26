DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id
    BIGINT
    PRIMARY
    KEY
    GENERATED
    BY
    DEFAULT AS
    IDENTITY,
    name
    VARCHAR
(
    100
) NOT NULL,
    email VARCHAR
(
    100
) UNIQUE
    );

CREATE TABLE IF NOT EXISTS requests
(
    id
    BIGINT
    PRIMARY
    KEY
    GENERATED
    BY
    DEFAULT AS
    IDENTITY,
    description
    VARCHAR
(
    200
),
    requester_id BIGINT REFERENCES users
(
    id
) ON DELETE CASCADE,
    created TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS items
(
    id
    BIGINT
    PRIMARY
    KEY
    GENERATED
    BY
    DEFAULT AS
    IDENTITY,
    name
    VARCHAR
(
    100
) NOT NULL,
    description VARCHAR
(
    200
),
    available BOOLEAN NOT NULL,
    owner_id BIGINT REFERENCES users
(
    id
),
    request_id BIGINT REFERENCES requests
(
    id
)
    );

CREATE TABLE IF NOT EXISTS bookings
(
    id
    BIGINT
    PRIMARY
    KEY
    GENERATED
    BY
    DEFAULT AS
    IDENTITY,
    booking_start
    TIMESTAMP,
    booking_end
    TIMESTAMP,
    item_id
    BIGINT
    REFERENCES
    items
(
    id
) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users
(
    id
)
  ON DELETE CASCADE,
    status INT
    );

CREATE TABLE IF NOT EXISTS comments
(
    id
    BIGINT
    PRIMARY
    KEY
    GENERATED
    BY
    DEFAULT AS
    IDENTITY,
    text
    VARCHAR
(
    200
),
    item_id BIGINT REFERENCES items
(
    id
) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users
(
    id
)
  ON DELETE CASCADE,
    create_time TIMESTAMP
    );
