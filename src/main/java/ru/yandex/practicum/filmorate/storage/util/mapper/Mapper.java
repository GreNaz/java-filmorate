package ru.yandex.practicum.filmorate.storage.util.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashSet;

@UtilityClass
public class Mapper {
    public static Film filmMapper(ResultSet resultSet, int row) throws SQLException {
        int id = resultSet.getInt("film_id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        int duration = resultSet.getInt("duration");
        Mpa mpa = new Mpa(resultSet.getInt("mpa.mpa_id"),
                resultSet.getString("mpa.name"));

        return new Film(id, name, description, releaseDate, duration, mpa, new LinkedHashSet<>());
    }

    public static Genre genreMapper(ResultSet resultSet, int row) throws SQLException {
        int id = resultSet.getInt("genre_id");
        String name = resultSet.getString("name");
        return new Genre(id, name);
    }

    public static Mpa mpaMapper(ResultSet resultSet, int row) throws SQLException {
        int id = resultSet.getInt("mpa_id");
        String name = resultSet.getString("name");
        return new Mpa(id, name);
    }

    public static User userMapper(ResultSet resultSet, int row) throws SQLException {
        int id = resultSet.getInt("user_id");
        String email = resultSet.getString("email");
        String login = resultSet.getString("login");
        String name = resultSet.getString("name");
        LocalDate birthday = resultSet.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }
}
