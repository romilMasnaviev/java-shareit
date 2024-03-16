drop table if exists requests;
drop table if exists bookings;
drop table if exists items;
drop table if exists users;

create table if not exists users
(
    id
    bigint
    primary
    key
    generated
    by
    default as
    identity,
    name
    varchar
(
    100
) not null,
    email varchar
(
    100
) not null unique
    );

create table if not exists items
(
    id
    bigint
    primary
    key
    generated
    by
    default as
    identity,
    name
    varchar
(
    100
) not null,
    description varchar
(
    200
),
    available bool not null,
    owner_id bigint references users
(
    id
),
    request_id bigint
    );

create table if not exists bookings
(
    id
    bigint
    primary
    key
    generated
    by
    default as
    identity,
    start
    timestamp
    without
    time
    zone,
    "end"
    timestamp
    without
    time
    zone,
    item_id
    int
    unique
    references
    items
(
    id
) on delete cascade,
    user_id int unique references users
(
    id
)
  on delete cascade,
    status varchar
(
    15
)
    );

create table if not exists requests
(
    id
    bigint
    primary
    key
    generated
    by
    default as
    identity,
    description
    varchar
(
    200
),
    requester_id int references users
(
    id
),
    created TIMESTAMP WITHOUT TIME ZONE
    );