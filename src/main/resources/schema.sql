CREATE TABLE IF NOT EXISTS genre
(
    genre_id int PRIMARY KEY,
    name     varchar(20) not null
);

CREATE TABLE IF NOT EXISTS mpa
(
    mpa_id int        not null PRIMARY KEY,
    name   varchar(6) not null
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  BIGINT generated by default as identity primary key,
    email    varchar(50) not null,
    login    varchar(20) not null,
    name     varchar(50),
    birthday date        not null
);

create unique index if not exists USER_EMAIL_UINDEX on users (email);
create unique index if not exists USER_LOGIN_UINDEX on users (login);

CREATE TABLE IF NOT EXISTS films
(
    film_id      BIGINT generated by default as identity primary key,
    name         varchar(100) not null,
    description  varchar(200) not null,
    release_date date         not null,
    duration     int          not null,
    mpa_id       int          not null,
    CONSTRAINT fk_mpa_id
        FOREIGN KEY (mpa_id)
            REFERENCES mpa (mpa_id)
);

CREATE TABLE IF NOT EXISTS friendship
(
    user_id   BIGINT not null references users (user_id),
    friend_id BIGINT not null references users (user_id),
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS films_likes
(
    film_id BIGINT not null references films (film_id),
    user_id BIGINT not null references users (user_id),
    PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  BIGINT not null references films (film_id),
    genre_id int    not null references genre (genre_id),
    PRIMARY KEY (genre_id, film_id)
);