package ru.yandex.practicum.filmorate.helper;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class TestDataHelper {
    public static Film FILM = new Film(1,
            "Film",
            "good film",
            LocalDate.of(2020, 5, 5), 120,
            new Mpa(1, "G"),
            null);
    public static Film FILM_2 = new Film(2,
            "2 Film",
            "good film 2",
            LocalDate.of(2019, 5, 5), 111,
            new Mpa(2, "PG"), null);
    public static User USER = new User(1,
            "test@test.com",
            "login",
            "name",
            LocalDate.of(1995, 5, 5));

    public static User USER_2 = new User(2,
            "new@test.ru",
            "new_login",
            "name",
            LocalDate.of(1990, 5, 6));
    public static User USER_3 = new User(4,
            "dawd@test.ru",
            "fawfs",
            "greag",
            LocalDate.of(1990, 5, 6));
    public static final String CHECK_GENRES = "[{\"id\":1,\"name\":\"Комедия\"},{\"id\":2,\"name\":\"Драма\"}," +
            "{\"id\":3,\"name\":\"Мультфильм\"},{\"id\":4,\"name\":\"Триллер\"}," +
            "{\"id\":5,\"name\":\"Документальный\"},{\"id\":6,\"name\":\"Боевик\"}]";

    public static final String CHECK_MPA = "[{\"id\":1,\"name\":\"G\"},{\"id\":2,\"name\":\"PG\"},{\"id\":3,\"name\":\"PG-13\"}," +
            "{\"id\":4,\"name\":\"R\"},{\"id\":5,\"name\":\"NC-17\"}]";

}
