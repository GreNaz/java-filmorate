CREATE TABLE IF NOT EXISTS director
(
    director_id BIGINT generated by default as identity primary key,
    name     varchar(20)  not null
);

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
    user_id   BIGINT not null,
    friend_id BIGINT not null,
    PRIMARY KEY (user_id, friend_id),
    constraint fk_friendship_friend_id
        foreign key (friend_id)
            references users (user_id),
    constraint fk_friendship_user_id
        foreign key (user_id)
            references users (user_id)
);

CREATE TABLE IF NOT EXISTS films_likes
(
    film_id BIGINT not null,
    user_id BIGINT not null,
    PRIMARY KEY (user_id, film_id),
    constraint fk_films_likes_film_id
        foreign key (film_id)
            references films (film_id),
    constraint fk_films_likes_user_id
        foreign key (user_id)
            references users (user_id)
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  BIGINT not null,
    genre_id int    not null,
    PRIMARY KEY (genre_id, film_id),
    constraint fk_film_id
        foreign key (film_id)
            references films (film_id),
    constraint fk_genre_id
        foreign key (genre_id)
            references genre (genre_id)
);

CREATE TABLE IF NOT EXISTS film_director
(
    film_id  BIGINT not null,
    director_id int    not null,
    PRIMARY KEY (director_id, film_id),
    constraint fkk_film_id
        foreign key (film_id)
            references films (film_id),
    constraint fk_director_id
        foreign key (director_id)
            references director (director_id)
            ON DELETE CASCADE
);